package com.kliq.app.viewmodel

import com.kliq.app.data.model.ClubEvent
import com.kliq.app.data.model.ClubOffer

data class ClubEventOfferUiState(
    val isLoading: Boolean = false,
    val events: List<ClubEvent> = emptyList(),
    val offers: List<ClubOffer> = emptyList(),
    val selectedOffer: ClubOffer? = null,
    val selectedEvent: ClubEvent? = null,
    val codeCopiedMessage: String? = null,
    val errorMessage: String? = null
)
