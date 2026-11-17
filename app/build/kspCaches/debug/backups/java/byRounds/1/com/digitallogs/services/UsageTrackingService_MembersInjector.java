package com.digitallogs.services;

import com.digitallogs.data.repository.UsageRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class UsageTrackingService_MembersInjector implements MembersInjector<UsageTrackingService> {
  private final Provider<UsageRepository> repositoryProvider;

  public UsageTrackingService_MembersInjector(Provider<UsageRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<UsageTrackingService> create(
      Provider<UsageRepository> repositoryProvider) {
    return new UsageTrackingService_MembersInjector(repositoryProvider);
  }

  public static MembersInjector<UsageTrackingService> create(
      javax.inject.Provider<UsageRepository> repositoryProvider) {
    return new UsageTrackingService_MembersInjector(Providers.asDaggerProvider(repositoryProvider));
  }

  @Override
  public void injectMembers(UsageTrackingService instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.digitallogs.services.UsageTrackingService.repository")
  public static void injectRepository(UsageTrackingService instance, UsageRepository repository) {
    instance.repository = repository;
  }
}
