package com.kliq.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumptionHabitsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsumptionHabitsUiState())
    val uiState: StateFlow<ConsumptionHabitsUiState> = _uiState.asStateFlow()

    fun selectSmokingHabit(habit: SmokingHabit) {
        _uiState.update { currentState ->
            val updatedSmoking = if (currentState.selectedSmokingHabit == habit) null else habit
            val isValid = updatedSmoking != null && currentState.selectedDrinkingHabit != null
            currentState.copy(
                selectedSmokingHabit = updatedSmoking,
                isSelectionValid = isValid,
                errorMessage = null
            )
        }
    }

    fun selectDrinkingHabit(habit: DrinkingHabit) {
        _uiState.update { currentState ->
            val updatedDrinking = if (currentState.selectedDrinkingHabit == habit) null else habit
            val isValid = currentState.selectedSmokingHabit != null && updatedDrinking != null
            currentState.copy(
                selectedDrinkingHabit = updatedDrinking,
                isSelectionValid = isValid,
                errorMessage = null
            )
        }
    }

    fun saveConsumptionHabits(userId: String = "current_user") {
        val currentSmoking = _uiState.value.selectedSmokingHabit
        val currentDrinking = _uiState.value.selectedDrinkingHabit

        if (currentSmoking == null || currentDrinking == null) {
            _uiState.update {
                it.copy(errorMessage = "Bitte wähle für beide Kategorien eine Option aus.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                userRepository.saveConsumptionHabits(userId, currentSmoking, currentDrinking)
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
                        errorMessage = e.localizedMessage ?: "Fehler beim Speichern der Konsum-Gewohnheiten."
                    )
                }
            }
        }
    }
}
