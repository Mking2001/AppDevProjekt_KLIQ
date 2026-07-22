package com.kliq.app.ui.navigation;

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
public final class NavigationViewModel_Factory implements Factory<NavigationViewModel> {
  @Override
  public NavigationViewModel get() {
    return newInstance();
  }

  public static NavigationViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NavigationViewModel newInstance() {
    return new NavigationViewModel();
  }

  private static final class InstanceHolder {
    private static final NavigationViewModel_Factory INSTANCE = new NavigationViewModel_Factory();
  }
}
