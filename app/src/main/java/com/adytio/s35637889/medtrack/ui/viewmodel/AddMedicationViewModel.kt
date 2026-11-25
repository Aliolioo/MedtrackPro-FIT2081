package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.local.entity.MedicationEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddMedicationViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun addMedication(medication: MedicationEntity) {
        viewModelScope.launch {
            repository.insertMedication(medication)
            _saved.value = true
        }
    }
}