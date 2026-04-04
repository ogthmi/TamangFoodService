package com.example.tamangfood.data.api

import com.example.tamangfood.data.model.address.AddAddressRequest
import com.example.tamangfood.data.model.address.AddAddressResponse
import com.example.tamangfood.data.model.address.AddressDetailResponse
import com.example.tamangfood.data.model.address.UserAddressesResponse
import com.example.tamangfood.data.model.auth.forgotpassword.RequestOtpResponse
import com.example.tamangfood.data.model.auth.forgotpassword.ResetPasswordRequest
import com.example.tamangfood.data.model.auth.forgotpassword.ResetPasswordResponse
import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.data.model.auth.signin.SignInResponse
import com.example.tamangfood.data.model.auth.signup.SignUpRequest
import com.example.tamangfood.data.model.auth.signup.SignUpResponse
import com.example.tamangfood.data.model.category.CategoryDetailsResponse
import com.example.tamangfood.data.model.category.CategoryResponse
import com.example.tamangfood.data.model.sample.SampleRequest
import com.example.tamangfood.data.model.sample.SampleResponse
import com.example.tamangfood.data.model.user.changepassword.ChangePasswordRequest
import com.example.tamangfood.data.model.user.changepassword.ChangePasswordResponse
import com.example.tamangfood.data.model.user.profile.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.PUT

interface ApiService {
    // Sample
    @POST("api/v1/...")
    suspend fun sample(
        @Body request: SampleRequest
    ): SampleResponse

    // Sign up
    @POST("api/v1/auth/sign-up")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    // Sign in
    @POST("api/v1/auth/log-in")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<SignInResponse>

    @POST("api/v1/auth/OTP/{email}")
    suspend fun requestOTP(
        @Path(value = "email") email: String,
    ): Response<RequestOtpResponse>

    @POST("api/v1/auth/forgot-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @GET("api/v1/users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): Response<UserProfileResponse>

    @DELETE("api/v1/users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: Int
    ): Response<Void>

    @Multipart
    @POST("api/v1/users/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: Int,
        @Part("fullName") fullName: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<UserProfileResponse>

    // Lấy danh sách address theo user
    @GET("api/v1/addresses/user")
    suspend fun getUserAddresses(): Response<UserAddressesResponse>

    // Lấy một địa chỉ theo id
    @GET("api/v1/addresses/{addressId}")
    suspend fun getAddressById(
        @Path("addressId") addressId: Int
    ): Response<AddressDetailResponse>

    // Thêm address
    @POST("api/v1/addresses")
    suspend fun addAddress(
        @Body body: AddAddressRequest
    ): Response<AddAddressResponse>

    // Cập nhật address
    @PUT("api/v1/addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: Int,
        @Body body: AddAddressRequest
    ): Response<AddAddressResponse>

    // Xóa address
    @DELETE("api/v1/addresses/{addressId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId: Int
    ): Response<AddAddressResponse>

    // Lấy danh sách category
    @GET("api/v1/category")
    @Headers("Accept: application/hal+json")
    suspend fun getCategories(): Response<CategoryResponse>

    // Lấy danh sách categroryDetail của category
    @GET("api/v1/category/details/{categoryId}")
    @Headers("Accept: application/hal+json")
    suspend fun getCategoryDetails(
        @Path("categoryId") categoryId: Int
    ): Response<CategoryDetailsResponse>
}