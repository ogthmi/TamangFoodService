package com.example.tamangfood.di

import com.example.tamangfood.domain.repository.AddressRepository
import com.example.tamangfood.domain.repository.CategoryRepository
import com.example.tamangfood.domain.repository.CartRepository
import com.example.tamangfood.domain.repository.FoodRepository
import com.example.tamangfood.domain.repository.ChangePasswordRepository
import com.example.tamangfood.domain.repository.OtpRepository
import com.example.tamangfood.domain.repository.OrderRepository
import com.example.tamangfood.domain.repository.PaymentRepository
import com.example.tamangfood.domain.repository.ResetPasswordRepository
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.domain.repository.SignUpRepository
import com.example.tamangfood.domain.repository.UserRepository
import com.example.tamangfood.domain.usecase.AddAddressUseCase
import com.example.tamangfood.domain.usecase.AddCartItemUseCase
import com.example.tamangfood.domain.usecase.AddFoodToFavoriteUseCase
import com.example.tamangfood.domain.usecase.ChangePasswordUseCase
import com.example.tamangfood.domain.usecase.CreatePaymentMethodUseCase
import com.example.tamangfood.domain.usecase.CreatePaymentIntentUseCase
import com.example.tamangfood.domain.usecase.CreateOrderUseCase
import com.example.tamangfood.domain.usecase.DeleteAccountUseCase
import com.example.tamangfood.domain.usecase.DeleteAddressUseCase
import com.example.tamangfood.domain.usecase.DeleteCartItemUseCase
import com.example.tamangfood.domain.usecase.DeleteFoodFromFavoriteUseCase
import com.example.tamangfood.domain.usecase.DeletePaymentMethodUseCase
import com.example.tamangfood.domain.usecase.GetAddressByIdUseCase
import com.example.tamangfood.domain.usecase.GetCategoriesUseCase
import com.example.tamangfood.domain.usecase.GetCartItemsUseCase
import com.example.tamangfood.domain.usecase.GetCategoryDetailsUseCase
import com.example.tamangfood.domain.usecase.GetFoodCommentsUseCase
import com.example.tamangfood.domain.usecase.GetBestSellerFoodsUseCase
import com.example.tamangfood.domain.usecase.GetFavoriteFoodsUseCase
import com.example.tamangfood.domain.usecase.GetOrdersByStatusUseCase
import com.example.tamangfood.domain.usecase.GetRecommendedFoodsUseCase
import com.example.tamangfood.domain.usecase.GetFoodDetailUseCase
import com.example.tamangfood.domain.usecase.GetFoodsByCategoryUseCase
import com.example.tamangfood.domain.usecase.GetPaymentMethodsUseCase
import com.example.tamangfood.domain.usecase.GetUserAddressUseCase
import com.example.tamangfood.domain.usecase.GetUserProfileUserCase
import com.example.tamangfood.domain.usecase.OtpUseCase
import com.example.tamangfood.domain.usecase.ResetPasswordUseCase
import com.example.tamangfood.domain.usecase.SampleUseCase
import com.example.tamangfood.domain.usecase.SignInUseCase
import com.example.tamangfood.domain.usecase.SignUpUseCase
import com.example.tamangfood.domain.usecase.UpdateAddressUseCase
import com.example.tamangfood.domain.usecase.UpdateCartItemQuantityUseCase
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
    fun bindAddCartItemUseCase(repo: CartRepository): AddCartItemUseCase {
        return AddCartItemUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetCartItemsUseCase(repo: CartRepository): GetCartItemsUseCase {
        return GetCartItemsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindDeleteCartItemUseCase(repo: CartRepository): DeleteCartItemUseCase {
        return DeleteCartItemUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindUpdateCartItemQuantityUseCase(repo: CartRepository): UpdateCartItemQuantityUseCase {
        return UpdateCartItemQuantityUseCase(repo)
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

    @Provides
    @Singleton
    fun bindGetCategoriesUseCase(repo: CategoryRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetCategoryDetailsUseCase(repo: CategoryRepository): GetCategoryDetailsUseCase {
        return GetCategoryDetailsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetFoodsByCategoryUseCase(repo: FoodRepository): GetFoodsByCategoryUseCase {
        return GetFoodsByCategoryUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetFoodDetailUseCase(repo: FoodRepository): GetFoodDetailUseCase {
        return GetFoodDetailUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetFoodCommentsUseCase(repo: FoodRepository): GetFoodCommentsUseCase {
        return GetFoodCommentsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetRecommendedFoodsUseCase(repo: FoodRepository): GetRecommendedFoodsUseCase {
        return GetRecommendedFoodsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindAddFoodToFavoriteUseCase(repo: FoodRepository): AddFoodToFavoriteUseCase {
        return AddFoodToFavoriteUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindDeleteFoodFromFavoriteUseCase(repo: FoodRepository): DeleteFoodFromFavoriteUseCase {
        return DeleteFoodFromFavoriteUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetFavoriteFoodUseCase(repo: FoodRepository): GetFavoriteFoodsUseCase {
        return GetFavoriteFoodsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetBestSellerFoodsUseCase(repo: FoodRepository): GetBestSellerFoodsUseCase {
        return GetBestSellerFoodsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindCreatePaymentMethodUseCase(repo: PaymentRepository): CreatePaymentMethodUseCase {
        return CreatePaymentMethodUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetPaymentMethodsUseCase(repo: PaymentRepository): GetPaymentMethodsUseCase {
        return GetPaymentMethodsUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindDeletePaymentMethodsUseCase(repo: PaymentRepository): DeletePaymentMethodUseCase {
        return DeletePaymentMethodUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindCreatePaymentIntentUseCase(repo: PaymentRepository): CreatePaymentIntentUseCase {
        return CreatePaymentIntentUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindCreateOrderUseCase(repo: OrderRepository): CreateOrderUseCase {
        return CreateOrderUseCase(repo)
    }

    @Provides
    @Singleton
    fun bindGetOrdersByStatusUseCase(repo: OrderRepository): GetOrdersByStatusUseCase {
        return GetOrdersByStatusUseCase(repo)
    }
}