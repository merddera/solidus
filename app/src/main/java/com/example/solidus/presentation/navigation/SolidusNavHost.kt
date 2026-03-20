package com.example.solidus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.solidus.presentation.add.AddTransactionScreen

@Composable
fun SolidusNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            com.example.solidus.presentation.main.MainScreen(parentNavController = navController)
        }

        composable("add_transaction") {
            AddTransactionScreen(
                transactionId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "edit_transaction/{id}",
            arguments = listOf(androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")
            AddTransactionScreen(
                transactionId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            com.example.solidus.presentation.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("analytics") {
            com.example.solidus.presentation.analytics.AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
