package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_ads")
data class AdEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brandName: String,
    val productDetails: String,
    val objective: String,
    val theme: String,
    val headline: String,
    val description: String,
    val ctaText: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val textColorHex: String,
    val backgroundColorHex: String,
    val createdAt: Long = System.currentTimeMillis()
)
