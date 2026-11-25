package com.adytio.s35637889.medtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(medications: List<MedicationEntity>)

    // filtered by patient, sorted by time so morning meds show first
    @Query(
        """
        SELECT * FROM medications 
        WHERE patientId = :patientId 
        ORDER BY scheduledTime ASC
        """
    )
    fun getMedicationsForPatient(patientId: String): Flow<List<MedicationEntity>>

    // for the taken toggle - saves both the boolean and today's date
    // so we can reset the toggle when a new day starts
    @Query(
        """
    UPDATE medications
    SET isTakenToday = :isTaken,
        lastTakenDate = :date
    WHERE medicationId = :medicationId
    """
    )
    suspend fun updateTakenState(
        medicationId: Int,
        isTaken: Boolean,
        date: String
    )

    // used in clinician dashboard to calculate avg meds per patient
    @Query("SELECT COUNT(*) FROM medications")
    suspend fun countMedications(): Int
}