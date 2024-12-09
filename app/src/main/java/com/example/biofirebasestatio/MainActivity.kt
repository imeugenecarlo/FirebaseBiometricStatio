package com.example.biofirebasestatio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biofirebasestatio.screens.Authentication
import com.example.biofirebasestatio.screens.Welcome
import com.example.biofirebasestatio.ui.theme.BioFirebaseStatioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioFirebaseStatioTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel = viewModel()

    NavHost(navController = navController, startDestination = NavRoutes.Authentication.route) {
        composable(NavRoutes.Authentication.route) {
            Authentication(
                user = authenticationViewModel.user,
                message = authenticationViewModel.message,
                signIn = { email, password -> authenticationViewModel.signIn(email, password) },
                register = { email, password -> authenticationViewModel.register(email, password) },
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BioFirebaseStatioTheme {
        MainScreen()
    }
}