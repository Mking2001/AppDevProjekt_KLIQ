package com.kliq.app.data.model

import kotlin.math.roundToInt

data class GenderRatio(
    val malePercentage: Float = 0f,
    val femalePercentage: Float = 0f,
    val diversePercentage: Float = 0f,
    val totalVisitorsCount: Int = 0,
    val hasSufficientData: Boolean = false
) {
    val formattedMale: String get() = "${malePercentage.roundToInt()}%"
    val formattedFemale: String get() = "${femalePercentage.roundToInt()}%"
    val formattedDiverse: String get() = "${diversePercentage.roundToInt()}%"

    companion object {
        const val MIN_PRIVACY_THRESHOLD = 5

        fun calculate(
            maleCount: Int,
            femaleCount: Int,
            diverseCount: Int
        ): GenderRatio {
            val total = maleCount + femaleCount + diverseCount
            if (total < MIN_PRIVACY_THRESHOLD) {
                return GenderRatio(
                    malePercentage = 0f,
                    femalePercentage = 0f,
                    diversePercentage = 0f,
                    totalVisitorsCount = total,
                    hasSufficientData = false
                )
            }

            val totalFloat = total.toFloat()
            val malePct = (maleCount / totalFloat) * 100f
            val femalePct = (femaleCount / totalFloat) * 100f
            val diversePct = (diverseCount / totalFloat) * 100f

            return GenderRatio(
                malePercentage = malePct,
                femalePercentage = femalePct,
                diversePercentage = diversePct,
                totalVisitorsCount = total,
                hasSufficientData = true
            )
        }
    }
}
