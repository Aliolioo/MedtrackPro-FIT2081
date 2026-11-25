package com.adytio.s35637889.medtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ClinicianLoginScreen(navController: NavController) {
    var accessKey by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Clinician Login",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = accessKey,
            onValueChange = {
                accessKey = it
                error = ""
            },
            label = { Text("Access Key") },
            visualTransformation = PasswordVisualTransformation(),
            isError = error.isNotEmpty(),
            supportingText = if (error.isNotEmpty()) {
                { Text(error) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (accessKey == "dollar-entry-apples") {
                    navController.navigate("clinician_dashboard")
                } else {
                    error = "Incorrect access key."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login")
        }

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}