package com.kliq.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AccessibilityRepositoryTest {

    private lateinit var repository: AccessibilityRepositoryImpl

    @Before
    fun setUp() {
        repository = AccessibilityRepositoryImpl()
    }

    @Test
    fun `initial settings have default values`() {
        val settings = repository.accessibilitySettings.value
        assertFalse(settings.isHighContrastEnabled)
        assertEquals(1.0f, settings.fontScale, 0.01f)
        assertTrue(settings.isTalkBackOptimized)
        assertFalse(settings.isReduceMotionEnabled)
    }

    @Test
    fun `updateHighContrast modifies settings flow`() {
        repository.updateHighContrast(true)
        assertTrue(repository.accessibilitySettings.value.isHighContrastEnabled)

        repository.updateHighContrast(false)
        assertFalse(repository.accessibilitySettings.value.isHighContrastEnabled)
    }

    @Test
    fun `updateFontScale clamps font scale within range`() {
        repository.updateFontScale(1.5f)
        assertEquals(1.5f, repository.accessibilitySettings.value.fontScale, 0.01f)

        repository.updateFontScale(3.0f)
        assertEquals(2.0f, repository.accessibilitySettings.value.fontScale, 0.01f)
    }

    @Test
    fun `resetToDefaults restores initial settings`() {
        repository.updateHighContrast(true)
        repository.updateFontScale(1.8f)
        repository.resetToDefaults()

        val settings = repository.accessibilitySettings.value
        assertFalse(settings.isHighContrastEnabled)
        assertEquals(1.0f, settings.fontScale, 0.01f)
    }
}
