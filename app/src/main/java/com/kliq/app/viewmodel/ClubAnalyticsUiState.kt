package com.kliq.app.viewmodel

import androidx.compose.ui.graphics.Color
import com.kliq.app.data.model.Gender
import com.kliq.app.data.model.GenderRatio
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
 * UI State container for Club Analytics and Gender Aggregation.
 */
data class ClubAnalyticsUiState(
    val isLoading: Boolean = false,
    val clubId: String = "",
    val genderRatio: GenderRatio = GenderRatio(),
    val segments: List<GenderBarSegment> = emptyList(),
    val totalLiveVisitors: Int = 0,
    val errorMessage: String? = null
) {
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
