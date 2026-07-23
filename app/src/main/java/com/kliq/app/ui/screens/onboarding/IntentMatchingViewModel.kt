package com.kliq.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntentMatchingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IntentMatchingUiState())
    val uiState: StateFlow<IntentMatchingUiState> = _uiState.asStateFlow()

    fun selectIntent(intent: SearchIntent) {
        _uiState.update { currentState ->
            val updatedIntent = if (currentState.selectedIntent == intent) null else intent
            currentState.copy(
                selectedIntent = updatedIntent,
                isSelectionValid = updatedIntent != null,
                errorMessage = null
            )
        }
    }

    fun saveIntent(userId: String = "current_user") {
        val currentIntent = _uiState.value.selectedIntent
        if (currentIntent == null) {
            _uiState.update {
                it.copy(errorMessage = "Bitte wähle mindestens eine Option aus, um fortzufahren.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                userRepository.saveSearchIntent(userId, currentIntent)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Fehler beim Speichern der Präferenzen."
                    )
                }
            }
        }
    }
}
