package com.adytio.s35637889.medtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.ClinicianDashboardViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.ClinicianDashboardViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianDashboardScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val viewModel: ClinicianDashboardViewModel = viewModel(
        factory = ClinicianDashboardViewModelFactory(repository)
    )

    val stats by viewModel.stats.collectAsState()
    val insights by viewModel.insights.collectAsState()
    val loadingInsights by viewModel.loadingInsights.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinician Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
    {
            Text(
                text = "Aggregate Patient Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            if (stats == null) {
                CircularProgressIndicator()
            } else {
                val currentStats = stats!!

                StatCard(
                    title = "Total Patients",
                    value = currentStats.totalPatients.toString()
                )

                StatCard(
                    title = "Average Medications per Patient",
                    value = "%.2f".format(currentStats.averageMedicationsPerPatient)
                )

                StatCard(
                    title = "Most Common Symptom Category",
                    value = currentStats.mostCommonSymptomCategory
                )

                StatCard(
                    title = "Average Symptom Severity",
                    value = "%.2f / 10".format(currentStats.averageSymptomSeverity)
                )

                Button(
                    onClick = {
                        viewModel.findPatterns()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Find Patterns")
                }
            }
            if (loadingInsights) {
                CircularProgressIndicator()
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

            if (insights.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "AI Pattern Insights",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = insights,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 20.sp
                        )
                    }
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

@Composable
fun StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}