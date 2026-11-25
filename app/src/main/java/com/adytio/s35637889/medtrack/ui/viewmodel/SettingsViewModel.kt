package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _patient = MutableStateFlow<PatientEntity?>(null)
    val patient: StateFlow<PatientEntity?> = _patient

    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            _patient.value = repository.getPatientById(patientId)
        }
    }
}