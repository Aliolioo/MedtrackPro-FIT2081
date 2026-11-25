package com.adytio.s35637889.medtrack

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.MedCoachViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.MedCoachViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedCoachScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val viewModel: MedCoachViewModel = viewModel(
        factory = MedCoachViewModelFactory(repository)
    )

    val drugInfo by viewModel.drugInfo.collectAsState()
    val medicationNames by viewModel.medicationNames.collectAsState()
    val tips by viewModel.tips.collectAsState()
    val generatedTip by viewModel.generatedTip.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val context = LocalContext.current

    val patientId = context
        .getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
        .getString("logged_in_patient_id", "") ?: ""

    var searchText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showTipsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        if (patientId.isNotBlank()) {
            viewModel.loadMedicationNames(patientId)
            viewModel.loadTips(patientId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MedCoach") },
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
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.HealthAndSafety, contentDescription = "MedCoach") },
                    label = { Text("MedCoach") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("settings") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Medication Information Search",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Search medication information using OpenFDA, or generate a personalised AI adherence tip.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Medication Name") },
                    placeholder = { Text("e.g. Ibuprofen") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    if (medicationNames.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No saved medications yet") },
                            onClick = { dropdownExpanded = false }
                        )
                    } else {
                        medicationNames.forEach { medicationName ->
                            DropdownMenuItem(
                                text = { Text(medicationName) },
                                onClick = {
                                    searchText = medicationName
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.searchDrug(searchText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Search")
            }

            Button(
                onClick = {
                    viewModel.generateAiTip(
                        patientId = patientId,
                        medicationName = searchText
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate AI Tip")
            }

            OutlinedButton(
                onClick = { showTipsDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Show All Tips")
            }

            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (error.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (generatedTip.isNotEmpty()) {
                InfoCard(
                    title = "AI Medication Tip",
                    content = generatedTip
                )
            }

            drugInfo?.let { info ->
                InfoCard(
                    title = "Purpose",
                    content = info.purpose?.firstOrNull() ?: "Not available"
                )

                InfoCard(
                    title = "Dosage & Administration",
                    content = info.dosage_and_administration?.firstOrNull() ?: "Not available"
                )

                InfoCard(
                    title = "Warnings",
                    content = info.warnings?.firstOrNull() ?: "Not available"
                )
            }
        }
    }

    if (showTipsDialog) {
        AlertDialog(
            onDismissRequest = { showTipsDialog = false },
            title = { Text("Previous AI Tips") },
            text = {
                if (tips.isEmpty()) {
                    Text("No tips generated yet.")
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        tips.forEach { tip ->
                            Text("• ${tip.tipText}")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showTipsDialog = false }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = content,
                lineHeight = 22.sp
            )
        }
    }
}