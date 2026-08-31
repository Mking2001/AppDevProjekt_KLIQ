package com.kliq.app.util

import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.OperatingHours
import java.time.LocalTime
import java.util.Calendar

object OpeningHoursHelper {

    fun determineLiveStatus(
        operatingHours: OperatingHours,
        currentTime: LocalTime = LocalTime.now(),
        currentDayOfWeek: String = getCurrentDayGermanName()
    ): LiveOpeningStatus {
        val todayScheduleText = operatingHours.weeklySchedule[currentDayOfWeek] ?: operatingHours.todayHours

        if (todayScheduleText.isBlank() || todayScheduleText.equals("Geschlossen", ignoreCase = true)) {
            return LiveOpeningStatus.CLOSED
        }

        if (todayScheduleText.contains("Open End", ignoreCase = true) || todayScheduleText.contains("24h", ignoreCase = true)) {
            return LiveOpeningStatus.OPEN_NOW
        }

        val parts = todayScheduleText.split("-").map { it.trim() }
        if (parts.size != 2) {
            return if (operatingHours.isOpenNow) LiveOpeningStatus.OPEN_NOW else LiveOpeningStatus.CLOSED
        }

        val startMinutes = parseMinutesFromMidnight(parts[0]) ?: return if (operatingHours.isOpenNow) LiveOpeningStatus.OPEN_NOW else LiveOpeningStatus.CLOSED
        val endMinutes = parseMinutesFromMidnight(parts[1])

        val nowMinutes = currentTime.hour * 60 + currentTime.minute

        if (endMinutes == null) {
            return if (nowMinutes >= startMinutes || (startMinutes > 1200 && nowMinutes < 360)) {
                LiveOpeningStatus.OPEN_NOW
            } else {
                LiveOpeningStatus.CLOSED
            }
        }

        val isOvernight = endMinutes <= startMinutes

        val isOpen = if (!isOvernight) {
            nowMinutes in startMinutes until endMinutes
        } else {
            nowMinutes >= startMinutes || nowMinutes < endMinutes
        }

        if (!isOpen) {
            return LiveOpeningStatus.CLOSED
        }

        val minutesUntilClose = if (!isOvernight) {
            endMinutes - nowMinutes
        } else if (nowMinutes >= startMinutes) {
            (24 * 60 - nowMinutes) + endMinutes
        } else {
            endMinutes - nowMinutes
        }

        return if (minutesUntilClose in 1..60) {
            LiveOpeningStatus.CLOSING_SOON
        } else {
            LiveOpeningStatus.OPEN_NOW
        }
    }

    fun parseMinutesFromMidnight(timeStr: String): Int? {
        val clean = timeStr.trim()
        if (clean.equals("Open End", ignoreCase = true)) return null
        return try {
            val parts = clean.split(":")
            if (parts.size == 2) {
                val h = parts[0].trim().toInt()
                val m = parts[1].trim().toInt()
                h * 60 + m
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentDayGermanName(calendar: Calendar = Calendar.getInstance()): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Montag"
            Calendar.TUESDAY -> "Dienstag"
            Calendar.WEDNESDAY -> "Mittwoch"
            Calendar.THURSDAY -> "Donnerstag"
            Calendar.FRIDAY -> "Freitag"
            Calendar.SATURDAY -> "Samstag"
            Calendar.SUNDAY -> "Sonntag"
            else -> "Freitag"
        }
    }

    fun getFormattedOpeningHoursForToday(operatingHours: OperatingHours, dayGerman: String = getCurrentDayGermanName()): String {
        return operatingHours.weeklySchedule[dayGerman] ?: operatingHours.todayHours.ifBlank { "Geschlossen" }
    }
}
