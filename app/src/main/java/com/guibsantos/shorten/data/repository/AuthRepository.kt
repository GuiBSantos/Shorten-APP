package com.guibsantos.shorten.data.repository

import com.guibsantos.shorten.data.local.TokenManager
import com.guibsantos.shorten.data.model.ChangePasswordRequest
import com.guibsantos.shorten.data.model.DeleteAccountRequest
import com.guibsantos.shorten.data.model.ForgotPasswordRequest
import com.guibsantos.shorten.data.model.GoogleLoginRequest
import com.guibsantos.shorten.data.model.GoogleLoginResponse
import com.guibsantos.shorten.data.model.LoginRequest
import com.guibsantos.shorten.data.model.RegisterRequest
import com.guibsantos.shorten.data.model.ResetPasswordRequest
import com.guibsantos.shorten.data.model.UpdateUsernameRequest
import com.guibsantos.shorten.data.model.ValidateCodeRequest
import com.guibsantos.shorten.data.network.RetrofitClient
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val tokenManager: TokenManager) {

    private val api = RetrofitClient(tokenManager).apiService

    suspend fun login(request: LoginRequest) = api.login(request)

    suspend fun register(request: RegisterRequest) = api.register(request)

    suspend fun getUserProfile() = api.getUserProfile()

    suspend fun checkUsernameAvailability(username: String): Result<Boolean> = try {
        val response = api.checkUsername(username)
        if (response.isSuccessful && response.body() != null)
            Result.success(response.body()!!)
        else
            Result.failure(Exception("Erro ao verificar username"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun checkEmailAvailability(email: String): Result<Boolean> = try {
        val response = api.checkEmail(email)
        if (response.isSuccessful && response.body() != null)
            Result.success(response.body()!!)
        else
            Result.failure(Exception("Erro ao verificar email"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun changePassword(request: ChangePasswordRequest) = api.changePassword(request)

    suspend fun uploadAvatar(body: MultipartBody.Part) = api.uploadAvatar(body)

    suspend fun forgotPassword(email: String) = api.forgotPassword(ForgotPasswordRequest(email))

    suspend fun resetPassword(email: String, code: String, newPass: String) =
        api.resetPassword(ResetPasswordRequest(email, code, newPass))

    suspend fun validateCode(email: String, code: String) =
        api.validateCode(ValidateCodeRequest(email, code))

    suspend fun updateUsername(newUsername: String) =
        api.updateUsername(UpdateUsernameRequest(newUsername))

    suspend fun deleteAccount(password: String) =
        api.deleteAccount(DeleteAccountRequest(password))

    suspend fun googleLogin(token: String): Response<GoogleLoginResponse> =
        api.loginWithGoogle(GoogleLoginRequest(token))

    fun saveToken(token: String) = tokenManager.saveToken(token)
    fun getToken(): String? = tokenManager.getToken()
    fun clearToken() = tokenManager.clearToken()
    fun saveRememberMe(value: Boolean) = tokenManager.saveRememberMe(value)
}