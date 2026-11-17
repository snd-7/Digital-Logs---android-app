package com.digitallogs.di;

import com.digitallogs.data.database.AppUsageDao;
import com.digitallogs.data.database.DigitalLogsDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideAppUsageDaoFactory implements Factory<AppUsageDao> {
  private final Provider<DigitalLogsDatabase> dbProvider;

  public AppModule_ProvideAppUsageDaoFactory(Provider<DigitalLogsDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AppUsageDao get() {
    return provideAppUsageDao(dbProvider.get());
  }

  public static AppModule_ProvideAppUsageDaoFactory create(
      javax.inject.Provider<DigitalLogsDatabase> dbProvider) {
    return new AppModule_ProvideAppUsageDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static AppModule_ProvideAppUsageDaoFactory create(
      Provider<DigitalLogsDatabase> dbProvider) {
    return new AppModule_ProvideAppUsageDaoFactory(dbProvider);
  }

  public static AppUsageDao provideAppUsageDao(DigitalLogsDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAppUsageDao(db));
  }
}
