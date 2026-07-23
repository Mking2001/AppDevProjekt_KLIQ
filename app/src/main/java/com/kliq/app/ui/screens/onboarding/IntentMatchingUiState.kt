package com.kliq.app.ui.screens.onboarding

import com.kliq.app.data.model.SearchIntent

/**
 * State representing the user's intent selection in the onboarding flow.
 */
data class IntentMatchingUiState(
    val selectedIntent: SearchIntent? = null,
    val isSelectionValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
