package com.kliq.app.data.model

import androidx.compose.ui.graphics.Color
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.TealSecondary

enum class OccupancyCategory(
    val displayName: String,
    val description: String,
    val colorHex: String,
    val color: Color
) {
    SCHWACH(
        displayName = "Schwach",
        description = "Geringe Auslastung & kurze Wartezeiten",
        colorHex = "#14B8A6",
        color = TealSecondary
    ),
    MITTEL(
        displayName = "Mittel",
        description = "Gute Auslastung & angenehme Stimmung",
        colorHex = "#7C3AED",
        color = PurplePrimary
    ),
    VOLL(
        displayName = "Voll",
        description = "Hohe Auslastung, Einlassverzögerungen möglich",
        colorHex = "#D946EF",
        color = FuchsiaTertiary
    );

    companion object {
        fun fromPercentage(percentage: Int): OccupancyCategory {
            val clamped = percentage.coerceIn(0, 100)
            return when {
                clamped < 40 -> SCHWACH
                clamped in 40..75 -> MITTEL
                else -> VOLL
            }
        }
    }
}

enum class OccupancyTrend(
    val label: String,
    val symbol: String
) {
    RISING(label = "Steigend", symbol = "▲"),
    STABLE(label = "Konstant", symbol = "►"),
    FALLING(label = "Fallend", symbol = "▼")
}
