package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.local.entity.MedCoachTipEntity
import com.adytio.s35637889.medtrack.data.remote.DrugLabelResult
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

// Handles both drug info lookup (OpenFDA) and AI tip generation (Gemini)
class MedCoachViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {
    // result from OpenFDA API
    private val _drugInfo = MutableStateFlow<DrugLabelResult?>(null)
    val drugInfo: StateFlow<DrugLabelResult?> = _drugInfo
    // patient's medication names for the dropdown
    private val _medicationNames = MutableStateFlow<List<String>>(emptyList())
    val medicationNames: StateFlow<List<String>> = _medicationNames
    // previously generated tips (for the "Show All Tips" dialog)
    private val _tips = MutableStateFlow<List<MedCoachTipEntity>>(emptyList())
    val tips: StateFlow<List<MedCoachTipEntity>> = _tips
    // the latest AI-generated tip
    private val _generatedTip = MutableStateFlow("")
    val generatedTip: StateFlow<String> = _generatedTip

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error


    // populates the dropdown with this patient's medication names
    fun loadMedicationNames(patientId: String) {
        viewModelScope.launch {
            repository.getMedicationsForPatient(patientId)
                .collect { medications ->
                    _medicationNames.value = medications
                        .map { it.medicationName }
                        .distinct()
                }
        }
    }

    fun loadTips(patientId: String) {
        viewModelScope.launch {
            repository.getMedCoachTipsForPatient(patientId)
                .collect { tipsList ->
                    _tips.value = tipsList
                }
        }
    }

    // calls OpenFDA API via repository
    fun searchDrug(drugName: String) {
        if (drugName.isBlank()) {
            _error.value = "Please enter a medication name."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            _drugInfo.value = null

            val result = repository.searchDrugInfo(drugName.trim())

            if (result == null) {
                _error.value = "No medication information found."
            }

            _drugInfo.value = result
            _loading.value = false
        }
    }

    // generates a personalised tip using Gemini API
    // pulls the patient's meds and symptoms first so the tip isn't generic
    fun generateAiTip(
        patientId: String,
        medicationName: String
    ) {
        if (medicationName.isBlank()) {
            _error.value = "Please enter or select a medication first."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = ""

            val medications = repository
                .getMedicationsForPatient(patientId)
                .first()

            val symptoms = repository
                .getSymptomsForPatient(patientId)
                .first()

            val medicationNames = medications.map { it.medicationName }

            val symptomSummary = symptoms.take(5).map {
                "${it.category} severity ${it.severity}/10"
            }

            val result = repository.generateMedicationTip(
                selectedMedication = medicationName.trim(),
                medicationNames = medicationNames,
                symptomSummary = symptomSummary
            )

            if (result != null) {
                _generatedTip.value = result

                // save the tip to Room so it shows up in "Show All Tips"
                repository.insertMedCoachTip(
                    MedCoachTipEntity(
                        patientId = patientId,
                        tipText = result,
                        createdAt = LocalDateTime.now().toString()
                    )
                )
            } else {
                _error.value = "Failed to generate AI tip."
            }

            _loading.value = false
        }
    }
}