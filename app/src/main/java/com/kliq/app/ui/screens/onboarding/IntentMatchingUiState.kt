package com.kliq.app.ui.screens.onboarding

import com.kliq.app.data.model.SearchIntent

data class IntentMatchingUiState(
    val selectedIntent: SearchIntent? = null,
    val isSelectionValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
