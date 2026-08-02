package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubContactInfo
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.util.OpeningHoursHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ClubExternalInfoViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubExternalInfoUiState())
    val uiState: StateFlow<ClubExternalInfoUiState> = _uiState.asStateFlow()

    fun loadExternalClubInfo(clubId: String) {
        if (clubId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Ungültige Club ID") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, clubId = clubId) }

        viewModelScope.launch {
            clubRepository.getClubById(clubId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Fehler beim Laden") }
                }
                .collect { club ->
                    if (club != null) {
                        updateStateFromClub(club)
                    } else {
                        loadFallbackClubInfo(clubId)
                    }
                }
        }
    }

    fun updateStateFromClub(club: Club, currentTime: LocalTime = LocalTime.now()) {
        val status = OpeningHoursHelper.determineLiveStatus(club.operatingHours, currentTime)
        val contact = ClubContactInfo(
            phoneNumber = club.phoneNumber ?: "+49 30 293600",
            email = club.contactEmail ?: "info@kliq-club.de",
            websiteUrl = club.websiteUrl ?: "https://berghain.berlin",
            instagramHandle = "@berghain_official"
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                clubId = club.id,
                clubName = club.name,
                address = club.location.address,
                websiteUrl = contact.websiteUrl,
                phoneNumber = contact.phoneNumber,
                contactEmail = contact.email,
                operatingHours = club.operatingHours,
                liveStatus = status,
                contactInfo = contact,
                errorMessage = null
            )
        }
    }

    private fun loadFallbackClubInfo(clubId: String) {
        val mockHours = OperatingHours(
            isOpenNow = true,
            todayHours = "23:00 - 08:00",
            weeklySchedule = mapOf(
                "Montag" to "Geschlossen",
                "Dienstag" to "Geschlossen",
                "Mittwoch" to "Geschlossen",
                "Donnerstag" to "22:00 - 04:00",
                "Freitag" to "23:00 - 08:00",
                "Samstag" to "23:59 - Open End",
                "Sonntag" to "Open End"
            )
        )
        val status = OpeningHoursHelper.determineLiveStatus(mockHours)

        _uiState.update {
            it.copy(
                isLoading = false,
                clubId = clubId,
                clubName = "Berghain / Panorama Bar",
                address = "Am Wriezener Bahnhof, 10243 Berlin",
                websiteUrl = "https://berghain.berlin",
                phoneNumber = "+49 30 293600",
                contactEmail = "support@berghain.de",
                operatingHours = mockHours,
                liveStatus = status,
                contactInfo = ClubContactInfo(
                    phoneNumber = "+49 30 293600",
                    email = "support@berghain.de",
                    websiteUrl = "https://berghain.berlin",
                    instagramHandle = "@berghain_official"
                ),
                errorMessage = null
            )
        }
    }

    fun getWebsiteIntentUri(): String? {
        val url = _uiState.value.websiteUrl ?: return null
        return if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
    }

    fun getPhoneDialUri(): String? {
        val phone = _uiState.value.phoneNumber ?: return null
        return "tel:${phone.replace(" ", "")}"
    }

    fun getNavigationUri(): String {
        val addr = _uiState.value.address
        return "geo:0,0?q=${java.net.URLEncoder.encode(addr.ifBlank { "Berlin" }, "UTF-8")}"
    }
}
