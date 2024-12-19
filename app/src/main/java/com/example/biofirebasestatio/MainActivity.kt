package com.example.biofirebasestatio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.example.biofirebasestatio.BiometricPromptManager.*
import com.example.biofirebasestatio.screens.Authentication
import com.example.biofirebasestatio.screens.Welcome

import com.example.biofirebasestatio.ui.theme.BioFirebaseStatioTheme

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFirebaseStatioTheme {
                val biometricResult by promptManager.promptResult.collectAsState(null)

                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        println("Activity result: $it")
                    }
                )
                MainScreen(
                    showBiometricPrompt = { title, description ->
                        promptManager.showBiometricPrompt(title, description)
                    },
                    biometricResult = biometricResult,
                    enrollLauncher = enrollLauncher
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    showBiometricPrompt: (String, String) -> Unit,
    biometricResult: BiometricResult?,
    enrollLauncher: ActivityResultLauncher<Intent>
) {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel = viewModel()

    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.AuthenticationNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val enrollment = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollment)
            }
        } else if (biometricResult is BiometricResult.AuthenticationSuccess) {
            navController.navigate(NavRoutes.Welcome.route)
        }
    }

    NavHost(navController = navController, startDestination = NavRoutes.Authentication.route) {
        composable(NavRoutes.Authentication.route) {
            Authentication(
                user = authenticationViewModel.user,
                message = authenticationViewModel.message,
                signIn = { email, password ->
                    authenticationViewModel.signIn(email, password)
                    navController.navigate(NavRoutes.Welcome.route)
                },
                register = { email, password -> authenticationViewModel.register(email, password) },
                enableBiometric = {
                    authenticationViewModel.enableBiometric()
                    showBiometricPrompt("Biometric Authentication", "Login to your account using biometrics")
                },
                navigateToNextScreen = { navController.navigate(NavRoutes.Welcome.route) }
            )
        }
        composable(NavRoutes.Welcome.route) {
            Welcome(
                user = authenticationViewModel.user,
                signOut = { authenticationViewModel.signOut() },
                navigateToAuthentication = {
                    navController.popBackStack(NavRoutes.Authentication.route, inclusive = false)
                })
        }
    }
}