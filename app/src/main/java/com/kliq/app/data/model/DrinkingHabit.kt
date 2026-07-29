package com.kliq.app.data.model

enum class DrinkingHabit(
    val title: String,
    val description: String
) {
    NEVER(
        title = "Nie",
        description = "Ich trinke keinen Alkohol"
    ),
    SOCIAL(
        title = "Gesellschaftlich",
        description = "Ab und zu in Gesellschaft oder bei Events"
    ),
    FREQUENTLY(
        title = "Oft",
        description = "Gerne und regelmäßig beim Ausgehen"
    );

    companion object {
        fun fromString(value: String?): DrinkingHabit {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: NEVER
        }
    }
}
