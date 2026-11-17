package com.digitallogs.di;

import com.digitallogs.data.database.DigitalLogsDatabase;
import com.digitallogs.data.database.UnlockEventDao;
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
public final class AppModule_ProvideUnlockEventDaoFactory implements Factory<UnlockEventDao> {
  private final Provider<DigitalLogsDatabase> dbProvider;

  public AppModule_ProvideUnlockEventDaoFactory(Provider<DigitalLogsDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UnlockEventDao get() {
    return provideUnlockEventDao(dbProvider.get());
  }

  public static AppModule_ProvideUnlockEventDaoFactory create(
      javax.inject.Provider<DigitalLogsDatabase> dbProvider) {
    return new AppModule_ProvideUnlockEventDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static AppModule_ProvideUnlockEventDaoFactory create(
      Provider<DigitalLogsDatabase> dbProvider) {
    return new AppModule_ProvideUnlockEventDaoFactory(dbProvider);
  }

  public static UnlockEventDao provideUnlockEventDao(DigitalLogsDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUnlockEventDao(db));
  }
}
