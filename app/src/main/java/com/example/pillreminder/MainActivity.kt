package com.example.pillreminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillreminder.model.nav.BottomNavItem
import com.example.pillreminder.card.BottomNavigationBar
import com.example.pillreminder.model.reminder.ReminderBroadcastReceiver
import com.example.pillreminder.model.reminder.ReminderManager
import com.example.pillreminder.model.reminder.createNotificationChannel
import com.example.pillreminder.model.reminder.scheduleReminder
import com.example.pillreminder.screen.CameraScreen
import com.example.pillreminder.screen.PillsScreen
import com.example.pillreminder.screen.ProfileScreen
import com.example.pillreminder.screen.ReminderScreen
import com.example.pillreminder.viewmodel.UserViewModel
import com.example.pillreminder.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        createNotificationChannel(this)
        enableEdgeToEdge()

        setContent {
            MainScreen()
            //TestReminderButton(this)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier
) {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory()
    )
    LaunchedEffect(Unit) {
        viewModel.fetchReminders(context) { _ ->
            launch {
                val manager = ReminderManager.getInstance()
                manager.loadFromDataStore(context)
                manager.getReminders().forEach {
                    scheduleReminder(context, it)
                }
            }
        }
    }

    NavHost(navController, startDestination = BottomNavItem.Main.route, modifier = modifier) {
        composable(BottomNavItem.Camera.route) {
            CameraScreen(
                onSaved = {
                    navController.navigate(BottomNavItem.Main.route)
                }
            )
        }
        composable(BottomNavItem.Main.route) {
            ReminderScreen(
                updateData= {
                    viewModel.updateReminders()
                }
            )
        }
        composable(BottomNavItem.Pills.route) {
            PillsScreen(
                updateData= {
                    viewModel.updateReminders()
                }
            )
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                loadData = {
                    viewModel.fetchReminders(context)
                },
                updateData = {
                    viewModel.updateReminders()
                }
            )
        }
    }
}

@Composable
fun TestReminderButton(context: Context) {
    Button(onClick = {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12+ check for exact alarm permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            return@Button
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("PILL_NAME", "Test Pill")
            putExtra("NOTIFICATION_ID", 12345)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            12345,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 5000 // 5 seconds from now

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }) {
        Text("Send Test Reminder in 5 sec")
    }
}


