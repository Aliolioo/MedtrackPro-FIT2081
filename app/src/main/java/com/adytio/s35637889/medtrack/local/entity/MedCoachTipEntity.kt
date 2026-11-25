package com.adytio.s35637889.medtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


// Stores AI-generated tips from MedCoach screen (Band B requirement)
@Entity(tableName = "medcoach_tips")
data class MedCoachTipEntity(
    @PrimaryKey(autoGenerate = true)
    val tipId: Int = 0,
    val patientId: String,
    val tipText: String,
    val createdAt: String  // timestamp when the tip was generated
)