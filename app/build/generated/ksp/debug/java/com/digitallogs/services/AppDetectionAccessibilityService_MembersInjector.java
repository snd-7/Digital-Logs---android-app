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
public final class AppDetectionAccessibilityService_MembersInjector implements MembersInjector<AppDetectionAccessibilityService> {
  private final Provider<UsageRepository> repositoryProvider;

  public AppDetectionAccessibilityService_MembersInjector(
      Provider<UsageRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<AppDetectionAccessibilityService> create(
      Provider<UsageRepository> repositoryProvider) {
    return new AppDetectionAccessibilityService_MembersInjector(repositoryProvider);
  }

  public static MembersInjector<AppDetectionAccessibilityService> create(
      javax.inject.Provider<UsageRepository> repositoryProvider) {
    return new AppDetectionAccessibilityService_MembersInjector(Providers.asDaggerProvider(repositoryProvider));
  }

  @Override
  public void injectMembers(AppDetectionAccessibilityService instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.digitallogs.services.AppDetectionAccessibilityService.repository")
  public static void injectRepository(AppDetectionAccessibilityService instance,
      UsageRepository repository) {
    instance.repository = repository;
  }
}
