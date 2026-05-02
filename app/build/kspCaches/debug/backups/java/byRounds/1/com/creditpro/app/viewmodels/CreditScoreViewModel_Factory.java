package com.creditpro.app.viewmodels;

import com.creditpro.app.repository.CreditProRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "KotlinInternalInJava",
    "cast"
})
public final class CreditScoreViewModel_Factory implements Factory<CreditScoreViewModel> {
  private final Provider<CreditProRepository> repoProvider;

  public CreditScoreViewModel_Factory(Provider<CreditProRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public CreditScoreViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static CreditScoreViewModel_Factory create(Provider<CreditProRepository> repoProvider) {
    return new CreditScoreViewModel_Factory(repoProvider);
  }

  public static CreditScoreViewModel newInstance(CreditProRepository repo) {
    return new CreditScoreViewModel(repo);
  }
}
