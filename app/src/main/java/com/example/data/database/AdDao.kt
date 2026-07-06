package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdDao {
    @Query("SELECT * FROM saved_ads ORDER BY createdAt DESC")
    fun getAllAds(): Flow<List<AdEntity>>

    @Query("SELECT * FROM saved_ads WHERE id = :id")
    fun getAdById(id: Int): Flow<AdEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: AdEntity): Long

    @Delete
    suspend fun deleteAd(ad: AdEntity)

    @Query("DELETE FROM saved_ads WHERE id = :id")
    suspend fun deleteAdById(id: Int)
}
