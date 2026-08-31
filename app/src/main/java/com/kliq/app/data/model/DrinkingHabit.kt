package com.kliq.app.data.model

enum class DrinkingHabit(
    val title: String,
    val description: String
) {
    NEVER(
        title = "Nicht Trinker",
        description = "Ich trinke keinen Alkohol"
    ),
    SOCIAL(
        title = "Genuss Trinker",
        description = "Ab und zu ein Drink zum Genuss"
    ),
    FREQUENTLY(
        title = "Säufer",
        description = "Gerne und viel beim Feiern"
    );

    companion object {
        fun fromString(value: String?): DrinkingHabit {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: NEVER
        }
    }
}
