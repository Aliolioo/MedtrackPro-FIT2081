package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.local.entity.SymptomEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SymptomsViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _symptoms = MutableStateFlow<List<SymptomEntity>>(emptyList())
    val symptoms: StateFlow<List<SymptomEntity>> = _symptoms

    fun loadSymptoms(patientId: String) {
        viewModelScope.launch {
            repository.getSymptomsForPatient(patientId)
                .collect { symptomList ->
                    _symptoms.value = symptomList
                }
        }
    }

    fun addSymptom(symptom: SymptomEntity) {
        viewModelScope.launch {
            repository.insertSymptom(symptom)
        }
    }
}