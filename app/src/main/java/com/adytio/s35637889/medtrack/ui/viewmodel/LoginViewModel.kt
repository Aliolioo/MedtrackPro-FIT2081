package com.adytio.s35637889.medtrack.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {


    // message shown under the login form (error or success)
    private val _loginMessage = MutableStateFlow("")
    val loginMessage: StateFlow<String> = _loginMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun login(
        context: Context,
        patientId: String,
        password: String
    ) {
        viewModelScope.launch {
            val patient = repository.login(patientId.trim(), password)

            if (patient != null) {
                // save session so user stays logged in after closing app
                context.getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("logged_in_patient_id", patient.patientId)
                    .apply()

                _loginMessage.value = "Login successful"
                _loginSuccess.value = true
            } else {
                _loginMessage.value = "Invalid Patient ID or password"
                _loginSuccess.value = false
            }
        }
    }
}