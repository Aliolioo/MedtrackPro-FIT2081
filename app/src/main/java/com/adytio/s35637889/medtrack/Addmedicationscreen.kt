package com.adytio.s35637889.medtrack

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.AddMedicationViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.AddMedicationViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: AddMedicationViewModel = viewModel(
        factory = AddMedicationViewModelFactory(repository)
    )

    val patientId = context
        .getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
        .getString("logged_in_patient_id", "") ?: ""

    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }
    var medicationType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var frequencyExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var dosageError by remember { mutableStateOf("") }
    var frequencyError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }

    val frequencyOptions = listOf(
        "Once daily",
        "Twice daily",
        "Three times daily",
        "As needed"
    )

    val medicationTypeOptions = listOf(
        "Tablet",
        "Capsule",
        "Liquid",
        "Injection",
        "Topical",
        "Other"
    )

    fun clearForm() {
        medicationName = ""
        dosage = ""
        frequency = ""
        scheduledTime = ""
        medicationType = ""
        notes = ""

        nameError = ""
        dosageError = ""
        frequencyError = ""
        timeError = ""
        typeError = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medication") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "New Medication",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = medicationName,
                onValueChange = {
                    medicationName = it
                    nameError = ""
                },
                label = { Text("Medication Name *") },
                isError = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = dosage,
                onValueChange = {
                    dosage = it
                    dosageError = ""
                },
                label = { Text("Dosage *") },
                placeholder = { Text("e.g. 250mg") },
                isError = dosageError.isNotEmpty(),
                supportingText = if (dosageError.isNotEmpty()) {
                    { Text(dosageError) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = frequencyExpanded,
                onExpandedChange = { frequencyExpanded = it }
            ) {
                OutlinedTextField(
                    value = frequency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Frequency *") },
                    isError = frequencyError.isNotEmpty(),
                    supportingText = if (frequencyError.isNotEmpty()) {
                        { Text(frequencyError) }
                    } else null,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(frequencyExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = frequencyExpanded,
                    onDismissRequest = { frequencyExpanded = false }
                ) {
                    frequencyOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                frequency = option
                                frequencyExpanded = false
                                frequencyError = ""
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = scheduledTime,
                onValueChange = {
                    scheduledTime = it
                    timeError = ""
                },
                label = { Text("Scheduled Time *") },
                placeholder = { Text("e.g. 08:00") },
                isError = timeError.isNotEmpty(),
                supportingText = if (timeError.isNotEmpty()) {
                    { Text(timeError) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = medicationType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Medication Type *") },
                    isError = typeError.isNotEmpty(),
                    supportingText = if (typeError.isNotEmpty()) {
                        { Text(typeError) }
                    } else null,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    medicationTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                medicationType = option
                                typeExpanded = false
                                typeError = ""
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { clearForm() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear")
                }

                Button(
                    onClick = {
                        nameError = if (medicationName.isBlank()) "Required" else ""
                        dosageError = if (dosage.isBlank()) "Required" else ""
                        frequencyError = if (frequency.isBlank()) "Required" else ""
                        timeError = if (scheduledTime.isBlank()) "Required" else ""
                        typeError = if (medicationType.isBlank()) "Required" else ""

                        if (
                            nameError.isEmpty() &&
                            dosageError.isEmpty() &&
                            frequencyError.isEmpty() &&
                            timeError.isEmpty() &&
                            typeError.isEmpty()
                        ) {
                            viewModel.addMedication(
                                MedicationEntity(
                                    patientId = patientId,
                                    medicationName = medicationName.trim(),
                                    dosage = dosage.trim(),
                                    frequency = frequency,
                                    scheduledTime = scheduledTime.trim(),
                                    medicationType = medicationType,
                                    notes = notes.trim()
                                )
                            )

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Medication added successfully."
                                )
                            }

                            clearForm()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save")
                }
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back")
            }
        }
    }
}