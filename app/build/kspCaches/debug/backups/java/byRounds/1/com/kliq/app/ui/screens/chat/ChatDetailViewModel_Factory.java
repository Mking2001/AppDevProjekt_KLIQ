package com.kliq.app.ui.screens.chat;

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
public final class ChatDetailViewModel_Factory implements Factory<ChatDetailViewModel> {
  @Override
  public ChatDetailViewModel get() {
    return newInstance();
  }

  public static ChatDetailViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ChatDetailViewModel newInstance() {
    return new ChatDetailViewModel();
  }

  private static final class InstanceHolder {
    private static final ChatDetailViewModel_Factory INSTANCE = new ChatDetailViewModel_Factory();
  }
}
