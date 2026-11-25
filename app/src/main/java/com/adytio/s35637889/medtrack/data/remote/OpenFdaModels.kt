package com.adytio.s35637889.medtrack.data.remote

data class OpenFdaResponse(
    val results: List<DrugLabelResult>?
)

data class DrugLabelResult(
    val purpose: List<String>?,
    val warnings: List<String>?,
    val dosage_and_administration: List<String>?
)