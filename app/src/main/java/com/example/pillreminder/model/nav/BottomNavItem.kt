package com.example.pillreminder.model.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Camera : BottomNavItem("Camera", Icons.Filled.Camera, "camera")
    object Main : BottomNavItem("Main", Icons.Filled.Home, "main")
    object Pills : BottomNavItem("Pills", Icons.AutoMirrored.Filled.List, "pills")
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, "profile")
}