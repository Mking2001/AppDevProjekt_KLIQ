package com.kliq.app.domain.usecase

import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.ui.screens.map.MapClusterManager
import com.kliq.app.ui.screens.map.VenueItemUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class GetClubsWithDistanceUseCase @Inject constructor(
    private val clubRepository: ClubRepository
) {

    operator fun invoke(
        userLat: Double = 52.5200,
        userLng: Double = 13.4050,
        filterCategory: String? = null
    ): Flow<List<VenueItemUi>> {
        return clubRepository.getAllClubs().map { clubList ->
            val mappedVenues = clubList.map { club ->
                val distKm = MapClusterManager.calculateDistanceMeters(
                    userLat,
                    userLng,
                    club.location.latitude,
                    club.location.longitude
                ) / 1000.0

                val formattedDist = String.format(Locale.US, "%.1f km", distKm)

                VenueItemUi(
                    id = club.id,
                    name = club.name,
                    category = club.category.ifBlank { "Club" },
                    distance = formattedDist,
                    rating = club.averageRating.toFloat(),
                    latitude = club.location.latitude,
                    longitude = club.location.longitude,
                    address = club.location.address,
                    activeEventTitle = club.activeEvent?.title,
                    isFavorite = club.isFavorite,
                    currentCapacityPercent = club.analytics.currentCapacityPercent,
                    isOpenNow = club.operatingHours.isOpenNow,
                    totalLiveVisitors = club.analytics.totalLiveVisitors,
                    malePercentage = club.analytics.malePercentage,
                    femalePercentage = club.analytics.femalePercentage
                )
            }

            filterVenuesByCategory(mappedVenues, filterCategory)
        }
    }

    private fun filterVenuesByCategory(
        venues: List<VenueItemUi>,
        filterName: String?
    ): List<VenueItemUi> {
        if (filterName == null || filterName == "Alle") return venues

        return when (filterName) {
            "Events" -> venues.filter { it.activeEventTitle != null }
            "Clubs" -> venues.filter { it.category.contains("Club", ignoreCase = true) }
            "Bars" -> venues.filter { it.category.contains("Bar", ignoreCase = true) }
            "Restaurants" -> venues.filter { it.category.contains("Restaurant", ignoreCase = true) }
            else -> venues.filter { it.category.equals(filterName, ignoreCase = true) }
        }
    }
}
