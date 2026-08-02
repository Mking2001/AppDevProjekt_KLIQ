package com.kliq.app.viewmodel

import androidx.compose.ui.graphics.Color
import com.kliq.app.data.model.Gender
import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.model.OccupancyCategory
import com.kliq.app.data.model.OccupancyTrend
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.TealSecondary

/**
 * Visual representation of a segment in the High-Contrast Kliq Lila-Style gender chart bar.
 */
data class GenderBarSegment(
    val gender: Gender,
    val label: String,
    val percentage: Float,
    val formattedPercentage: String,
    val colorHex: String,
    val color: Color,
    val flexWeight: Float
)

/**
 * UI State container for Club Analytics, Live Visitor Capacity and Gender Aggregation.
 */
data class ClubAnalyticsUiState(
    val isLoading: Boolean = false,
    val clubId: String = "",
    val genderRatio: GenderRatio = GenderRatio(),
    val segments: List<GenderBarSegment> = emptyList(),
    val totalLiveVisitors: Int = 0,
    val currentCapacityPercent: Int = 0,
    val maxCapacity: Int = 1500,
    val occupancyCategory: OccupancyCategory = OccupancyCategory.SCHWACH,
    val occupancyTrend: OccupancyTrend = OccupancyTrend.STABLE,
    val isLive: Boolean = true,
    val formattedLastUpdated: String = "LIVE • Vor wenigen Sekunden",
    val errorMessage: String? = null
) {
    val occupancyRate: Float
        get() = (currentCapacityPercent / 100f).coerceIn(0f, 1f)

    val formattedCapacityPercent: String
        get() = "$currentCapacityPercent%"

    val formattedVisitorCount: String
        get() = "$totalLiveVisitors / $maxCapacity Gäste"
    companion object {
        fun createSegments(genderRatio: GenderRatio): List<GenderBarSegment> {
            if (!genderRatio.hasSufficientData) return emptyList()

            val list = mutableListOf<GenderBarSegment>()

            if (genderRatio.femalePercentage > 0f) {
                list.add(
                    GenderBarSegment(
                        gender = Gender.FEMALE,
                        label = "Weiblich",
                        percentage = genderRatio.femalePercentage,
                        formattedPercentage = genderRatio.formattedFemale,
                        colorHex = "#D946EF",
                        color = FuchsiaTertiary,
                        flexWeight = genderRatio.femalePercentage.coerceAtLeast(1f)
                    )
                )
            }

            if (genderRatio.malePercentage > 0f) {
                list.add(
                    GenderBarSegment(
                        gender = Gender.MALE,
                        label = "Männlich",
                        percentage = genderRatio.malePercentage,
                        formattedPercentage = genderRatio.formattedMale,
                        colorHex = "#7C3AED",
                        color = PurplePrimary,
                        flexWeight = genderRatio.malePercentage.coerceAtLeast(1f)
                    )
                )
            }

            if (genderRatio.diversePercentage > 0f) {
                list.add(
                    GenderBarSegment(
                        gender = Gender.DIVERSE,
                        label = "Divers",
                        percentage = genderRatio.diversePercentage,
                        formattedPercentage = genderRatio.formattedDiverse,
                        colorHex = "#14B8A6",
                        color = TealSecondary,
                        flexWeight = genderRatio.diversePercentage.coerceAtLeast(1f)
                    )
                )
            }

            return list
        }
    }
}
