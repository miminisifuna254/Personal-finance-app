package com.dailyplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyplanner.ui.screens.PlannerScreen
import com.dailyplanner.ui.theme.DailyPlannerTheme
import com.dailyplanner.viewmodel.PlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyPlannerTheme {
                Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    PlannerScreen(viewModel = viewModel())
                }
            }
        }
    }
}
