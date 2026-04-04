package com.example.tamangfood.di

import com.example.tamangfood.domain.repository.AddressRepository
import com.example.tamangfood.domain.repository.ChangePasswordRepository
import com.example.tamangfood.domain.repository.OtpRepository
import com.example.tamangfood.domain.repository.ResetPasswordRepository
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.domain.repository.SignUpRepository
import com.example.tamangfood.domain.repository.UserRepository
import com.example.tamangfood.domain.usecase.AddAddressUseCase
import com.example.tamangfood.domain.usecase.ChangePasswordUseCase
import com.example.tamangfood.domain.usecase.DeleteAccountUseCase
import com.example.tamangfood.domain.usecase.DeleteAddressUseCase
import com.example.tamangfood.domain.usecase.GetAddressByIdUseCase
import com.example.tamangfood.domain.usecase.GetUserAddressUseCase
import com.example.tamangfood.domain.usecase.GetUserProfileUserCase
import com.example.tamangfood.domain.usecase.OtpUseCase
import com.example.tamangfood.domain.usecase.ResetPasswordUseCase
import com.example.tamangfood.domain.usecase.SampleUseCase
import com.example.tamangfood.domain.usecase.SignInUseCase
import com.example.tamangfood.domain.usecase.SignUpUseCase
import com.example.tamangfood.domain.usecase.UpdateAddressUseCase
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
    fun bindSampleUseCase(repo: SampleRepository): SampleUseCase {
        return SampleUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindSignInUseCase(repo: SignInRepository): SignInUseCase {
        return SignInUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindSignUpUseCase(repo: SignUpRepository): SignUpUseCase {
        return SignUpUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetUserProfileUseCase(repo: UserRepository): GetUserProfileUserCase {
        return GetUserProfileUserCase(repo)
    }

    @Provides
    @Singleton
    fun bindDeleteAccountUseCase(repo: UserRepository): DeleteAccountUseCase {
        return DeleteAccountUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindOtpUseCase(repo: OtpRepository): OtpUseCase {
        return OtpUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindResetPasswordUseCase(repo: ResetPasswordRepository): ResetPasswordUseCase {
        return ResetPasswordUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindChangePasswordUseCase(repo: ChangePasswordRepository): ChangePasswordUseCase {
        return ChangePasswordUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetUserAddressesUseCase(repo: AddressRepository): GetUserAddressUseCase {
        return GetUserAddressUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetAddressByIdUseCase(repo: AddressRepository): GetAddressByIdUseCase {
        return GetAddressByIdUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindAddAddressUseCase(repo: AddressRepository): AddAddressUseCase {
        return AddAddressUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindUpdateAddressUseCase(repo: AddressRepository): UpdateAddressUseCase {
        return UpdateAddressUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindDeleteAddressUseCase(repo: AddressRepository): DeleteAddressUseCase {
        return DeleteAddressUseCase(repo)
    }
}