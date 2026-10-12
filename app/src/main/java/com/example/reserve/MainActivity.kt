package com.example.reserve

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.reserve.ui.navigation.ReserveNavGraph
import com.example.reserve.ui.theme.ReserveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReserveTheme {
                ReserveNavGraph()
            }
        }
    }
}
