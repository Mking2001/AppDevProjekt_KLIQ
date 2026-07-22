package com.kliq.app.data.repository;

import com.kliq.app.data.local.dao.UserDao;
import com.kliq.app.data.remote.KliqApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class UserRepository_Factory implements Factory<UserRepository> {
  private final Provider<KliqApiService> apiServiceProvider;

  private final Provider<UserDao> userDaoProvider;

  public UserRepository_Factory(Provider<KliqApiService> apiServiceProvider,
      Provider<UserDao> userDaoProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public UserRepository get() {
    return newInstance(apiServiceProvider.get(), userDaoProvider.get());
  }

  public static UserRepository_Factory create(Provider<KliqApiService> apiServiceProvider,
      Provider<UserDao> userDaoProvider) {
    return new UserRepository_Factory(apiServiceProvider, userDaoProvider);
  }

  public static UserRepository newInstance(KliqApiService apiService, UserDao userDao) {
    return new UserRepository(apiService, userDao);
  }
}
