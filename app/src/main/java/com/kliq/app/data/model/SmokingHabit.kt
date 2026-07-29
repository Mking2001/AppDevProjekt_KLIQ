package com.kliq.app.data.model

enum class SmokingHabit(
    val title: String,
    val description: String
) {
    NEVER(
        title = "Nie",
        description = "Ich rauche gar nicht"
    ),
    OCCASIONALLY(
        title = "Gelegentlich",
        description = "Ab und zu beim Feiern oder in Gesellschaft"
    ),
    REGULARLY(
        title = "Regelmäßig",
        description = "Regelmäßiger Raucher im Alltag"
    );

    companion object {
        fun fromString(value: String?): SmokingHabit {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: NEVER
        }
    }
}
