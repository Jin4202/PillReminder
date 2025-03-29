package com.example.pillreminder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Main : BottomNavItem("Main", Icons.Filled.Home, "main")
    object Pills : BottomNavItem("Pills", Icons.Filled.List, "pills")
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, "profile")
}