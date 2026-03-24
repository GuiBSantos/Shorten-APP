package com.example.shortenerapp.domain.usecase

import android.util.Patterns
import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.repository.UrlRepository
import javax.inject.Inject

class ShortenUrlUseCase @Inject constructor(
    private val repository: UrlRepository
) {
    suspend operator fun invoke(
        url: String,
        maxClicks: Int?,
        expirationTime: Long?
    ): Result<HistoricoItem> {
        val urlLimpa = url.trim()
        if (urlLimpa.isEmpty()) return Result.failure(Exception("Digite uma URL válida!"))

        val urlParaValidar = if (urlLimpa.startsWith("http://") || urlLimpa.startsWith("https://"))
            urlLimpa else "https://$urlLimpa"

        if (!Patterns.WEB_URL.matcher(urlParaValidar).matches()) {
            return Result.failure(Exception("URL inválida!"))
        }

        return try {
            val request = ShortenUrlRequest(
                url = urlParaValidar,
                maxClicks = maxClicks,
                expirationTimeInMinutes = expirationTime
            )
            val response = repository.shortenUrl(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(
                    HistoricoItem(
                        shortCode = body.shortCode,
                        original = body.url,
                        encurtada = body.shortUrl,
                        dataExpiracao = body.expiresAt?.toString() ?: "Sem validade"
                    )
                )
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                Result.failure(Exception("Erro ao encurtar: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
