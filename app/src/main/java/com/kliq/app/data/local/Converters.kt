package com.kliq.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.model.SpecialOffer

class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromSearchIntent(intent: SearchIntent?): String {
        return intent?.name ?: SearchIntent.BOTH.name
    }

    @TypeConverter
    fun toSearchIntent(value: String?): SearchIntent {
        return SearchIntent.fromString(value)
    }

    @TypeConverter
    fun fromSmokingHabit(habit: SmokingHabit?): String {
        return habit?.name ?: SmokingHabit.NEVER.name
    }

    @TypeConverter
    fun toSmokingHabit(value: String?): SmokingHabit {
        return SmokingHabit.fromString(value)
    }

    @TypeConverter
    fun fromDrinkingHabit(habit: DrinkingHabit?): String {
        return habit?.name ?: DrinkingHabit.NEVER.name
    }

    @TypeConverter
    fun toDrinkingHabit(value: String?): DrinkingHabit {
        return DrinkingHabit.fromString(value)
    }

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
