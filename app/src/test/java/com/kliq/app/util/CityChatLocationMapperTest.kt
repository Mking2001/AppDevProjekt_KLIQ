package com.kliq.app.util

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.util.CityChatLocationMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CityChatLocationMapperTest {

    @Test
    fun testGPSLocationMapsToClosestCityKlagenfurt() {
        val klagenfurtLocation = LocationData(latitude = 46.6236, longitude = 14.3084)
        val config = CityChatLocationMapper.resolveCityForLocation(klagenfurtLocation)

        assertEquals("pub_klagenfurt", config.id)
        assertEquals("Klagenfurt - Tonight", config.title)
        assertEquals("Klagenfurt", config.cityRegion)
    }

    @Test
    fun testGPSLocationMapsToClosestCityVillach() {
        val villachLocation = LocationData(latitude = 46.6103, longitude = 13.8558)
        val config = CityChatLocationMapper.resolveCityForLocation(villachLocation)

        assertEquals("pub_villach", config.id)
        assertEquals("Villach - Party Radar", config.title)
        assertEquals("Villach", config.cityRegion)
    }

    @Test
    fun testGPSLocationMapsToClosestCityGraz() {
        val grazLocation = LocationData(latitude = 47.0707, longitude = 15.4395)
        val config = CityChatLocationMapper.resolveCityForLocation(grazLocation)

        assertEquals("pub_graz", config.id)
        assertEquals("Graz", config.cityRegion)
    }

    @Test
    fun testNullLocationResolvesDefaultCity() {
        val config = CityChatLocationMapper.resolveCityForLocation(null)
        assertEquals("pub_klagenfurt", config.id)
        assertEquals("Klagenfurt", config.cityRegion)
    }

    @Test
    fun testDistanceCalculationInKm() {
        val dist = CityChatLocationMapper.calculateDistanceInKm(
            46.6236, 14.3084,
            47.0707, 15.4395
        )
        assertTrue("Erwartet 90 bis 130 km, war $dist", dist > 90.0 && dist < 130.0)
    }

    @Test
    fun testAllSupportedCitiesHaveUniqueIdsAndCoordinates() {
        val cities = CityChatLocationMapper.SUPPORTED_CITIES

        assertEquals(cities.size, cities.map { it.id }.toSet().size)
        assertTrue(cities.all { it.latitude != 0.0 && it.longitude != 0.0 })
        assertTrue(cities.all { it.avatarInitial.length == 1 })
    }
}
