package com.kliq.app.ui.screens.auth

/**
 * Repräsentiert die Auswahloption für eine Ländervorwahl.
 *
 * @property countryName Deutscher Name des Landes.
 * @property flagEmoji Flaggen-Emoji zur visuellen Erkennung.
 * @property phonePrefix Die internationale Ländervorwahl mit führendem Pluszeichen (z. B. "+49").
 * @property isoCode ISO 3166-1 alpha-2 Ländercode.
 */
data class CountryCodeOption(
    val countryName: String,
    val flagEmoji: String,
    val phonePrefix: String,
    val isoCode: String
)

/**
 * Unveränderlicher UI-Zustand für den Telefonnummer-Login und OTP-Onboarding-Prozess.
 *
 * @property countryCode Die aktuell gewählte Ländervorwahl.
 * @property selectedCountry Das aktuell ausgewählte CountryCodeOption-Objekt.
 * @property phoneNumber Die vom Nutzer eingegebene Telefonnummer (ohne Ländervorwahl).
 * @property isValidPhoneNumber True, wenn das Format und die Ziffernlänge gültig sind.
 * @property validationErrorMessage Inline-Fehlermeldung für ungültige Formate oder Null-Werte.
 * @property isLoading True, wenn ein Netzwerkanfrage (OTP versenden / verifizieren) läuft.
 * @property isOtpSent True, sobald die OTP-Anforderung erfolgreich versandt wurde.
 * @property otpCode Der vom Nutzer eingegebene 6-stellige Einmal-Code.
 * @property isValidOtp True, wenn der OTP-Code das erforderliche Format hat (6 Ziffern).
 * @property errorMessage System- oder Server-Fehlermeldung zur Anzeige in der UI.
 * @property isSuccess True, wenn der Onboarding-Schritt erfolgreich abgeschlossen wurde.
 * @property supportedCountries Die verfuegbaren Ländervorwahlen zur Auswahl.
 */
data class PhoneLoginUiState(
    val countryCode: String = "+49",
    val selectedCountry: CountryCodeOption = DEFAULT_COUNTRY,
    val phoneNumber: String = "",
    val isValidPhoneNumber: Boolean = false,
    val validationErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val isOtpSent: Boolean = false,
    val otpCode: String = "",
    val isValidOtp: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val supportedCountries: List<CountryCodeOption> = DEFAULT_COUNTRIES
) {
    /**
     * Kombinierter String aus Ländervorwahl und Telefonnummer im E.164 Standard-Format.
     */
    val fullPhoneNumber: String
        get() = "${selectedCountry.phonePrefix}$phoneNumber"

    /**
     * Steuert die Aktivierung des Buttons zum Anfordern des OTP-Codes.
     */
    val isSubmitPhoneNumberEnabled: Boolean
        get() = isValidPhoneNumber && !isLoading && !isOtpSent

    /**
     * Steuert die Aktivierung des Buttons zur OTP-Code-Bestätigung.
     */
    val isVerifyOtpEnabled: Boolean
        get() = isValidOtp && !isLoading && isOtpSent

    companion object {
        val DEFAULT_COUNTRY = CountryCodeOption("Deutschland", "🇩🇪", "+49", "DE")

        val DEFAULT_COUNTRIES = listOf(
            CountryCodeOption("Deutschland", "🇩🇪", "+49", "DE"),
            CountryCodeOption("Österreich", "🇦🇹", "+43", "AT"),
            CountryCodeOption("Schweiz", "🇨🇭", "+41", "CH"),
            CountryCodeOption("Vereinigte Staaten", "🇺🇸", "+1", "US"),
            CountryCodeOption("Vereinigtes Königreich", "🇬🇧", "+44", "GB"),
            CountryCodeOption("Niederlande", "🇳🇱", "+31", "NL"),
            CountryCodeOption("Frankreich", "🇫🇷", "+33", "FR"),
            CountryCodeOption("Spanien", "🇪🇸", "+34", "ES"),
            CountryCodeOption("Italien", "🇮🇹", "+39", "IT")
        )
    }
}
