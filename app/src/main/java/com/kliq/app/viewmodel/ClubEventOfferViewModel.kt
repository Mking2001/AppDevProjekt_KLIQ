package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.ClubEvent
import com.kliq.app.data.model.ClubOffer
import com.kliq.app.data.repository.ClubEventOfferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubEventOfferViewModel @Inject constructor(
    private val repository: ClubEventOfferRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubEventOfferUiState(isLoading = true))
    val uiState: StateFlow<ClubEventOfferUiState> = _uiState.asStateFlow()

    private var currentClubId: String? = null

    fun loadEventsAndOffers(clubId: String) {
        if (clubId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Ungültige Club ID") }
            return
        }

        if (currentClubId == clubId) return
        currentClubId = clubId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                repository.refreshClubEventsAndOffers(clubId)
            }

            combine(
                repository.getEventsForClub(clubId),
                repository.getOffersForClub(clubId)
            ) { events, offers ->
                Pair(events, offers)
            }.collect { (events, offers) ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        events = events,
                        offers = offers,
                        errorMessage = if (events.isEmpty() && offers.isEmpty()) "Keine Events oder Angebote verfügbar" else null
                    )
                }
            }
        }
    }

    fun selectOffer(offer: ClubOffer?) {
        _uiState.update { it.copy(selectedOffer = offer) }
    }

    fun selectEvent(event: ClubEvent?) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun onCodeCopied(code: String) {
        _uiState.update { it.copy(codeCopiedMessage = "Gutscheincode '$code' kopiert!") }
    }

    fun clearCopiedMessage() {
        _uiState.update { it.copy(codeCopiedMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
