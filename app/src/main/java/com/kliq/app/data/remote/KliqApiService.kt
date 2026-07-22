package com.kliq.app.data.remote

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.remote.model.ExternalSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KliqApiService {
    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") userId: String): UserEntity
    
    @GET("search/discover")
    suspend fun searchExternalClubsAndEvents(
        @Query("q") query: String,
        @Query("lat") latitude: Double? = null,
        @Query("lng") longitude: Double? = null,
        @Query("radius") radiusKm: Int? = 25
    ): ExternalSearchResponseDto
}
