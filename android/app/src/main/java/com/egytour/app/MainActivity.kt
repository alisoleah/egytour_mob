package com.egytour.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.egytour.app.ui.screens.LoginScreen
import com.egytour.app.ui.theme.EgytourTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EgytourTheme {
                LoginScreen()
            }
        }
    }
}
