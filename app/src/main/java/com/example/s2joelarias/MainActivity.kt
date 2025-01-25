package com.example.s2joelarias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.s2joelarias.ui.theme.S2joelariasTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Screens; Pantallas o vistas
import com.example.s2joelarias.screens.LoginScreen
import com.example.s2joelarias.screens.RecoveryScreen
import com.example.s2joelarias.screens.RegisterScreen
import com.example.s2joelarias.screens.UserDashboardScreen
import com.example.s2joelarias.screens.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S2joelariasTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(navController, userViewModel)
                    }

                    composable("register") {
                        RegisterScreen(navController, userViewModel)
                    }

                    composable("recovery") {
                        RecoveryScreen(navController)
                    }
                    composable("dashboard") {
                        UserDashboardScreen(navController, userViewModel)
                    }
                }
            }
        }
    }
}