package com.adytio.s35637889.medtrack.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DatabaseSeedViewModel(
    private val repository: MedTrackRepository
) : ViewModel() {

    private val _isSeeded = MutableStateFlow(false)
    val isSeeded: StateFlow<Boolean> = _isSeeded

    fun seedDatabase(context: Context) {
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded(context)
            _isSeeded.value = true
        }
    }
}