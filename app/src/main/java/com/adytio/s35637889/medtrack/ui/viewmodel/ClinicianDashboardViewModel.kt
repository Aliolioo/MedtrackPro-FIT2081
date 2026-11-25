package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.adytio.s35637889.medtrack.data.repository.ClinicianDashboardStats
//import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.ClinicianDashboardViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.ClinicianDashboardViewModelFactory
class ClinicianDashboardViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<MedTrackRepository.ClinicianDashboardStats?>(null)
    val stats: StateFlow<MedTrackRepository.ClinicianDashboardStats?> = _stats

    private val _insights = MutableStateFlow("")
    val insights: StateFlow<String> = _insights

    private val _loadingInsights = MutableStateFlow(false)
    val loadingInsights: StateFlow<Boolean> = _loadingInsights

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadStats() {
        viewModelScope.launch {
            _stats.value = repository.getClinicianDashboardStats()
        }
    }

    fun findPatterns() {
        viewModelScope.launch {
            val currentStats = _stats.value

            if (currentStats == null) {
                _error.value = "Dashboard statistics are not loaded yet."
                return@launch
            }

            _loadingInsights.value = true
            _error.value = ""

            val result = repository.generateClinicianInsights(currentStats)

            if (result != null) {
                _insights.value = result
            } else {
                _error.value = "Failed to generate insights."
            }

            _loadingInsights.value = false
        }
    }
}