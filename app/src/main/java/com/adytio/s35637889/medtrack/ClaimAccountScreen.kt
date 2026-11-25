package com.adytio.s35637889.medtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.ClaimAccountViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.ClaimAccountViewModelFactory

@Composable
fun ClaimAccountScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val signUpViewModel: ClaimAccountViewModel = viewModel(
        factory = ClaimAccountViewModelFactory(repository)
    )

    val message by signUpViewModel.message.collectAsState()
    val success by signUpViewModel.success.collectAsState()

    var patientId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var patientIdError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }

    LaunchedEffect(success) {
        if (success) {
            navController.navigate("login") {
                popUpTo("sign_up") { inclusive = true }
            }
        }
    }

    fun validatePhone(phone: String): String {
        val trimmed = phone.trim()
        return when {
            trimmed.isBlank() -> "Phone number is required."
            !trimmed.all { it.isDigit() } -> "Digits only."
            !trimmed.startsWith("04") -> "Must start with 04."
            trimmed.length != 10 -> "Must be exactly 10 digits."
            else -> ""
        }
    }

    fun validatePassword(pw: String): String {
        return when {
            pw.isBlank() -> "Password is required."
            pw.length < 8 -> "At least 8 characters required."
            !pw.any { it.isLetter() } -> "Must contain at least one letter."
            !pw.any { it.isDigit() } -> "Must contain at least one number."
            else -> ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Claim Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Enter your Patient ID and phone number, then create a password.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = patientId,
            onValueChange = {
                patientId = it
                patientIdError = ""
            },
            label = { Text("Patient ID") },
            placeholder = { Text("e.g. P1001") },
            isError = patientIdError.isNotEmpty(),
            supportingText = if (patientIdError.isNotEmpty()) {
                { Text(patientIdError) }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it.filter { ch -> ch.isDigit() }.take(10)
                phoneError = ""
            },
            label = { Text("Phone Number") },
            placeholder = { Text("e.g. 0412345678") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = phoneError.isNotEmpty(),
            supportingText = if (phoneError.isNotEmpty()) {
                { Text(phoneError) }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ""
            },
            label = { Text("Create Password") },
            visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector =
                            if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password"
                    )
                }
            },
            isError = passwordError.isNotEmpty(),
            supportingText = if (passwordError.isNotEmpty()) {
                { Text(passwordError) }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmError = ""
            },
            label = { Text("Confirm Password") },
            visualTransformation =
                if (confirmVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                    Icon(
                        imageVector =
                            if (confirmVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle confirm password"
                    )
                }
            },
            isError = confirmError.isNotEmpty(),
            supportingText = if (confirmError.isNotEmpty()) {
                { Text(confirmError) }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (message.isNotEmpty() && !success) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                patientIdError =
                    if (patientId.isBlank()) "Patient ID is required." else ""

                phoneError = validatePhone(phoneNumber)
                passwordError = validatePassword(password)

                confirmError = when {
                    confirmPassword.isBlank() -> "Please confirm your password."
                    confirmPassword != password -> "Passwords do not match."
                    else -> ""
                }

                if (
                    patientIdError.isEmpty() &&
                    phoneError.isEmpty() &&
                    passwordError.isEmpty() &&
                    confirmError.isEmpty()
                ) {
                    signUpViewModel.claimAccount(
                        patientId = patientId,
                        phoneNumber = phoneNumber,
                        password = password
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Claim Account", fontSize = 16.sp)
        }

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already claimed? Log in")
        }
    }
}