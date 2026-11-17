package com.digitallogs.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitallogs.data.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class ExportUiState(
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val exportPath: String? = null,
    val rowCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val repository: UsageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExportUiState())
    val state: StateFlow<ExportUiState> = _state

    private val sdf     = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            _state.value = ExportUiState(isExporting = true)
            try {
                val file = File(context.cacheDir, "digital_logs_${System.currentTimeMillis()}.csv")
                var rowCount = 0

                FileWriter(file).use { writer ->
                    writer.appendLine("date,app_name,package_name,session_start,session_end,duration_seconds")

                    // Primary source: sessions table (AccessibilityService — precise start/end)
                    val sessions = repository.getRecentSessions().first()
                    for (s in sessions) {
                        val date     = dateFmt.format(Date(s.startTime))
                        val start    = sdf.format(Date(s.startTime))
                        val end      = sdf.format(Date(s.endTime))
                        val duration = s.duration / 1000
                        writer.appendLine("$date,\"${s.appName}\",${s.packageName},$start,$end,$duration")
                        rowCount++
                    }

                    // Fallback source: app_usage table (UsageStatsManager — daily aggregates)
                    // Used when AccessibilityService was never active and sessions table is empty
                    if (rowCount == 0) {
                        val appUsage = repository.getRecentAppUsage().first()
                        for (u in appUsage) {
                            val date     = u.date
                            val start    = sdf.format(Date(u.startTime))
                            val end      = sdf.format(Date(u.endTime))
                            val duration = u.duration / 1000
                            writer.appendLine("$date,\"${u.appName}\",${u.packageName},$start,$end,$duration")
                            rowCount++
                        }
                    }
                }

                if (rowCount == 0) {
                    _state.value = ExportUiState(
                        error = "Sem dados para exportar. Usa a app durante algum tempo e tenta novamente."
                    )
                } else {
                    _state.value = ExportUiState(
                        exportSuccess = true,
                        exportPath = file.absolutePath,
                        rowCount = rowCount
                    )
                }
            } catch (e: Exception) {
                _state.value = ExportUiState(error = e.message ?: "Erro desconhecido")
            }
        }
    }

    fun shareFile(context: Context, path: String) {
        try {
            val file = File(path)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "DigitalLogs Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Usage Logs"))
        } catch (e: Exception) {
            _state.value = ExportUiState(error = e.message)
        }
    }

    fun resetState() {
        _state.value = ExportUiState()
    }
}
