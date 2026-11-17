package com.digitallogs.ui.viewmodel;

import android.content.Context;
import com.digitallogs.analytics.UsageAnalyticsManager;
import com.digitallogs.data.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<UsageRepository> repositoryProvider;

  private final Provider<UsageAnalyticsManager> analyticsManagerProvider;

  public MainViewModel_Factory(Provider<Context> contextProvider,
      Provider<UsageRepository> repositoryProvider,
      Provider<UsageAnalyticsManager> analyticsManagerProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
    this.analyticsManagerProvider = analyticsManagerProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(contextProvider.get(), repositoryProvider.get(), analyticsManagerProvider.get());
  }

  public static MainViewModel_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<UsageRepository> repositoryProvider,
      javax.inject.Provider<UsageAnalyticsManager> analyticsManagerProvider) {
    return new MainViewModel_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(repositoryProvider), Providers.asDaggerProvider(analyticsManagerProvider));
  }

  public static MainViewModel_Factory create(Provider<Context> contextProvider,
      Provider<UsageRepository> repositoryProvider,
      Provider<UsageAnalyticsManager> analyticsManagerProvider) {
    return new MainViewModel_Factory(contextProvider, repositoryProvider, analyticsManagerProvider);
  }

  public static MainViewModel newInstance(Context context, UsageRepository repository,
      UsageAnalyticsManager analyticsManager) {
    return new MainViewModel(context, repository, analyticsManager);
  }
}
