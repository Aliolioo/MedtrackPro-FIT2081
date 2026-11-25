package com.adytio.s35637889.medtrack

import android.app.Application
import com.adytio.s35637889.medtrack.data.local.MedTrackDatabase
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository

class MedTrackApplication : Application() {

    val database by lazy {
        MedTrackDatabase.getDatabase(this)
    }

    val repository by lazy {
        MedTrackRepository(
            patientDao = database.patientDao(),
            medicationDao = database.medicationDao(),
            symptomDao = database.symptomDao(),
            medCoachTipDao = database.medCoachTipDao()


        )
    }
}