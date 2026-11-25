package com.adytio.s35637889.medtrack

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.SettingsViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val context = LocalContext.current

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(repository)
    )

    val patient by viewModel.patient.collectAsState()

    val patientId = context
        .getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
        .getString("logged_in_patient_id", "") ?: ""

    LaunchedEffect(patientId) {
        if (patientId.isNotBlank()) {
            viewModel.loadPatient(patientId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("symptoms") },
                    icon = { Icon(Icons.Filled.MonitorHeart, contentDescription = "Symptoms") },
                    label = { Text("Symptoms") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("medcoach") },
                    icon = {
                        Icon(Icons.Filled.HealthAndSafety, contentDescription = "MedCoach")
                    },
                    label = { Text("MedCoach") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Account Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SettingsInfoRow("Name", patient?.name ?: "Unknown")
                    SettingsInfoRow("Phone Number", patient?.phoneNumber ?: "Unknown")
                    SettingsInfoRow("Patient ID", patient?.patientId ?: patientId)
                }
            }

            Button(
                onClick = {
                    context.getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .remove("logged_in_patient_id")
                        .apply()

                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }

            OutlinedButton(
                onClick = {
                    navController.navigate("clinician_login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clinician Login")
            }
        }
    }
}

@Composable
fun SettingsInfoRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}