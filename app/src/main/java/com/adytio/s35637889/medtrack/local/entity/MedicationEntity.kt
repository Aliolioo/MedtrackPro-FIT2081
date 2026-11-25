package com.adytio.s35637889.medtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Medication table: each row belongs to a patient (linked by patientId)
// Fields match the columns from medications.csv
@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val medicationId: Int = 0,

    val patientId: String,

    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val scheduledTime: String,
    val medicationType: String,
    val notes: String,


    // for the taken toggle (Band C) - persists across restarts
    // resets daily by checking lastTakenDate against today
    val isTakenToday: Boolean = false,
    val lastTakenDate: String? = null
)