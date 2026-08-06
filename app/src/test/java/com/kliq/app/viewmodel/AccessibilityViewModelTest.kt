package com.kliq.app.viewmodel

import com.kliq.app.data.repository.AccessibilityRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccessibilityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AccessibilityRepositoryImpl
    private lateinit var viewModel: AccessibilityViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = AccessibilityRepositoryImpl()
        viewModel = AccessibilityViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState matches default settings`() {
        val state = viewModel.uiState.value
        assertFalse(state.settings.isHighContrastEnabled)
        assertEquals(1.0f, state.settings.fontScale, 0.01f)
    }

    @Test
    fun `toggleHighContrast updates repository state`() {
        viewModel.toggleHighContrast()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(repository.accessibilitySettings.value.isHighContrastEnabled)
    }

    @Test
    fun `setFontScale updates repository state`() {
        viewModel.setFontScale(1.4f)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1.4f, repository.accessibilitySettings.value.fontScale, 0.01f)
    }

    @Test
    fun `resetAccessibilityDefaults resets state to default`() {
        viewModel.setHighContrastEnabled(true)
        viewModel.setFontScale(1.8f)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.resetAccessibilityDefaults()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(repository.accessibilitySettings.value.isHighContrastEnabled)
        assertEquals(1.0f, repository.accessibilitySettings.value.fontScale, 0.01f)
    }
}
