package dev.fivestar.happycalender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dev.fivestar.happycalender.ui.theme.HappyCalenderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = getSharedPreferences("calendar_prefs", MODE_PRIVATE)
        val viewModel = CalendarViewModel(CalendarRepositoryLocal(sharedPreferences))
        setContent {
            HappyCalenderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdventCalendarScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}