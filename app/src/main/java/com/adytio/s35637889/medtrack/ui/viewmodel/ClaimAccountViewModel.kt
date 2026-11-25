package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.util.normalizePhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClaimAccountViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun claimAccount(
        patientId: String,
        phoneNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            val patient = repository.findPatientForClaim(
                    patientId = patientId.trim(),
            phoneNumber = normalizePhone(phoneNumber)
            )


            when {
                patient == null -> {
                    _message.value = "Patient ID and phone number do not match."
                    _success.value = false
                }

                patient.isClaimed -> {
                    _message.value = "This account has already been claimed."
                    _success.value = false
                }

                else -> {
                    repository.claimPatientAccount(
                        patientId = patient.patientId,
                        password = password
                    )
                    _message.value = "Account claimed successfully. Please log in."
                    _success.value = true
                }
            }
        }
    }
}