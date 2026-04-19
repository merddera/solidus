package com.example.solidus.presentation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconByName(iconName: String): ImageVector {
    return when (iconName) {
        "ic_food" -> Icons.Default.ShoppingCart
        "ic_transport" -> Icons.Default.Build
        "ic_salary" -> Icons.Default.Person
        "ic_home" -> Icons.Default.Home
        "ic_health" -> Icons.Default.Favorite
        "ic_shopping" -> Icons.Default.ShoppingCart
        "ic_card" -> Icons.Default.List
        else -> Icons.Default.Settings
    }
}
