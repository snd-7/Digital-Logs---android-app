package com.digitallogs.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.digitallogs.data.database.DigitalLogsDatabase
import com.digitallogs.data.models.UnlockEventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Listens for screen unlock events and records them to Room.
 * Registered dynamically in MainActivity — cannot use @AndroidEntryPoint.
 */
class ScreenUnlockReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        if (intent?.action == Intent.ACTION_USER_PRESENT) {
            val today = dateFormat.format(Date())
            scope.launch {
                try {
                    val db = DigitalLogsDatabase.getInstance(context)
                    db.unlockEventDao().insertUnlockEvent(UnlockEventEntity(date = today))
                } catch (_: Exception) {}
            }
        }
    }
}
