package com.example.solidus.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home_tab", "Главная", Icons.Default.Home)
    object Transactions : BottomNavItem("transactions_tab", "Транзакции", Icons.Default.List)
    object Settings : BottomNavItem("settings_tab", "Настройки", Icons.Default.Settings)
}

@Composable
fun MainScreen(
    parentNavController: NavController
) {
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Transactions,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                com.example.solidus.presentation.home.HomeTab(
                    onNavigateToAdd = { parentNavController.navigate("add_transaction") },
                    onNavigateToEdit = { id -> parentNavController.navigate("edit_transaction/$id") }
                )
            }
            composable(BottomNavItem.Transactions.route) {
                com.example.solidus.presentation.transactions.TransactionsTab(
                    onNavigateToEdit = { id -> parentNavController.navigate("edit_transaction/$id") }
                )
            }
            composable(BottomNavItem.Settings.route) {
                com.example.solidus.presentation.settings.SettingsScreen(
                    onNavigateBack = { /* Handled via tabs, no back */ }
                )
            }
        }
    }
}
