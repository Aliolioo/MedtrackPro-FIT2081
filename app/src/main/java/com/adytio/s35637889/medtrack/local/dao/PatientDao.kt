package com.adytio.s35637889.medtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    // used when generating the next patient ID for signup
    @Query("SELECT patientId FROM patients")
    suspend fun getAllPatientIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    // bulk insert for CSV seeding
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)


    // returns Flow so the UI updates automatically when data changes
    @Query("SELECT * FROM patients ORDER BY patientId ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE patientId = :patientId LIMIT 1")
    suspend fun getPatientById(patientId: String): PatientEntity?

    @Query("SELECT * FROM patients WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getPatientByPhone(phoneNumber: String): PatientEntity?

    // for account claiming - need both ID and phone to verify identity
    @Query(
        """
        SELECT * FROM patients 
        WHERE patientId = :patientId 
        AND phoneNumber = :phoneNumber 
        LIMIT 1
        """
    )
    suspend fun findPatientForClaim(
        patientId: String,
        phoneNumber: String
    ): PatientEntity?


    // sets the password and marks account as claimed
    @Query(
        """
        UPDATE patients 
        SET password = :password, isClaimed = 1 
        WHERE patientId = :patientId
        """
    )
    suspend fun claimPatientAccount(
        patientId: String,
        password: String
    )

    // checks credentials - returns null if wrong password/id
    @Query(
        """
        SELECT * FROM patients 
        WHERE patientId = :patientId 
        AND password = :password 
        LIMIT 1
        """
    )
    suspend fun login(
        patientId: String,
        password: String
    ): PatientEntity?

    // for clinician dashboard stats
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun countPatients(): Int
}