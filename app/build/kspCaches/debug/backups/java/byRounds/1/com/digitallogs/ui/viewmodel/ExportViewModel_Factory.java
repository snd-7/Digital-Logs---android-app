package com.digitallogs.ui.viewmodel;

import com.digitallogs.data.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ExportViewModel_Factory implements Factory<ExportViewModel> {
  private final Provider<UsageRepository> repositoryProvider;

  public ExportViewModel_Factory(Provider<UsageRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ExportViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ExportViewModel_Factory create(
      javax.inject.Provider<UsageRepository> repositoryProvider) {
    return new ExportViewModel_Factory(Providers.asDaggerProvider(repositoryProvider));
  }

  public static ExportViewModel_Factory create(Provider<UsageRepository> repositoryProvider) {
    return new ExportViewModel_Factory(repositoryProvider);
  }

  public static ExportViewModel newInstance(UsageRepository repository) {
    return new ExportViewModel(repository);
  }
}
