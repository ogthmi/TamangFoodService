package com.example.tamangfood.di

import com.example.tamangfood.data.repository.SampleRepositoryImpl
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.domain.usecase.SampleUseCase
import dagger.Binds
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
}