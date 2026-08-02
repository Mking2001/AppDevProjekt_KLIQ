package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.ClubOfferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubOfferDao {
    @Query("SELECT * FROM club_offers WHERE clubId = :clubId")
    fun getOffersByClubId(clubId: String): Flow<List<ClubOfferEntity>>

    @Query("SELECT * FROM club_offers WHERE id = :offerId")
    fun getOfferById(offerId: String): Flow<ClubOfferEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<ClubOfferEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: ClubOfferEntity)

    @Query("DELETE FROM club_offers WHERE id = :offerId")
    suspend fun deleteOfferById(offerId: String)

    @Query("DELETE FROM club_offers WHERE clubId = :clubId")
    suspend fun deleteOffersByClubId(clubId: String)
}
