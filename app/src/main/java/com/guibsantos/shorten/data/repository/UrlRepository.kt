package com.guibsantos.shorten.data.repository

import com.guibsantos.shorten.data.local.TokenManager
import com.guibsantos.shorten.data.model.ShortenUrlRequest
import com.guibsantos.shorten.data.model.ShortenUrlResponse
import com.guibsantos.shorten.data.model.UserResponse
import com.guibsantos.shorten.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun shortenUrl(request: ShortenUrlRequest) = api.encurtarUrl(request)

    suspend fun deleteUrl(shortCode: String) = api.deletarUrl(shortCode)

    suspend fun getUserHistory(): Result<List<ShortenUrlResponse>> {
        return try {
            val response = api.getMyUrls()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro na API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): Response<UserResponse> {
        return api.getUserProfile()
    }
}
