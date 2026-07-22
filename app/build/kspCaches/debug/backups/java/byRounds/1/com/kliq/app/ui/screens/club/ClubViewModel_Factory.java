package com.kliq.app.ui.screens.club;

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
public final class ClubViewModel_Factory implements Factory<ClubViewModel> {
  @Override
  public ClubViewModel get() {
    return newInstance();
  }

  public static ClubViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ClubViewModel newInstance() {
    return new ClubViewModel();
  }

  private static final class InstanceHolder {
    private static final ClubViewModel_Factory INSTANCE = new ClubViewModel_Factory();
  }
}
