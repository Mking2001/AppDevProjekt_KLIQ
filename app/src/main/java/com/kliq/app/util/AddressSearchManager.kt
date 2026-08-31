package com.kliq.app.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.kliq.app.data.seed.KlagenfurtSeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class AddressSuggestion(
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val category: String = "Location"
)

object AddressSearchManager {

    val KLAGENFURT_REAL_ADDRESSES: List<AddressSuggestion> = listOf(
        AddressSuggestion("Neuer Platz", "Neuer Platz 1, 9020 Klagenfurt am Wörthersee", 46.6240, 14.3078, "Platz"),
        AddressSuggestion("Alter Platz", "Alter Platz, 9020 Klagenfurt am Wörthersee", 46.6247, 14.3053, "Platz"),
        AddressSuggestion("Heuplatz", "Heuplatz, 9020 Klagenfurt am Wörthersee", 46.6268, 14.3075, "Platz"),
        AddressSuggestion("Theaterplatz", "Theaterplatz 4, 9020 Klagenfurt am Wörthersee", 46.6253, 14.3050, "Kultur & Theater"),
        AddressSuggestion("Domplatz", "Domplatz 1, 9020 Klagenfurt am Wörthersee", 46.6214, 14.3082, "Platz"),
        AddressSuggestion("Pfarrplatz", "Pfarrplatz 1, 9020 Klagenfurt am Wörthersee", 46.6258, 14.3089, "Platz"),
        AddressSuggestion("Kardinalplatz", "Kardinalplatz 1, 9020 Klagenfurt am Wörthersee", 46.6219, 14.3128, "Platz"),
        AddressSuggestion("Dr.-Arthur-Lemisch-Platz", "Dr.-Arthur-Lemisch-Platz, 9020 Klagenfurt am Wörthersee", 46.6242, 14.3045, "Platz"),
        AddressSuggestion("Kramergasse", "Kramergasse 1, 9020 Klagenfurt am Wörthersee", 46.6243, 14.3065, "Fußgängerzone"),
        AddressSuggestion("Wiener Gasse", "Wiener Gasse 10, 9020 Klagenfurt am Wörthersee", 46.6255, 14.3072, "Straße"),
        AddressSuggestion("Herrengasse", "Herrengasse 12, 9020 Klagenfurt am Wörthersee", 46.6235, 14.3055, "Straße"),
        AddressSuggestion("Bahnhofstraße", "Bahnhofstraße 24, 9020 Klagenfurt am Wörthersee", 46.6210, 14.3120, "Straße"),
        AddressSuggestion("Hauptbahnhof Klagenfurt", "Walther-von-der-Vogelweide-Platz 1, 9020 Klagenfurt am Wörthersee", 46.6163, 14.3142, "Bahnhof"),
        AddressSuggestion("Villacher Straße", "Villacher Straße 1, 9020 Klagenfurt am Wörthersee", 46.6231, 14.2980, "Straße"),
        AddressSuggestion("St. Veiter Ring", "St. Veiter Ring 20, 9020 Klagenfurt am Wörthersee", 46.6285, 14.3090, "Ringstraße"),
        AddressSuggestion("Völkermarkter Ring", "Völkermarkter Ring 1, 9020 Klagenfurt am Wörthersee", 46.6250, 14.3150, "Ringstraße"),
        AddressSuggestion("Viktringer Ring", "Viktringer Ring 15, 9020 Klagenfurt am Wörthersee", 46.6185, 14.3070, "Ringstraße"),
        AddressSuggestion("Villacher Ring", "Villacher Ring 1, 9020 Klagenfurt am Wörthersee", 46.6235, 14.3015, "Ringstraße"),
        AddressSuggestion("Universitätsstraße (Alpen-Adria-Universität)", "Universitätsstraße 65-67, 9020 Klagenfurt am Wörthersee", 46.6162, 14.2655, "Campus"),
        AddressSuggestion("Lakeside Science & Technology Park", "Lakeside B01, 9020 Klagenfurt am Wörthersee", 46.6145, 14.2680, "Technologiepark"),
        AddressSuggestion("Wörthersee Ostbucht (Strandbad)", "Metnitzstrand 2, 9020 Klagenfurt am Wörthersee", 46.6200, 14.2530, "See / Strandbad"),
        AddressSuggestion("Maria Loretto", "Lorettoweg 54, 9020 Klagenfurt am Wörthersee", 46.6130, 14.2485, "Halbinsel"),
        AddressSuggestion("Lendkanal (Steinerne Brücke)", "Tarviser Straße 2, 9020 Klagenfurt am Wörthersee", 46.6225, 14.2950, "Uferpromenade"),
        AddressSuggestion("Lendhafen", "Lendhafen, 9020 Klagenfurt am Wörthersee", 46.6220, 14.3005, "Szeneviertel"),
        AddressSuggestion("Klagenfurter Messe", "Messeplatz 1, 9020 Klagenfurt am Wörthersee", 46.6205, 14.3175, "Messegelände"),
        AddressSuggestion("Wörthersee Stadion", "Südring 207, 9020 Klagenfurt am Wörthersee", 46.6086, 14.2785, "Stadion"),
        AddressSuggestion("Schleppekurve", "Schleppegasse 1, 9020 Klagenfurt am Wörthersee", 46.6380, 14.2985, "Location"),
        AddressSuggestion("Pernhartgasse", "Pernhartgasse 8, 9020 Klagenfurt am Wörthersee", 46.6238, 14.3060, "Straße"),
        AddressSuggestion("Burggasse", "Burggasse 4, 9020 Klagenfurt am Wörthersee", 46.6249, 14.3095, "Straße"),
        AddressSuggestion("Karfreitstraße", "Karfreitstraße 14, 9020 Klagenfurt am Wörthersee", 46.6228, 14.3105, "Straße"),
        AddressSuggestion("Salmstraße", "Salmstraße 6, 9020 Klagenfurt am Wörthersee", 46.6218, 14.3090, "Straße"),
        AddressSuggestion("Lidmanskygasse", "Lidmanskygasse 10, 9020 Klagenfurt am Wörthersee", 46.6208, 14.3075, "Straße"),
        AddressSuggestion("Feldkirchner Straße", "Feldkirchner Straße 50, 9020 Klagenfurt am Wörthersee", 46.6350, 14.3040, "Straße"),
        AddressSuggestion("Völkermarkter Straße", "Völkermarkter Straße 30, 9020 Klagenfurt am Wörthersee", 46.6265, 14.3220, "Straße"),
        AddressSuggestion("St. Ruprechter Straße", "St. Ruprechter Straße 12, 9020 Klagenfurt am Wörthersee", 46.6150, 14.3110, "Straße"),
        AddressSuggestion("Siebenhügelstraße", "Siebenhügelstraße 25, 9020 Klagenfurt am Wörthersee", 46.6110, 14.2850, "Straße"),
        AddressSuggestion("Kreuzbergl", "Kreuzbergl, 9020 Klagenfurt am Wörthersee", 46.6290, 14.2890, "Naherholungsgebiet")
    )

    suspend fun search(context: Context, query: String): List<AddressSuggestion> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            return@withContext KLAGENFURT_REAL_ADDRESSES.take(6)
        }

        val localMatches = KLAGENFURT_REAL_ADDRESSES.filter {
            it.name.contains(trimmed, ignoreCase = true) ||
            it.fullAddress.contains(trimmed, ignoreCase = true) ||
            it.category.contains(trimmed, ignoreCase = true)
        }

        if (localMatches.isNotEmpty()) {
            return@withContext localMatches
        }

        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.GERMANY)
                val searchQuery = if (!trimmed.lowercase(Locale.GERMANY).contains("klagenfurt") &&
                    !trimmed.lowercase(Locale.GERMANY).contains("kärnten") &&
                    !trimmed.lowercase(Locale.GERMANY).contains("österreich")) {
                    "$trimmed, Klagenfurt, Österreich"
                } else {
                    trimmed
                }

                @Suppress("DEPRECATION")
                val results: List<Address>? = geocoder.getFromLocationName(searchQuery, 5)
                if (!results.isNullOrEmpty()) {
                    return@withContext results.mapNotNull { address ->
                        val lat = address.latitude
                        val lng = address.longitude
                        val feature = address.featureName ?: address.thoroughfare ?: trimmed
                        val fullLine = (0..address.maxAddressLineIndex)
                            .mapNotNull { address.getAddressLine(it) }
                            .joinToString(", ")
                            .ifBlank { "$feature, ${address.postalCode ?: "9020"} ${address.locality ?: "Klagenfurt"}" }

                        AddressSuggestion(
                            name = feature,
                            fullAddress = fullLine,
                            latitude = lat,
                            longitude = lng,
                            category = "Adresse"
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }

        return@withContext KLAGENFURT_REAL_ADDRESSES.filter {
            it.name.contains(trimmed.take(3), ignoreCase = true) ||
            it.fullAddress.contains(trimmed.take(3), ignoreCase = true)
        }
    }
}
