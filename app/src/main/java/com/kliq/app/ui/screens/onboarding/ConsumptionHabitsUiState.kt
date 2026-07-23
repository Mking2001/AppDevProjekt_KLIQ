package com.kliq.app.ui.screens.onboarding

import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SmokingHabit

/**
 * State representing the user's consumption habit selections in onboarding.
 */
data class ConsumptionHabitsUiState(
    val selectedSmokingHabit: SmokingHabit? = null,
    val selectedDrinkingHabit: DrinkingHabit? = null,
    val isSelectionValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
