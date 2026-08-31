package com.kliq.app.data.model

enum class SearchIntent(
    val title: String,
    val description: String
) {
    FRIENDS(
        title = "Freunde",
        description = "Neue Leute kennenlernen & Freundschaften schließen"
    ),
    DATING(
        title = "Dating / Liebe",
        description = "Flirten, Singles treffen & Romantik entdecken"
    ),
    BOTH(
        title = "Beides",
        description = "Offen für Freundschaften und Dating-Matches"
    );

    companion object {
        fun fromString(value: String?): SearchIntent {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: BOTH
        }
    }
}
