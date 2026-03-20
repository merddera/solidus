package com.example.solidus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.solidus.presentation.add.AddTransactionScreen
import com.example.solidus.presentation.home.HomeScreen

@Composable
fun SolidusNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToAdd = { navController.navigate("add_transaction") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("add_transaction") {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            com.example.solidus.presentation.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
