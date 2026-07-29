package com.kliq.app.util

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.util.CityChatLocationMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CityChatLocationMapperTest {

    @Test
    fun testGPSLocationMapsToClosestCityBerlin() {
        val berlinLocation = LocationData(latitude = 52.5200, longitude = 13.4050)
        val config = CityChatLocationMapper.resolveCityForLocation(berlinLocation)

        assertEquals("pub_1", config.id)
        assertEquals("Berlin - Tonight", config.title)
        assertEquals("Berlin", config.cityRegion)
    }

    @Test
    fun testGPSLocationMapsToClosestCityMunich() {
        val munichLocation = LocationData(latitude = 48.1351, longitude = 11.5820)
        val config = CityChatLocationMapper.resolveCityForLocation(munichLocation)

        assertEquals("pub_2", config.id)
        assertEquals("München - Party Radar", config.title)
        assertEquals("München", config.cityRegion)
    }

    @Test
    fun testNullLocationResolvesDefaultCity() {
        val config = CityChatLocationMapper.resolveCityForLocation(null)
        assertEquals("pub_1", config.id)
        assertEquals("Berlin", config.cityRegion)
    }

    @Test
    fun testDistanceCalculationInKm() {
        val dist = CityChatLocationMapper.calculateDistanceInKm(
            52.5200, 13.4050, // Berlin
            48.1351, 11.5820  // Munich
        )
        assertTrue(dist > 450.0 && dist < 600.0)
    }
}
