package com.guibsantos.shorten.data.model

data class HistoricoItem(
    val shortCode: String,
    val original: String,
    val encurtada: String,
    val dataExpiracao: String
)
