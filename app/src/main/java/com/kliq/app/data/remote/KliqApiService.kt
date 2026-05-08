package com.kliq.app.data.remote

import com.kliq.app.data.local.entities.UserEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface KliqApiService {
    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") userId: String): UserEntity
    
    // Additional social discovery endpoints would go here
    // @GET("discover/nearby")
    // suspend fun getNearbyUsers(@Query("lat") lat: Double, @Query("lng") lng: Double): List<UserEntity>
}
