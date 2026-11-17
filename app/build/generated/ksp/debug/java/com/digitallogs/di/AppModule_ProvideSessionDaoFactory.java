package com.digitallogs.di;

import com.digitallogs.data.database.DigitalLogsDatabase;
import com.digitallogs.data.database.SessionDao;
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
public final class AppModule_ProvideSessionDaoFactory implements Factory<SessionDao> {
  private final Provider<DigitalLogsDatabase> dbProvider;

  public AppModule_ProvideSessionDaoFactory(Provider<DigitalLogsDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SessionDao get() {
    return provideSessionDao(dbProvider.get());
  }

  public static AppModule_ProvideSessionDaoFactory create(
      javax.inject.Provider<DigitalLogsDatabase> dbProvider) {
    return new AppModule_ProvideSessionDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static AppModule_ProvideSessionDaoFactory create(
      Provider<DigitalLogsDatabase> dbProvider) {
    return new AppModule_ProvideSessionDaoFactory(dbProvider);
  }

  public static SessionDao provideSessionDao(DigitalLogsDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSessionDao(db));
  }
}
