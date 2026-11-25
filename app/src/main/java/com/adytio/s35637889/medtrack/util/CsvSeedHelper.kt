package com.adytio.s35637889.medtrack.util

import android.content.Context
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import com.adytio.s35637889.medtrack.data.local.entity.SymptomEntity
import com.adytio.s35637889.medtrack.util.normalizePhone

object CsvSeedHelper {

    fun readPatientsFromCsv(context: Context): List<PatientEntity> {
        return context.assets.open("patients.csv")
            .bufferedReader()
            .readLines()
            .drop(1)
            .mapNotNull { line ->
                val c = line.split(",").map { it.trim() }

                if (c.size >= 3) {
                    PatientEntity(
                        patientId = c[0],
                        phoneNumber = normalizePhone(c[1]),
                        name = c[2],
                        password = null,
                        isClaimed = false
                    )
                } else {
                    null
                }
            }
    }

    fun readMedicationsFromCsv(context: Context): List<MedicationEntity> {
        return context.assets.open("medications.csv")
            .bufferedReader()
            .readLines()
            .drop(1)
            .mapNotNull { line ->
                val c = line.split(",").map { it.trim() }

                if (c.size >= 7) {
                    MedicationEntity(
                        patientId = c[0],
                        medicationName = c[1],
                        dosage = c[2],
                        frequency = c[3],
                        scheduledTime = c[4],
                        medicationType = c[5],
                        notes = c[6]
                    )
                } else {
                    null
                }
            }
    }

    fun readSymptomsFromCsv(context: Context): List<SymptomEntity> {
        return context.assets.open("symptoms.csv")
            .bufferedReader()
            .readLines()
            .drop(1)
            .mapNotNull { line ->
                val c = line.split(",").map { it.trim() }

                if (c.size >= 5) {
                    SymptomEntity(
                        patientId = c[0],
                        category = c[1],
                        severity = c[2].toIntOrNull() ?: 1,
                        notes = c[3],
                        dateTime = c[4] + if (c.size > 5) " ${c[5]}" else ""
                    )
                } else {
                    null
                }
            }
    }
}