package com.kliq.app.data.model

data class CountryCodeItem(
    val code: String,
    val countryName: String,
    val flag: String
)

object CountryCodes {
    val list = listOf(
        CountryCodeItem("+43", "Österreich", "🇦🇹"),
        CountryCodeItem("+49", "Deutschland", "🇩🇪"),
        CountryCodeItem("+41", "Schweiz", "🇨🇭"),
        CountryCodeItem("+39", "Italien", "🇮🇹"),
        CountryCodeItem("+386", "Slowenien", "🇸🇮"),
        CountryCodeItem("+36", "Ungarn", "🇭🇺"),
        CountryCodeItem("+421", "Slowakei", "🇸🇰"),
        CountryCodeItem("+420", "Tschechien", "🇨🇿"),
        CountryCodeItem("+423", "Liechtenstein", "🇱🇮"),
        CountryCodeItem("+44", "Großbritannien", "🇬🇧"),
        CountryCodeItem("+1", "USA / Kanada", "🇺🇸")
    )

    fun getFlag(code: String): String {
        return list.firstOrNull { it.code == code }?.flag ?: "🇦🇹"
    }
}
