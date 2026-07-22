package com.kliq.app.di;

import com.kliq.app.data.remote.KliqApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
    "KotlinInternalInJava"
})
public final class AppModule_ProvideApiServiceFactory implements Factory<KliqApiService> {
  @Override
  public KliqApiService get() {
    return provideApiService();
  }

  public static AppModule_ProvideApiServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static KliqApiService provideApiService() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideApiService());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideApiServiceFactory INSTANCE = new AppModule_ProvideApiServiceFactory();
  }
}
