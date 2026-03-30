package com.example.tamangfood.di

import com.example.tamangfood.data.repository.AddressRepositoryImpl
import com.example.tamangfood.data.repository.ChangePasswordRepositoryImpl
import com.example.tamangfood.data.repository.OtpRepositoryImpl
import com.example.tamangfood.data.repository.ResetPasswordRepositoryImpl
import com.example.tamangfood.data.repository.SignInRepositoryImpl
import com.example.tamangfood.data.repository.SampleRepositoryImpl
import com.example.tamangfood.data.repository.SignUpRepositoryImpl
import com.example.tamangfood.data.repository.UserRepositoryImpl
import com.example.tamangfood.domain.repository.AddressRepository
import com.example.tamangfood.domain.repository.ChangePasswordRepository
import com.example.tamangfood.domain.repository.OtpRepository
import com.example.tamangfood.domain.repository.ResetPasswordRepository
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.domain.repository.SignUpRepository
import com.example.tamangfood.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindOtpRepository(impl: OtpRepositoryImpl): OtpRepository

    @Binds
    @Singleton
    abstract fun bindResetPasswordRepository(impl: ResetPasswordRepositoryImpl): ResetPasswordRepository

    @Binds
    @Singleton
    abstract fun bindChangePasswordRepository(impl: ChangePasswordRepositoryImpl): ChangePasswordRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository
}