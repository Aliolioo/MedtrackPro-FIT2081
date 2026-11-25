package com.adytio.s35637889.medtrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adytio.s35637889.medtrack.data.local.dao.MedCoachTipDao
import com.adytio.s35637889.medtrack.data.local.dao.MedicationDao
import com.adytio.s35637889.medtrack.data.local.dao.PatientDao
import com.adytio.s35637889.medtrack.data.local.dao.SymptomDao
import com.adytio.s35637889.medtrack.data.local.entity.MedCoachTipEntity
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import com.adytio.s35637889.medtrack.data.local.entity.SymptomEntity


// Room database class - ties together all entities and DAOs
// Using singleton pattern from week 5 lab so only one instance exists
@Database(
    entities = [
        PatientEntity::class,
        MedicationEntity::class,
        SymptomEntity::class,
        MedCoachTipEntity::class
    ],
    version = 3,  // bumped version during development when adding columns
    exportSchema = false
)
abstract class MedTrackDatabase : RoomDatabase() {
    // one DAO per entity table

    abstract fun patientDao(): PatientDao
    abstract fun medicationDao(): MedicationDao
    abstract fun symptomDao(): SymptomDao
    abstract fun medCoachTipDao(): MedCoachTipDao

    companion object {
        @Volatile // makes sure INSTANCE is visible to all threads immediately
        private var INSTANCE: MedTrackDatabase? = null

        // singleton - only creates one db instance for the whole app
        fun getDatabase(context: Context): MedTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedTrackDatabase::class.java,
                    "medtrack_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}