package com.kliq.app.data.remote

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit-Tests für den [MockSmsVerificationService].
 * Verifiziert die Simulation von Latenz, Code-Validierung und Fehlerfällen.
 */
class MockSmsVerificationServiceTest {

    private lateinit var service: SmsVerificationService

    @Before
    fun setUp() {
        service = MockSmsVerificationService()
    }

    @Test
    fun sendVerificationCode_validPhoneNumber_returnsSuccess() = runTest {
        val result = service.sendVerificationCode("+4917612345678")
        assertTrue(result.isSuccess)
    }

    @Test
    fun sendVerificationCode_blankPhoneNumber_returnsFailure() = runTest {
        val result = service.sendVerificationCode("   ")
        assertTrue(result.isFailure)
        assertEquals("Telefonnummer darf nicht leer sein", result.exceptionOrNull()?.message)
    }

    @Test
    fun verifyCode_validTestCode_returnsSuccess() = runTest {
        val result = service.verifyCode("+4917612345678", MockSmsVerificationService.VALID_TEST_CODE)
        assertTrue(result.isSuccess)
    }

    @Test
    fun verifyCode_invalidCode_returnsFailure() = runTest {
        val result = service.verifyCode("+4917612345678", "000000")
        assertTrue(result.isFailure)
        assertEquals("Ungültiger Code. Bitte überprüfe deine Eingabe.", result.exceptionOrNull()?.message)
    }
}
