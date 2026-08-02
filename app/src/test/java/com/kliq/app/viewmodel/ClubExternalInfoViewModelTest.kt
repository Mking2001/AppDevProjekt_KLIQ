package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.OperatingHours
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class ClubExternalInfoViewModelTest {

    private lateinit var viewModel: ClubExternalInfoViewModel

    @Before
    fun setUp() {
        viewModel = ClubExternalInfoViewModel(clubRepository = null)
    }

    @Test
    fun `loadExternalClubInfo with valid id loads mock data`() {
        viewModel.loadExternalClubInfo("club_123")
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("club_123", state.clubId)
        assertTrue(state.address.isNotBlank())
        assertNotNull(state.websiteUrl)
    }

    @Test
    fun `loadExternalClubInfo with blank id sets error state`() {
        viewModel.loadExternalClubInfo("")
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("Ungültige Club ID", state.errorMessage)
    }

    @Test
    fun `updateStateFromClub correctly sets ui state and computes live status`() {
        val mockClub = Club(
            id = "test_club",
            name = "Test Club",
            location = GpsLocation(52.52, 13.40, "Alexanderplatz 1, Berlin"),
            averageRating = 4.5,
            operatingHours = OperatingHours(
                isOpenNow = true,
                todayHours = "23:00 - 06:00",
                weeklySchedule = mapOf("Freitag" to "23:00 - 06:00")
            ),
            websiteUrl = "https://testclub.de",
            phoneNumber = "+49 30 123456",
            contactEmail = "contact@testclub.de"
        )

        viewModel.updateStateFromClub(mockClub, currentTime = LocalTime.of(1, 0))
        val state = viewModel.uiState.value

        assertEquals("test_club", state.clubId)
        assertEquals("Test Club", state.clubName)
        assertEquals("https://testclub.de", state.websiteUrl)
        assertEquals("+49 30 123456", state.phoneNumber)
        assertEquals(LiveOpeningStatus.OPEN_NOW, state.liveStatus)
    }

    @Test
    fun `intent uri helpers return correctly formatted URIs`() {
        viewModel.loadExternalClubInfo("club_123")

        val webUri = viewModel.getWebsiteIntentUri()
        val phoneUri = viewModel.getPhoneDialUri()
        val navUri = viewModel.getNavigationUri()

        assertTrue(webUri?.startsWith("https://") == true)
        assertTrue(phoneUri?.startsWith("tel:") == true)
        assertTrue(navUri.startsWith("geo:0,0?q="))
    }
}
