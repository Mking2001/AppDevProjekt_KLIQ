package com.kliq.app.util

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Location
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.domain.usecase.GetClubsWithDistanceUseCase
import com.kliq.app.ui.screens.map.MapViewModel
import com.kliq.app.viewmodel.LocationTrackingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.lang.reflect.Modifier

/**
 * Unit-Tests zur Verifikation des Architektur-Refactorings (Kapitel 9.5),
 * der UseCase-Modularisierung und der schreibgeschützten StateFlow-Kapselung in ViewModels.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ArchitectureRefactoringUnitTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Prüft, dass der [GetClubsWithDistanceUseCase] Entfernungen korrekt berechnet
     * und Kategorie-Filter ("Clubs", "Bars", "Events") im Domain-Layer anwendet.
     */
    @Test
    fun testGetClubsWithDistanceUseCase_calculatesDistancesAndFilters() = kotlinx.coroutines.test.runTest {
        val mockClubRepo = mock(ClubRepository::class.java)

        val testClubs = listOf(
            Club(
                id = "c1",
                name = "Berghain",
                category = "Club",
                averageRating = 4.9,
                location = Location(latitude = 52.5112, longitude = 13.4430, address = "Am Wriezener Bahnhof")
            ),
            Club(
                id = "c2",
                name = "Sunset Bar",
                category = "Bar",
                averageRating = 4.7,
                location = Location(latitude = 52.5280, longitude = 13.4100, address = "Torstraße 140")
            )
        )

        `when`(mockClubRepo.getAllClubs()).thenReturn(flowOf(testClubs))

        val useCase = GetClubsWithDistanceUseCase(mockClubRepo)

        // 1. Abfrage aller Venues
        val allResult = useCase(userLat = 52.5200, userLng = 13.4050, filterCategory = "Alle").first()
        assertEquals(2, allResult.size)
        assertNotNull(allResult.find { it.name == "Berghain" }?.distance)

        // 2. Abfrage nur "Clubs"
        val clubsResult = useCase(userLat = 52.5200, userLng = 13.4050, filterCategory = "Clubs").first()
        assertEquals(1, clubsResult.size)
        assertEquals("Berghain", clubsResult.first().name)
    }

    /**
     * Prüft via Reflektion, dass ViewModels (wie [MapViewModel] und [LocationTrackingViewModel])
     * ihre internen MutableStateFlows als `private` kapseln und nach außen ausschließlich
     * schreibgeschützte `StateFlow` Interfaces bereitstellen.
     */
    @Test
    fun testViewModelStateFlowEncapsulation_readOnlyStreams() {
        val mapViewModelClass = MapViewModel::class.java
        val locationViewModelClass = LocationTrackingViewModel::class.java

        // Prüfe, dass `_uiState` in MapViewModel private ist
        val mapPrivateField = mapViewModelClass.getDeclaredField("_uiState")
        assertTrue(Modifier.isPrivate(mapPrivateField.modifiers))

        // Prüfe, dass `uiState` schreibgeschützt ist (StateFlow)
        val mapPublicField = mapViewModelClass.getDeclaredField("uiState")
        assertEquals(kotlinx.coroutines.flow.StateFlow::class.java, mapPublicField.type)

        // Prüfe `_uiState` in LocationTrackingViewModel
        val locationPrivateField = locationViewModelClass.getDeclaredField("_uiState")
        assertTrue(Modifier.isPrivate(locationPrivateField.modifiers))
    }
}
