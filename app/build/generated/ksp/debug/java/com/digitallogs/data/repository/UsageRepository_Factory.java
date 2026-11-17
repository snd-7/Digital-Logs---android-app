package com.digitallogs.data.repository;

import android.content.Context;
import com.digitallogs.data.database.AppUsageDao;
import com.digitallogs.data.database.SessionDao;
import com.digitallogs.data.database.UnlockEventDao;
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
public final class UsageRepository_Factory implements Factory<UsageRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<AppUsageDao> appUsageDaoProvider;

  private final Provider<SessionDao> sessionDaoProvider;

  private final Provider<UnlockEventDao> unlockEventDaoProvider;

  public UsageRepository_Factory(Provider<Context> contextProvider,
      Provider<AppUsageDao> appUsageDaoProvider, Provider<SessionDao> sessionDaoProvider,
      Provider<UnlockEventDao> unlockEventDaoProvider) {
    this.contextProvider = contextProvider;
    this.appUsageDaoProvider = appUsageDaoProvider;
    this.sessionDaoProvider = sessionDaoProvider;
    this.unlockEventDaoProvider = unlockEventDaoProvider;
  }

  @Override
  public UsageRepository get() {
    return newInstance(contextProvider.get(), appUsageDaoProvider.get(), sessionDaoProvider.get(), unlockEventDaoProvider.get());
  }

  public static UsageRepository_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<AppUsageDao> appUsageDaoProvider,
      javax.inject.Provider<SessionDao> sessionDaoProvider,
      javax.inject.Provider<UnlockEventDao> unlockEventDaoProvider) {
    return new UsageRepository_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(appUsageDaoProvider), Providers.asDaggerProvider(sessionDaoProvider), Providers.asDaggerProvider(unlockEventDaoProvider));
  }

  public static UsageRepository_Factory create(Provider<Context> contextProvider,
      Provider<AppUsageDao> appUsageDaoProvider, Provider<SessionDao> sessionDaoProvider,
      Provider<UnlockEventDao> unlockEventDaoProvider) {
    return new UsageRepository_Factory(contextProvider, appUsageDaoProvider, sessionDaoProvider, unlockEventDaoProvider);
  }

  public static UsageRepository newInstance(Context context, AppUsageDao appUsageDao,
      SessionDao sessionDao, UnlockEventDao unlockEventDao) {
    return new UsageRepository(context, appUsageDao, sessionDao, unlockEventDao);
  }
}
