package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ViewModel for the Home screen
// loads patient info and their medications from Room
class HomeViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    // patient details for the greeting
    private val _patient = MutableStateFlow<PatientEntity?>(null)
    val patient: StateFlow<PatientEntity?> = _patient

    // list of medications shown on home screen
    private val _medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val medications: StateFlow<List<MedicationEntity>> = _medications

    // called when user toggles the taken switch
    fun updateTakenState(
        medicationId: Int,
        isTaken: Boolean,
        today: String
    ) {
        viewModelScope.launch {
            repository.updateMedicationTakenState(
                medicationId = medicationId,
                isTaken = isTaken,
                date = today
            )
        }
    }

    // fetches patient + their medications, called once when screen opens
    fun loadHomeData(patientId: String) {
        viewModelScope.launch {
            _patient.value = repository.getPatientById(patientId)

            repository.getMedicationsForPatient(patientId)
                .collect { meds ->
                    _medications.value = meds
                }
        }
    }
}