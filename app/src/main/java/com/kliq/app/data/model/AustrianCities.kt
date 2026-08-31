package com.kliq.app.data.model

object AustrianCities {
    val CITIES: List<String> = listOf(
        "Wien",
        "Graz",
        "Linz",
        "Salzburg",
        "Innsbruck",
        "Klagenfurt am Wörthersee",
        "Villach",
        "Wels",
        "Sankt Pölten",
        "Dornbirn",
        "Wiener Neustadt",
        "Steyr",
        "Feldkirch",
        "Bregenz",
        "Leonding",
        "Klosterneuburg",
        "Baden",
        "Wolfsberg",
        "Leoben",
        "Krems an der Donau",
        "Traun",
        "Amstetten",
        "Lustenau",
        "Kapfenberg",
        "Mödling",
        "Hallein",
        "Kufstein",
        "Braunau am Inn",
        "Spittal an der Drau",
        "Schwechat",
        "Telfs",
        "Saalfelden am Steinernen Meer",
        "Ansfelden",
        "Hohenems",
        "Bludenz",
        "Eisenstadt",
        "Lienz",
        "Vöcklabruck",
        "Gmunden",
        "St. Veit an der Glan",
        "Völkermarkt",
        "Feldkirchen in Kärnten",
        "Hermagor",
        "Neunkirchen",
        "Korneuburg",
        "Tulln an der Donau"
    )

    fun filter(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        val trimmed = query.trim()
        return CITIES.filter { it.contains(trimmed, ignoreCase = true) }
    }

    fun isValidCity(cityName: String): Boolean {
        return CITIES.any { it.equals(cityName.trim(), ignoreCase = true) }
    }
}
