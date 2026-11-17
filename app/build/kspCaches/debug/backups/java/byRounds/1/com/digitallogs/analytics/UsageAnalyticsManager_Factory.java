package com.digitallogs.analytics;

import android.content.Context;
import com.digitallogs.data.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class UsageAnalyticsManager_Factory implements Factory<UsageAnalyticsManager> {
  private final Provider<Context> contextProvider;

  private final Provider<UsageRepository> repositoryProvider;

  public UsageAnalyticsManager_Factory(Provider<Context> contextProvider,
      Provider<UsageRepository> repositoryProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UsageAnalyticsManager get() {
    return newInstance(contextProvider.get(), repositoryProvider.get());
  }

  public static UsageAnalyticsManager_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<UsageRepository> repositoryProvider) {
    return new UsageAnalyticsManager_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(repositoryProvider));
  }

  public static UsageAnalyticsManager_Factory create(Provider<Context> contextProvider,
      Provider<UsageRepository> repositoryProvider) {
    return new UsageAnalyticsManager_Factory(contextProvider, repositoryProvider);
  }

  public static UsageAnalyticsManager newInstance(Context context, UsageRepository repository) {
    return new UsageAnalyticsManager(context, repository);
  }
}
