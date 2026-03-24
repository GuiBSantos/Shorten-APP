package com.example.shortenerapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.local.ThemeManager
import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.model.ChangePasswordRequest
import com.example.shortenerapp.data.repository.AuthRepository
import com.example.shortenerapp.ui.utils.ErrorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _username = MutableStateFlow("Carregando...")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("...")
    val email = _email.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl = _avatarUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage = _feedbackMessage.asStateFlow()

    // Estados de UI para inputs (podem continuar como mutableStateOf para facilitar o Two-Way binding em Compose)
    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmNewPassword by mutableStateOf("")

    var showEditUsernameDialog by mutableStateOf(false)
    var showDeleteAccountDialog by mutableStateOf(false)
    var editUsernameText by mutableStateOf("")
    var deleteAccountPassword by mutableStateOf("")

    init {
        observeTheme()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            themeManager.isDarkTheme.collect { isDark -> _isDarkTheme.value = isDark }
        }
    }

    fun toggleTheme(isChecked: Boolean) {
        viewModelScope.launch { themeManager.toggleTheme(isChecked) }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun clearState() {
        _username.value = "Carregando..."
        _email.value = "..."
        _avatarUrl.value = null
        _isLoading.value = false
        _feedbackMessage.value = null
        currentPassword = ""
        newPassword = ""
        confirmNewPassword = ""
        showEditUsernameDialog = false
        showDeleteAccountDialog = false
        editUsernameText = ""
        deleteAccountPassword = ""
    }

    fun logout() {
        tokenManager.clearToken()
        clearState()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _username.value = user.username
                    _email.value = user.email ?: ""
                    _avatarUrl.value = if (user.avatarUrl != null) "${user.avatarUrl}?t=${System.currentTimeMillis()}" else null
                }
            } catch (e: Exception) {
                _feedbackMessage.value = ErrorUtils.parseError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        val file = uriToFile(context, uri)
        if (file == null) {
            _feedbackMessage.value = "Erro ao ler a imagem."
            return
        }

        val sizeInMb = file.length() / (1024 * 1024)
        if (sizeInMb > 5) {
            _feedbackMessage.value = "Imagem muito grande (Máx 5MB)."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = repository.uploadAvatar(body)
                if (response.isSuccessful && response.body() != null) {
                    val rawUrl = response.body()!!.avatarUrl
                    _avatarUrl.value = "$rawUrl?t=${System.currentTimeMillis()}"
                    _feedbackMessage.value = "Foto atualizada com sucesso!"
                } else {
                    _feedbackMessage.value = "Falha no upload."
                }
            } catch (e: Exception) {
                _feedbackMessage.value = ErrorUtils.parseError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword() {
        if (newPassword != confirmNewPassword) {
            _feedbackMessage.value = "As senhas não coincidem."
            return
        }
        if (newPassword.length < 8) {
            _feedbackMessage.value = "Mínimo de 8 caracteres."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ChangePasswordRequest(currentPassword, newPassword)
                val response = repository.changePassword(request)
                if (response.isSuccessful) {
                    _feedbackMessage.value = "Senha alterada!"
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                } else {
                    _feedbackMessage.value = "Senha atual incorreta."
                }
            } catch (e: Exception) {
                _feedbackMessage.value = ErrorUtils.parseError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val myFile = File(context.cacheDir, "temp_avatar.jpg")
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(myFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            myFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateUsername(onSuccess: () -> Unit) {
        if (editUsernameText.length < 3) {
            _feedbackMessage.value = "Mínimo 3 caracteres."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateUsername(editUsernameText)
                if (response.isSuccessful) {
                    _username.value = editUsernameText
                    _feedbackMessage.value = "Nome de usuário alterado!"
                    onSuccess()
                } else {
                    _feedbackMessage.value = "Nome já está em uso ou inválido."
                }
            } catch (e: Exception) {
                _feedbackMessage.value = ErrorUtils.parseError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteAccount(deleteAccountPassword)
                if (response.isSuccessful) {
                    tokenManager.clearToken()
                    clearState()
                    onSuccess()
                } else {
                    _feedbackMessage.value = "Senha incorreta."
                }
            } catch (e: Exception) {
                _feedbackMessage.value = ErrorUtils.parseError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
