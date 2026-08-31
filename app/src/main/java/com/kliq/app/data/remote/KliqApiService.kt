package com.kliq.app.data.remote

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.remote.model.ExternalSearchResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class ReportUserRequestDto(
    val reporterUserId: String,
    val targetUserId: String,
    val reason: String,
    val details: String? = null
)

data class BlockUserRequestDto(
    val currentUserId: String,
    val targetUserId: String,
    val reason: String? = null
)

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

    @POST("users/report")
    suspend fun reportUser(@Body request: ReportUserRequestDto): Response<Unit>

    @POST("users/block")
    suspend fun blockUser(@Body request: BlockUserRequestDto): Response<Unit>

    @DELETE("users/{currentUserId}/block/{targetUserId}")
    suspend fun unblockUser(
        @Path("currentUserId") currentUserId: String,
        @Path("targetUserId") targetUserId: String
    ): Response<Unit>
}
