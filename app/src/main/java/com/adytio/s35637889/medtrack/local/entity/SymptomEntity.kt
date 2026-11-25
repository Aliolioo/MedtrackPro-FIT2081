package com.adytio.s35637889.medtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Symptom table - stores logged symptoms per patient
@Entity(tableName = "symptoms")
data class SymptomEntity(
    @PrimaryKey(autoGenerate = true)
    val symptomId: Int = 0,

    val patientId: String, // links to patients table

    val category: String, // e.g. Headache, Fever, Nausea
    val severity: Int, // 1-10 scale
    val notes: String,
    val dateTime: String // stored as string for simplicity
)