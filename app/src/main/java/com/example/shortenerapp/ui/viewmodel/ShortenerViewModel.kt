package com.example.shortenerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.data.repository.UrlRepository
import com.example.shortenerapp.domain.usecase.GetHistoryUseCase
import com.example.shortenerapp.domain.usecase.ShortenUrlUseCase
import com.example.shortenerapp.ui.utils.ErrorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShortenerViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val shortenUrlUseCase: ShortenUrlUseCase,
    private val repository: UrlRepository
) : ViewModel() {

    private val _historico = MutableStateFlow<List<HistoricoItem>>(emptyList())
    val historico = _historico.asStateFlow()

    private val _isLoadingHistorico = MutableStateFlow(false)
    val isLoadingHistorico = _isLoadingHistorico.asStateFlow()

    private val _isLoadingEncurtar = MutableStateFlow(false)
    val isLoadingEncurtar = _isLoadingEncurtar.asStateFlow()

    private val _userAvatarUrl = MutableStateFlow<String?>(null)
    val userAvatarUrl = _userAvatarUrl.asStateFlow()

    init {
        carregarHistorico()
        carregarPerfilUsuario()
    }

    fun clearState() {
        _historico.value = emptyList()
        _userAvatarUrl.value = null
        _isLoadingEncurtar.value = false
        _isLoadingHistorico.value = false
    }

    fun carregarPerfilUsuario() {
        viewModelScope.launch {
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _userAvatarUrl.value = if (user.avatarUrl != null) "${user.avatarUrl}?t=${System.currentTimeMillis()}" else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun carregarHistorico() {
        viewModelScope.launch {
            _isLoadingHistorico.value = true
            getHistoryUseCase().onSuccess { lista ->
                _historico.value = lista
            }.onFailure { e ->
                println("Erro ao carregar histórico: ${e.message}")
            }
            _isLoadingHistorico.value = false
        }
    }

    fun encurtarUrl(
        urlDigitada: String,
        maxClicks: Int? = null,
        expirationTime: Long? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoadingEncurtar.value = true
            shortenUrlUseCase(urlDigitada, maxClicks, expirationTime)
                .onSuccess { item ->
                    _historico.value = listOf(item) + _historico.value
                    onSuccess(item.encurtada)
                }
                .onFailure { e ->
                    onError(e.message ?: "Erro desconhecido")
                }
            _isLoadingEncurtar.value = false
        }
    }

    fun deletarUrl(item: HistoricoItem, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUrl(item.shortCode)
                if (response.isSuccessful) {
                    _historico.value = _historico.value.filter { it.shortCode != item.shortCode }
                    onSuccess()
                } else {
                    onError("Erro ao deletar: ${response.code()}")
                }
            } catch (e: Exception) {
                onError(ErrorUtils.parseError(e))
            }
        }
    }
}
