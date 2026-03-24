package com.example.shortenerapp.domain.usecase

import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.data.repository.UrlRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: UrlRepository
) {
    suspend operator fun invoke(): Result<List<HistoricoItem>> {
        return repository.getUserHistory().map { lista ->
            lista.map {
                item ->
                HistoricoItem(
                    shortCode = item.shortCode,
                    original = item.url,
                    encurtada = item.shortUrl,
                    dataExpiracao = formatarData(item.expiresAt)
                )
            }
        }
    }

    private fun formatarData(dataRow: Any?): String {
        if (dataRow == null) return "Sem validade"
        val stringData = dataRow.toString()
        if (stringData.startsWith("[")) return "data definida"
        return stringData.replace("T", " ").take(16)
    }
}