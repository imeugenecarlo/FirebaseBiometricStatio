package com.example.biofirebasestatio.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Authentication(
    user: FirebaseUser? = null,
    message: String = "",
    signIn: (email: String, password: String) -> Unit = { _, _ -> },
    register: (email: String, password: String) -> Unit = { _, _ -> },
    enableBiometric: () -> Unit = {},
    showBiometricPrompt: (String, String) -> Unit = { _, _ -> },
    navigateToNextScreen: () -> Unit = {}
) {
    val emailStart = "" // TODO remove starting email
    val passwordStart = "" // TODO remove starting password
    var email by remember { mutableStateOf(emailStart) }
    var password by remember { mutableStateOf(passwordStart) }
    var emailIsError by remember { mutableStateOf(false) }
    var passwordIsError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = { Text("Authentication") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = emailIsError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            if (emailIsError) {
                Text("Invalid email", color = MaterialTheme.colorScheme.error)
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordIsError,
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Filled.Visibility, contentDescription = "Hide password")
                        } else {
                            Icon(Icons.Filled.VisibilityOff, contentDescription = "Show password")
                        }
                    }
                }
            )
            if (passwordIsError) {
                Text("Invalid password", color = MaterialTheme.colorScheme.error)
            }
            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.error)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    register(email, password)
                }) {
                    Text("Register")
                }
                Button(onClick = {
                    email = email.trim()
                    if (email.isEmpty() || !validateEmail(email)) {
                        emailIsError = true
                        return@Button
                    } else {
                        emailIsError = false
                    }
                    password = password.trim()
                    if (password.isEmpty()) {
                        passwordIsError = true
                        return@Button
                    } else {
                        passwordIsError = false
                    }
                    signIn(email, password)
                    enableBiometric()
                    showBiometricPrompt("Biometric Authentication", "Login to your account using biometrics")
                }) {
                    Text("Sign in")
                }
            }
        }
    }
}

private fun validateEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    Authentication()
}