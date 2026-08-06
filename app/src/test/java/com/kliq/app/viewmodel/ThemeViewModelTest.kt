package com.kliq.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ThemeViewModelTest {

    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        viewModel = ThemeViewModel()
    }

    @Test
    fun `initial themeState defaults to dark mode and night optimized`() {
        val state = viewModel.themeState.value
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertTrue(state.isNightOptimized)
    }

    @Test
    fun `toggleTheme cycles through ThemeMode states correctly`() {
        assertEquals(ThemeMode.DARK, viewModel.themeState.value.themeMode)

        viewModel.toggleTheme()
        assertEquals(ThemeMode.LIGHT, viewModel.themeState.value.themeMode)

        viewModel.toggleTheme()
        assertEquals(ThemeMode.SYSTEM, viewModel.themeState.value.themeMode)

        viewModel.toggleTheme()
        assertEquals(ThemeMode.DARK, viewModel.themeState.value.themeMode)
    }

    @Test
    fun `setThemeMode updates mode without resetting night optimization`() {
        viewModel.setThemeMode(ThemeMode.SYSTEM)
        assertEquals(ThemeMode.SYSTEM, viewModel.themeState.value.themeMode)
        assertTrue(viewModel.themeState.value.isNightOptimized)
    }

    @Test
    fun `toggleNightOptimized updates flag correctly`() {
        viewModel.toggleNightOptimized()
        assertFalse(viewModel.themeState.value.isNightOptimized)

        viewModel.toggleNightOptimized()
        assertTrue(viewModel.themeState.value.isNightOptimized)
    }

    @Test
    fun `setNightOptimized updates flag correctly`() {
        viewModel.setNightOptimized(false)
        assertFalse(viewModel.themeState.value.isNightOptimized)

        viewModel.setNightOptimized(true)
        assertTrue(viewModel.themeState.value.isNightOptimized)
    }
}
