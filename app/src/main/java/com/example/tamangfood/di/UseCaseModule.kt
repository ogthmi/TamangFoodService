package com.example.tamangfood.di

import com.example.tamangfood.data.repository.SampleRepositoryImpl
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.domain.repository.SignUpRepository
import com.example.tamangfood.domain.repository.UserRepository
import com.example.tamangfood.domain.usecase.DeleteAccountUseCase
import com.example.tamangfood.domain.usecase.SampleUseCase
import com.example.tamangfood.domain.usecase.SignInUseCase
import com.example.tamangfood.domain.usecase.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun bindSampleUseCase(repo: SampleRepository): SampleUseCase{
        return SampleUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindSignInUseCase(repo: SignInRepository): SignInUseCase{
        return SignInUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindSignUpUseCase(repo: SignUpRepository): SignUpUseCase{
        return SignUpUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindDeleteAccountUseCase(repo: UserRepository): DeleteAccountUseCase {
        return DeleteAccountUseCase(repo)
    }
}