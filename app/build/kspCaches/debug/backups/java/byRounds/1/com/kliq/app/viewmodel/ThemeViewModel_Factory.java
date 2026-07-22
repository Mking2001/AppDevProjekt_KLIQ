package com.kliq.app.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
    "KotlinInternalInJava"
})
public final class ThemeViewModel_Factory implements Factory<ThemeViewModel> {
  @Override
  public ThemeViewModel get() {
    return newInstance();
  }

  public static ThemeViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ThemeViewModel newInstance() {
    return new ThemeViewModel();
  }

  private static final class InstanceHolder {
    private static final ThemeViewModel_Factory INSTANCE = new ThemeViewModel_Factory();
  }
}
