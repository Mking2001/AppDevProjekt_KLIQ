package com.kliq.app.util

import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.OperatingHours
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalTime

class OpeningHoursHelperTest {

    @Test
    fun `determineLiveStatus returns OPEN_NOW when club is open during standard hours`() {
        val hours = OperatingHours(
            todayHours = "18:00 - 23:00",
            weeklySchedule = mapOf("Freitag" to "18:00 - 23:00")
        )
        val status = OpeningHoursHelper.determineLiveStatus(
            operatingHours = hours,
            currentTime = LocalTime.of(20, 0),
            currentDayOfWeek = "Freitag"
        )
        assertEquals(LiveOpeningStatus.OPEN_NOW, status)
    }

    @Test
    fun `determineLiveStatus returns CLOSING_SOON when club closes within 60 minutes`() {
        val hours = OperatingHours(
            todayHours = "23:00 - 06:00",
            weeklySchedule = mapOf("Freitag" to "23:00 - 06:00")
        )
        val status = OpeningHoursHelper.determineLiveStatus(
            operatingHours = hours,
            currentTime = LocalTime.of(5, 30),
            currentDayOfWeek = "Freitag"
        )
        assertEquals(LiveOpeningStatus.CLOSING_SOON, status)
    }

    @Test
    fun `determineLiveStatus returns CLOSED when current time is outside operating hours`() {
        val hours = OperatingHours(
            todayHours = "23:00 - 06:00",
            weeklySchedule = mapOf("Freitag" to "23:00 - 06:00")
        )
        val status = OpeningHoursHelper.determineLiveStatus(
            operatingHours = hours,
            currentTime = LocalTime.of(14, 0),
            currentDayOfWeek = "Freitag"
        )
        assertEquals(LiveOpeningStatus.CLOSED, status)
    }

    @Test
    fun `determineLiveStatus returns OPEN_NOW for Open End schedule`() {
        val hours = OperatingHours(
            todayHours = "23:59 - Open End",
            weeklySchedule = mapOf("Samstag" to "23:59 - Open End")
        )
        val status = OpeningHoursHelper.determineLiveStatus(
            operatingHours = hours,
            currentTime = LocalTime.of(2, 0),
            currentDayOfWeek = "Samstag"
        )
        assertEquals(LiveOpeningStatus.OPEN_NOW, status)
    }

    @Test
    fun `determineLiveStatus returns CLOSED for explicit Geschlossen schedule`() {
        val hours = OperatingHours(
            todayHours = "Geschlossen",
            weeklySchedule = mapOf("Montag" to "Geschlossen")
        )
        val status = OpeningHoursHelper.determineLiveStatus(
            operatingHours = hours,
            currentTime = LocalTime.of(20, 0),
            currentDayOfWeek = "Montag"
        )
        assertEquals(LiveOpeningStatus.CLOSED, status)
    }

    @Test
    fun `parseMinutesFromMidnight calculates correct minutes`() {
        assertEquals(1380, OpeningHoursHelper.parseMinutesFromMidnight("23:00"))
        assertEquals(360, OpeningHoursHelper.parseMinutesFromMidnight("06:00"))
        assertNull(OpeningHoursHelper.parseMinutesFromMidnight("Open End"))
    }
}
