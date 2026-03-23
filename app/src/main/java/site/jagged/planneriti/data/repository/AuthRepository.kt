package site.jagged.planneriti.data.repository

import site.jagged.planneriti.data.local.SecureStorage
import site.jagged.planneriti.data.remote.api.AuthApi
import site.jagged.planneriti.data.remote.api.DeleteAccountRequest
import site.jagged.planneriti.data.remote.api.ForgotPasswordRequest
import site.jagged.planneriti.data.remote.api.LoginRequest
import site.jagged.planneriti.data.remote.api.SignupRequest
//import site.jagged.planneriti.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

data class User(val email: String, val isVerified: Boolean)

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val secureStorage: SecureStorage
) {
    fun isLoggedIn(): Boolean = secureStorage.getAuthToken() != null

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            secureStorage.saveAuthToken(response.accessToken)
            secureStorage.saveCredentials(email, password)
            val user = api.getMe()
            Result.success(User(user.email, user.isVerified))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, password: String, confirmPassword: String): Result<Unit> {
        return try {
            api.signup(SignupRequest(email, password, confirmPassword))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            if (!isLoggedIn()) return null
            val user = api.getMe()
            User(user.email, user.isVerified)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun refreshCurrentUser(): Result<User> {
        return try {
            if (!isLoggedIn()) {
                Result.failure(IllegalStateException("User is not logged in"))
            } else {
                val user = api.getMe()
                Result.success(User(user.email, user.isVerified))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            api.forgotPassword(ForgotPasswordRequest(email))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            api.deleteAccount(DeleteAccountRequest(password))
            logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        secureStorage.clearAuthToken()
        secureStorage.clearCredentials()
    }
}