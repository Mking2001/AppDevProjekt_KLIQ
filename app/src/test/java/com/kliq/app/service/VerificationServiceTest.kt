package com.kliq.app.service

import com.kliq.app.data.model.ClubGeofenceState
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.model.VisitedClubHistory
import com.kliq.app.data.repository.GeofenceRepository
import com.kliq.app.data.util.AntiSpamReviewValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VerificationServiceTest {

    private lateinit var fakeGeofenceRepository: FakeGeofenceRepository
    private lateinit var antiSpamValidator: AntiSpamReviewValidator
    private lateinit var verificationService: VerificationServiceImpl

    @Before
    fun setUp() {
        fakeGeofenceRepository = FakeGeofenceRepository()
        antiSpamValidator = AntiSpamReviewValidator()
        verificationService = VerificationServiceImpl(fakeGeofenceRepository, antiSpamValidator)
    }

    @Test
    fun verifyUserProximityOrQr_whenUnverified_returnsLockedState() = runTest {
        val result = verificationService.verifyUserProximityOrQr("user1", "user2", null)

        assertFalse(result.isVerified)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, result.method)
    }

    @Test
    fun verifyUserProximityOrQr_whenValidQrToken_returnsQrVerifiedState() = runTest {
        val result = verificationService.verifyUserProximityOrQr("user1", "user2", "KLIQ_PASS_USER2")

        assertTrue(result.isVerified)
        assertEquals(ReviewVerificationMethod.QR_CODE_SCAN, result.method)
    }

    @Test
    fun verifyUserProximityOrQr_whenInsideGeofence_returnsGpsVerifiedState() = runTest {
        fakeGeofenceRepository.setActiveClubState(
            ClubGeofenceState(
                activeClubId = "club123",
                activeClubName = "Club Matrix",
                isInsideGeofence = true
            )
        )

        val result = verificationService.verifyUserProximityOrQr("user1", "user2", null)

        assertTrue(result.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, result.method)
    }

    @Test
    fun observeVerificationStatus_emitsUpdatedStateReactively() = runTest {
        val flow = verificationService.observeVerificationStatus("user1", "user2")
        var initial = flow.first()
        assertFalse(initial.isVerified)

        fakeGeofenceRepository.setActiveClubState(
            ClubGeofenceState(
                activeClubId = "club_berlin",
                activeClubName = "Berghain",
                isInsideGeofence = true
            )
        )

        val updated = flow.first()
        assertTrue(updated.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, updated.method)
    }

    private class FakeGeofenceRepository : GeofenceRepository {
        private val _activeClubState = MutableStateFlow(ClubGeofenceState())
        override val activeClubState: StateFlow<ClubGeofenceState> = _activeClubState

        private val _visitedHistory = MutableStateFlow<List<VisitedClubHistory>>(emptyList())
        override val visitedHistory: StateFlow<List<VisitedClubHistory>> = _visitedHistory

        fun setActiveClubState(state: ClubGeofenceState) {
            _activeClubState.value = state
        }

        override suspend fun handleGeofenceTransition(clubId: String, transitionType: GeofenceTransitionType) {}
        override fun isClubGeofenceVerified(clubId: String): Boolean = _activeClubState.value.isInsideGeofence
        override suspend fun getVisitedHistoryForUser(): List<VisitedClubHistory> = _visitedHistory.value
        override suspend fun updateRegisteredGeofenceCount(count: Int) {}
        override suspend fun resetGeofenceState() {}
    }
}
