package com.kliq.app.data.remote

interface SmsVerificationService {

    suspend fun sendVerificationCode(phoneNumber: String): Result<Unit>

    suspend fun verifyCode(phoneNumber: String, code: String): Result<Unit>
}
