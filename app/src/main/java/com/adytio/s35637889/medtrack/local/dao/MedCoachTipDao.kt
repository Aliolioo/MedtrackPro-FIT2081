package com.adytio.s35637889.medtrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adytio.s35637889.medtrack.data.local.entity.MedCoachTipEntity
import kotlinx.coroutines.flow.Flow


// DAO for storing and retrieving GenAI-generated tips
@Dao
interface MedCoachTipDao {

    @Insert
    suspend fun insertTip(tip: MedCoachTipEntity)

    // ordered by tipId desc so newest tips show first in the dialog
    @Query("SELECT * FROM medcoach_tips WHERE patientId = :patientId ORDER BY tipId DESC")
    fun getTipsForPatient(patientId: String): Flow<List<MedCoachTipEntity>>
}