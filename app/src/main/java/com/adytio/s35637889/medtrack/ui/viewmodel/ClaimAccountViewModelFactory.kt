package com.adytio.s35637889.medtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository

class ClaimAccountViewModelFactory(
    private val repository: MedTrackRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClaimAccountViewModel(repository) as T
    }
}