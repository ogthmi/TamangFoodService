package com.example.tamangfood.di

import com.example.tamangfood.data.repository.SignInRepositoryImpl
import com.example.tamangfood.data.repository.SampleRepositoryImpl
import com.example.tamangfood.data.repository.SignUpRepositoryImpl
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.domain.repository.SignUpRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSampleRepository(impl: SampleRepositoryImpl): SampleRepository

    @Binds
    @Singleton
    abstract fun bindSignInRepository(impl: SignInRepositoryImpl): SignInRepository

    @Binds
    @Singleton
    abstract fun bindSignUpRepository(impl: SignUpRepositoryImpl): SignUpRepository
}