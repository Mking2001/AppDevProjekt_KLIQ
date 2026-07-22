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
public final class TopBarViewModel_Factory implements Factory<TopBarViewModel> {
  @Override
  public TopBarViewModel get() {
    return newInstance();
  }

  public static TopBarViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TopBarViewModel newInstance() {
    return new TopBarViewModel();
  }

  private static final class InstanceHolder {
    private static final TopBarViewModel_Factory INSTANCE = new TopBarViewModel_Factory();
  }
}
