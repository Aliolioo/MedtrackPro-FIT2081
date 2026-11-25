
package com.adytio.s35637889.medtrack
import android.util.Log
import android.content.Context
//import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adytio.s35637889.medtrack.ui.viewmodel.DatabaseSeedViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.DatabaseSeedViewModelFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue

import com.adytio.s35637889.medtrack.ui.theme.MedTrackTheme

//  Data models
//data class Patient(
//    val patientId: String,
//    val phoneNumber: String,
//    val name: String,
//    val password: String
//)
//
//data class Medication(
//    val patientId: String,
//    val medicationName: String,
//    val dosage: String,
//    val frequency: String,
//    val scheduledTime: String,
//    val medicationType: String,
//    val notes: String
//)
//data class Symptom(
//    val patientId: String,
//    val category: String,
//    val severity: Int,
//    val notes: String,
//    val dateTime: String
//)

// Constants
const val PREFS           = "medtrack_prefs"
const val KEY_PATIENT_ID  = "logged_in_patient_id"
//const val KEY_USERS       = "users"
//const val KEY_MEDICATIONS = "medications"

fun getPrefs(context: Context) =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//
//// SharedPreferences helpers (Gson)
//private val gson = Gson()
//
//// Users
//fun readSpUsers(context: Context): List<Patient> {
//    val json = getPrefs(context).getString(KEY_USERS, null) ?: return emptyList()
//    val type = object : TypeToken<List<Patient>>() {}.type
//    return gson.fromJson(json, type)
//}
//
//fun saveSpUser(context: Context, patient: Patient) {
//    // Load existing list, append the new patient, save the whole list back
//    val list = readSpUsers(context).toMutableList()
//    list.add(patient)
//    getPrefs(context).edit().putString(KEY_USERS, gson.toJson(list)).apply()
//}
//
//// Medications
//fun readSpMedications(context: Context, patientId: String): List<Medication> {
//    val json = getPrefs(context).getString(KEY_MEDICATIONS, null) ?: return emptyList()
//    val type = object : TypeToken<List<Medication>>() {}.type
//    val all: List<Medication> = gson.fromJson(json, type)
//    // Filter to only this patient's medications
//    return all.filter { it.patientId == patientId }
//}
//
//fun saveSpMedication(context: Context, med: Medication) {
//    // Read ALL medications (all patients), append, save back
//    val json = getPrefs(context).getString(KEY_MEDICATIONS, null)
//    val type = object : TypeToken<List<Medication>>() {}.type
//    val list: MutableList<Medication> =
//        if (json != null) gson.fromJson(json, type) else mutableListOf()
//    list.add(med)
//    getPrefs(context).edit().putString(KEY_MEDICATIONS, gson.toJson(list)).apply()
//}


// MainActivity shows the Welcome screen.
// on launch, if a session already exists jump straight to HomeActivity.


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MedTrackTheme {
                val context = LocalContext.current
                val app = application as MedTrackApplication
                val repository = app.repository

                val seedViewModel: DatabaseSeedViewModel = viewModel(
                    factory = DatabaseSeedViewModelFactory(repository)
                )

                val isSeeded by seedViewModel.isSeeded.collectAsState()
                val savedPatientId = context
                    .getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
                    .getString("logged_in_patient_id", "") ?: ""

                LaunchedEffect(Unit) {
                    seedViewModel.seedDatabase(context)
                }

                if (isSeeded) {
                    val navController: NavHostController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = if (savedPatientId.isNotBlank()) "home" else "welcome"                    ) {
                        composable("welcome") {
                            WelcomeScreen(navController)
                        }

                        composable("login") {
                            LoginScreen(navController, repository)
                        }

                        composable("claim_account") {
                            ClaimAccountScreen(navController, repository)
                        }

                        composable("sign_up") {
                            SignUpScreen(navController, repository)
                        }

                        composable("home") {
                            HomeScreen(navController, repository)
                        }

                        composable("add_medication") {
                            AddMedicationScreen(navController, repository)
                        }

                        composable("symptoms") {
                            SymptomsScreen(navController, repository)
                        }

                        composable("medcoach") {
                            MedCoachScreen(navController, repository)
                        }

                        composable("settings") {
                            SettingsScreen(navController, repository)
                        }

                        composable("clinician_login") {
                            ClinicianLoginScreen(navController)
                        }

                        composable("clinician_dashboard") {
                            ClinicianDashboardScreen(navController, repository)
                        }

                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}