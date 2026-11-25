package com.adytio.s35637889.medtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adytio.s35637889.medtrack.data.local.entity.SymptomEntity
import com.adytio.s35637889.medtrack.data.local.model.SymptomCategoryCount
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: SymptomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptoms(symptoms: List<SymptomEntity>)


    // finds which symptom category appears the most across all patients
    // used in clinician dashboard
    @Query(
        """
    SELECT category, COUNT(*) AS count
    FROM symptoms
    GROUP BY category
    ORDER BY count DESC
    LIMIT 1
    """
    )
    suspend fun getMostCommonSymptomCategory(): SymptomCategoryCount?

    @Query("SELECT AVG(severity) FROM symptoms")
    suspend fun getAverageSymptomSeverity(): Double?


    // newest symptoms first for the symptom history list
    @Query(
        """
        SELECT * FROM symptoms 
        WHERE patientId = :patientId 
        ORDER BY dateTime DESC
        """
    )
    fun getSymptomsForPatient(patientId: String): Flow<List<SymptomEntity>>

    @Query("SELECT COUNT(*) FROM symptoms")
    suspend fun countSymptoms(): Int
}