package site.jagged.planneriti.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(
    @SerializedName("access_token") val accessToken: String
)
data class SignupRequest(
    val email: String,
    val password: String,
    @SerializedName("confirm_password") val confirmPassword: String
)
data class UserResponse(
    val email: String,
    @SerializedName("is_verified") val isVerified: Boolean
)
data class ForgotPasswordRequest(val email: String)
data class DeleteAccountRequest(val password: String)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Map<String, String>

    @GET("auth/me")
    suspend fun getMe(): UserResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest)

    @POST("auth/delete-account/initiate")
    suspend fun deleteAccount(@Body request: DeleteAccountRequest)
}