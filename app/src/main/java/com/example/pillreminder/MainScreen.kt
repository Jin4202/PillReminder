package com.example.pillreminder

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillreminder.contents.BottomNavItem
import com.example.pillreminder.contents.BottomNavigationBar
import com.example.pillreminder.contents.MainScreenContent
import com.example.pillreminder.contents.PillsScreen
import com.example.pillreminder.contents.ProfileScreen
import com.example.pillreminder.reminder.Reminder

@Composable
fun MainScreen(reminders: List<Reminder>) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            reminders = reminders
        )
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier,
    reminders: List<Reminder>
) {
    NavHost(navController, startDestination = BottomNavItem.Main.route, modifier = modifier) {
        composable(BottomNavItem.Main.route) {
            MainScreenContent(reminders = reminders)
        }
        composable(BottomNavItem.Pills.route) {
            PillsScreen(reminders = reminders)
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}