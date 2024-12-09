package com.example.biofirebasestatio

sealed class NavRoutes(val route: String) {
    data object Authentication : NavRoutes("authentication")
    data object Welcome : NavRoutes("welcome")
}