package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.local.entity.PatientEntity
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _generatedPatientId = MutableStateFlow("")
    val generatedPatientId: StateFlow<String> = _generatedPatientId

    fun signUpNewUser(
        name: String,
        phoneNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            val existingPatient = repository.getPatientByPhone(phoneNumber.trim())

            if (existingPatient != null) {
                _message.value = "An account with this phone number already exists."
                return@launch
            }

            val newPatientId = repository.generateNextPatientId()

            val newPatient = PatientEntity(
                patientId = newPatientId,
                phoneNumber = phoneNumber.trim(),
                name = name.trim(),
                password = password,
                isClaimed = true
            )

            repository.insertPatient(newPatient)

            _generatedPatientId.value = newPatientId
            _message.value = "Account created successfully."
        }
    }
}