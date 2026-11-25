package com.adytio.s35637889.medtrack

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.local.entity.SymptomEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.SymptomsViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.SymptomsViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: SymptomsViewModel = viewModel(
        factory = SymptomsViewModelFactory(repository)
    )

    val symptoms by viewModel.symptoms.collectAsState()

    val patientId = context
        .getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
        .getString("logged_in_patient_id", "") ?: ""

    var category by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(5f) }
    var notes by remember { mutableStateOf("") }
    var dateTimeInput by remember {
        mutableStateOf(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        )
    }

    var categoryExpanded by remember { mutableStateOf(false) }

    var categoryError by remember { mutableStateOf("") }
    var dateTimeError by remember { mutableStateOf("") }

    val categoryOptions = listOf(
        "Headache",
        "Fever",
        "Nausea",
        "Fatigue",
        "Dizziness",
        "Pain",
        "Cough",
        "Other"
    )

    fun clearForm() {
        category = ""
        severity = 5f
        notes = ""
        dateTimeInput = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )

        categoryError = ""
        dateTimeError = ""
    }

    LaunchedEffect(patientId) {
        if (patientId.isNotBlank()) {
            viewModel.loadSymptoms(patientId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Symptoms") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
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
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.MonitorHeart, contentDescription = "Symptoms") },
                    label = { Text("Symptoms") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("medcoach") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "MedCoach") },
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

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Log Symptom",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        isError = categoryError.isNotEmpty(),
                        supportingText = if (categoryError.isNotEmpty()) {
                            { Text(categoryError) }
                        } else null,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                    categoryError = ""
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Severity: ${severity.toInt()}/10",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Slider(
                    value = severity,
                    onValueChange = { severity = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mild",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Severe",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dateTimeInput,
                    onValueChange = {
                        dateTimeInput = it
                        dateTimeError = ""
                    },
                    label = { Text("Date & Time *") },
                    placeholder = { Text("yyyy-MM-dd HH:mm") },
                    isError = dateTimeError.isNotEmpty(),
                    supportingText = if (dateTimeError.isNotEmpty()) {
                        { Text(dateTimeError) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Optional") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            categoryError =
                                if (category.isBlank()) "Required" else ""

                            dateTimeError =
                                if (dateTimeInput.isBlank()) "Required" else ""

                            if (
                                categoryError.isEmpty() &&
                                dateTimeError.isEmpty()
                            ) {
                                viewModel.addSymptom(
                                    SymptomEntity(
                                        patientId = patientId,
                                        category = category,
                                        severity = severity.toInt(),
                                        notes = notes.trim(),
                                        dateTime = dateTimeInput.trim()
                                    )
                                )

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Symptom saved successfully."
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Symptom History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (symptoms.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No symptoms recorded yet.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(symptoms) { symptom ->
                    SymptomCard(symptom)
                }
            }
        }
    }
}

@Composable
fun SymptomCard(symptom: SymptomEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = symptom.category,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Severity: ${symptom.severity}/10",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = symptom.dateTime,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (symptom.notes.isNotBlank()) {
                Text(
                    text = symptom.notes,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}