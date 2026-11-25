package com.adytio.s35637889.medtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    val patientId: String,

    val phoneNumber: String,
    val name: String,

    // CSV users start with no password.
    // They claim account first, then set password.
    val password: String? = null,

    // false = CSV patient has not claimed account yet
    // true = user has signed up/claimed account
    val isClaimed: Boolean = false
)