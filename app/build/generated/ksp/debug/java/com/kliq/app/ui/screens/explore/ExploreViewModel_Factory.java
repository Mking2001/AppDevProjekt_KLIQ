package com.kliq.app.ui.screens.explore;

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
public final class ExploreViewModel_Factory implements Factory<ExploreViewModel> {
  @Override
  public ExploreViewModel get() {
    return newInstance();
  }

  public static ExploreViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ExploreViewModel newInstance() {
    return new ExploreViewModel();
  }

  private static final class InstanceHolder {
    private static final ExploreViewModel_Factory INSTANCE = new ExploreViewModel_Factory();
  }
}
