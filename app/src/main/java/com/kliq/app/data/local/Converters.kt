package com.kliq.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.app.data.model.SpecialOffer

class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromSpecialOffersList(value: List<SpecialOffer>?): String {
        if (value == null) return "[]"
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSpecialOffersList(value: String?): List<SpecialOffer> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<SpecialOffer>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return ""
        return value.joinToString(separator = "|||")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split("|||")
    }
}
