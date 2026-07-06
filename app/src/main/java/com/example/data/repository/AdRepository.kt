package com.example.data.repository

import com.example.data.database.AdDao
import com.example.data.database.AdEntity
import kotlinx.coroutines.flow.Flow

class AdRepository(private val adDao: AdDao) {
    val allAds: Flow<List<AdEntity>> = adDao.getAllAds()

    fun getAdById(id: Int): Flow<AdEntity?> {
        return adDao.getAdById(id)
    }

    suspend fun insertAd(ad: AdEntity): Long {
        return adDao.insertAd(ad)
    }

    suspend fun deleteAd(ad: AdEntity) {
        adDao.deleteAd(ad)
    }

    suspend fun deleteAdById(id: Int) {
        adDao.deleteAdById(id)
    }
}
