package com.adytio.s35637889.medtrack.data.repository

import android.content.Context
import com.adytio.s35637889.medtrack.BuildConfig
import com.adytio.s35637889.medtrack.data.local.dao.MedCoachTipDao
import com.adytio.s35637889.medtrack.data.local.dao.MedicationDao
import com.adytio.s35637889.medtrack.data.local.dao.PatientDao
import com.adytio.s35637889.medtrack.data.local.dao.SymptomDao
import com.adytio.s35637889.medtrack.data.local.entity.MedCoachTipEntity
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import com.adytio.s35637889.medtrack.data.local.entity.SymptomEntity
import com.adytio.s35637889.medtrack.util.CsvSeedHelper
import com.adytio.s35637889.medtrack.data.remote.RetrofitInstance
import com.adytio.s35637889.medtrack.data.remote.DrugLabelResult
import com.adytio.s35637889.medtrack.data.remote.gemini.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import com.adytio.s35637889.medtrack.data.remote.gemini.Content
import com.adytio.s35637889.medtrack.data.remote.gemini.GeminiRequest
import com.adytio.s35637889.medtrack.data.remote.gemini.GeminiRetrofit
import com.adytio.s35637889.medtrack.data.remote.gemini.Part
import org.json.JSONArray

class MedTrackRepository(
    private val patientDao: PatientDao,
    private val medicationDao: MedicationDao,
    private val symptomDao: SymptomDao,
    private val medCoachTipDao: MedCoachTipDao

) {

    companion object {
        private const val PREFS_NAME = "medtrack_prefs"

        // Allowed by assignment: first-launch detection
        private const val KEY_DB_SEEDED = "db_seeded"

        private const val KEY_OLD_USERS = "users"
        private const val KEY_OLD_MEDICATIONS = "medications"
    }

    suspend fun updateMedicationTakenState(
        medicationId: Int,
        isTaken: Boolean,
        date: String
    ) {
        medicationDao.updateTakenState(
            medicationId = medicationId,
            isTaken = isTaken,
            date = date
        )
    }

    suspend fun seedDatabaseIfNeeded(context: Context) {
        withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val alreadySeeded = prefs.getBoolean(KEY_DB_SEEDED, false)

            if (alreadySeeded) return@withContext

            // 1. Seed CSV data into Room
            val patients = CsvSeedHelper.readPatientsFromCsv(context)
            val medications = CsvSeedHelper.readMedicationsFromCsv(context)
            val symptoms = CsvSeedHelper.readSymptomsFromCsv(context)

            patientDao.insertPatients(patients)
            medicationDao.insertMedications(medications)
            symptomDao.insertSymptoms(symptoms)

            // Migrate SharedPreferences data into Room
            migrateOldSharedPreferencesUsers(context)
            migrateOldSharedPreferencesMedications(context)

            // Mark database as seeded so CSV is not imported again
            prefs.edit()
                .putBoolean(KEY_DB_SEEDED, true)
                .apply()
        }
    }

    private suspend fun migrateOldSharedPreferencesUsers(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_OLD_USERS, "[]") ?: "[]"
        val arr = JSONArray(json)

        val migratedUsers = mutableListOf<PatientEntity>()

        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)

            migratedUsers.add(
                PatientEntity(
                    patientId = o.getString("patientId"),
                    phoneNumber = o.getString("phoneNumber"),
                    name = o.getString("name"),
                    password = o.optString("password").ifBlank { null },
                    isClaimed = true
                )
            )
        }

        if (migratedUsers.isNotEmpty()) {
            patientDao.insertPatients(migratedUsers)
        }
    }

    suspend fun generateMedicationTip(
        selectedMedication: String,
        medicationNames: List<String>,
        symptomSummary: List<String>
    ): String? {
        return try {
            val medsText = if (medicationNames.isEmpty()) {
                "No saved medications."
            } else {
                medicationNames.joinToString(", ")
            }

            val symptomsText = if (symptomSummary.isEmpty()) {
                "No symptom history recorded."
            } else {
                symptomSummary.joinToString(", ")
            }

            val prompt = """
            You are MedCoach, a medication adherence assistant.

            The patient selected this medication:
            $selectedMedication

            The patient's saved medication list:
            $medsText

            The patient's recent symptom history:
            $symptomsText

            Generate ONE short and practical medication adherence tip.
            Keep it under 45 words.
            Do not diagnose.
            Do not tell the patient to change dosage.
            Remind the patient to consult a healthcare professional for serious concerns.
        """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(prompt)
                        )
                    )
                )
            )

            val response = GeminiRetrofit.api.generateTip(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertMedCoachTip(tip: MedCoachTipEntity) {
        medCoachTipDao.insertTip(tip)
    }

    fun getMedCoachTipsForPatient(patientId: String) =
        medCoachTipDao.getTipsForPatient(patientId)

    suspend fun searchDrugInfo(
        drugName: String
    ): DrugLabelResult? {

        return try {
            val response = RetrofitInstance.openFdaApi.searchDrugLabel(
                search = "openfda.brand_name:\"$drugName\""
            )

            response.results?.firstOrNull()

        } catch (e: Exception) {
            null
        }
    }

    private suspend fun migrateOldSharedPreferencesMedications(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_OLD_MEDICATIONS, "[]") ?: "[]"
        val arr = JSONArray(json)

        val migratedMedications = mutableListOf<MedicationEntity>()

        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)

            migratedMedications.add(
                MedicationEntity(
                    patientId = o.getString("patientId"),
                    medicationName = o.getString("medicationName"),
                    dosage = o.getString("dosage"),
                    frequency = o.getString("frequency"),
                    scheduledTime = o.getString("scheduledTime"),
                    medicationType = o.getString("medicationType"),
                    notes = o.optString("notes")
                )
            )
        }

        if (migratedMedications.isNotEmpty()) {
            medicationDao.insertMedications(migratedMedications)
        }
    }

    // Patient functions

    fun getAllPatients(): Flow<List<PatientEntity>> {
        return patientDao.getAllPatients()
    }

    suspend fun generateNextPatientId(): String {
        val ids = patientDao.getAllPatientIds()

        val maxNumber = ids
            .mapNotNull { id ->
                id.removePrefix("P").toIntOrNull()
            }
            .maxOrNull() ?: 1000

        return "P${maxNumber + 1}"
    }

    suspend fun getPatientById(patientId: String): PatientEntity? {
        return patientDao.getPatientById(patientId)
    }

    suspend fun getPatientByPhone(phoneNumber: String): PatientEntity? {
        return patientDao.getPatientByPhone(phoneNumber)
    }

    suspend fun findPatientForClaim(
        patientId: String,
        phoneNumber: String
    ): PatientEntity? {
        return patientDao.findPatientForClaim(patientId, phoneNumber)
    }

    suspend fun claimPatientAccount(
        patientId: String,
        password: String
    ) {
        patientDao.claimPatientAccount(patientId, password)
    }

    suspend fun login(
        patientId: String,
        password: String
    ): PatientEntity? {
        return patientDao.login(patientId, password)
    }

    suspend fun insertPatient(patient: PatientEntity) {
        patientDao.insertPatient(patient)
    }

    // Medication functions

    fun getMedicationsForPatient(patientId: String): Flow<List<MedicationEntity>> {
        return medicationDao.getMedicationsForPatient(patientId)
    }

    suspend fun insertMedication(medication: MedicationEntity) {
        medicationDao.insertMedication(medication)
    }

    // Symptom functions

    fun getSymptomsForPatient(patientId: String): Flow<List<SymptomEntity>> {
        return symptomDao.getSymptomsForPatient(patientId)
    }

    suspend fun insertSymptom(symptom: SymptomEntity) {
        symptomDao.insertSymptom(symptom)
    }

    data class ClinicianDashboardStats(
        val totalPatients: Int,
        val averageMedicationsPerPatient: Double,
        val mostCommonSymptomCategory: String,
        val averageSymptomSeverity: Double
    )

    suspend fun getClinicianDashboardStats(): ClinicianDashboardStats {
        val totalPatients = patientDao.countPatients()
        val totalMedications = medicationDao.countMedications()

        val averageMedicationsPerPatient =
            if (totalPatients == 0) 0.0
            else totalMedications.toDouble() / totalPatients.toDouble()

        val mostCommonCategory =
            symptomDao.getMostCommonSymptomCategory()?.category ?: "No symptoms recorded"

        val averageSeverity =
            symptomDao.getAverageSymptomSeverity() ?: 0.0

        return ClinicianDashboardStats(
            totalPatients = totalPatients,
            averageMedicationsPerPatient = averageMedicationsPerPatient,
            mostCommonSymptomCategory = mostCommonCategory,
            averageSymptomSeverity = averageSeverity
        )
    }

    suspend fun generateClinicianInsights(
        stats: ClinicianDashboardStats
    ): String? {
        return try {
            val prompt = """
            You are a clinical data assistant for a medication tracking app.

            Here is aggregate patient data:
            - Total patients: ${stats.totalPatients}
            - Average medications per patient: ${"%.2f".format(stats.averageMedicationsPerPatient)}
            - Most common symptom category: ${stats.mostCommonSymptomCategory}
            - Average symptom severity: ${"%.2f".format(stats.averageSymptomSeverity)}

            Generate exactly 3 short, data-driven insights.
            Do not diagnose patients.
            Do not mention individual patients.
            Keep each insight practical and concise.
        """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(prompt)
                        )
                    )
                )
            )

            val response = GeminiRetrofit.api.generateTip(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

        }catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e(
                "CLINICIAN_AI_ERROR",
                "HTTP ${e.code()} error body: $errorBody",
                e
            )
            null
        } catch (e: Exception) {
            android.util.Log.e("CLINICIAN_AI_ERROR", "Failed to generate clinician insights", e)
            null
        }
    }
}