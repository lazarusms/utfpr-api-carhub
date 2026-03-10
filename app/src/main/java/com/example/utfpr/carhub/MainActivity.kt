package com.example.utfpr.carhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.utfpr.carhub.ui.theme.CarhubTheme
import com.example.utfpr.carhub.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarhubTheme {
                AppNavigation()
            }
        }
    }
}
