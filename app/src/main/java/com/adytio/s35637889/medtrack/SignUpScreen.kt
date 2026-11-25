package com.adytio.s35637889.medtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adytio.s35637889.medtrack.data.repository.MedTrackRepository
import com.adytio.s35637889.medtrack.ui.viewmodel.SignUpViewModel
import com.adytio.s35637889.medtrack.ui.viewmodel.SignUpViewModelFactory

@Composable
fun SignUpScreen(
    navController: NavController,
    repository: MedTrackRepository
) {
    val viewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(repository)
    )

    val message by viewModel.message.collectAsState()
    val generatedPatientId by viewModel.generatedPatientId.collectAsState()

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    fun validatePhone(phone: String): String {
        return when {
            phone.isBlank() -> "Phone number is required."
            !phone.all { it.isDigit() } -> "Digits only."
            !phone.startsWith("04") -> "Must start with 04."
            phone.length != 10 -> "Must be exactly 10 digits."
            else -> ""
        }
    }

    fun validatePassword(pw: String): String {
        return when {
            pw.isBlank() -> "Password is required."
            pw.length < 8 -> "At least 8 characters."
            !pw.any { it.isLetter() } -> "Must contain a letter."
            !pw.any { it.isDigit() } -> "Must contain a number."
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
            text = "Sign Up",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Create a new MedTrack account.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = ""
            },
            label = { Text("Full Name") },
            isError = nameError.isNotEmpty(),
            supportingText = if (nameError.isNotEmpty()) {
                { Text(nameError) }
            } else null,
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
            placeholder = { Text("0412345678") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = phoneError.isNotEmpty(),
            supportingText = if (phoneError.isNotEmpty()) {
                { Text(phoneError) }
            } else null,
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
            label = { Text("Password") },
            isError = passwordError.isNotEmpty(),
            supportingText = if (passwordError.isNotEmpty()) {
                { Text(passwordError) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                nameError = if (name.isBlank()) "Name is required." else ""
                phoneError = validatePhone(phoneNumber)
                passwordError = validatePassword(password)

                if (
                    nameError.isEmpty() &&
                    phoneError.isEmpty() &&
                    passwordError.isEmpty()
                ) {
                    viewModel.signUpNewUser(
                        name = name,
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
            Text("Create Account")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message)
        }

        if (generatedPatientId.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Patient ID is:",
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = generatedPatientId,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Please save this ID. You need it to log in.",
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Login")
            }
        }

        TextButton(
            onClick = { navController.navigate("claim_account") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have a Patient ID? Claim account")
        }
    }
}