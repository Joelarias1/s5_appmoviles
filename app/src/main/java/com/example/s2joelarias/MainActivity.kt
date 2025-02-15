package com.example.s2joelarias

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//TESTING DE CON CONEXION
        try {
            val database = Firebase.database
            val testRef = database.getReference("test_connection")

            testRef.setValue("Test de conexión: ${System.currentTimeMillis()}")
                .addOnSuccessListener {
                    Log.d("FirebaseTest", "Conexión exitosa a Firebase ✓")
                    Toast.makeText(this, "Conexión a Firebase exitosa ✓", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseTest", "Error de conexión: ${e.message}")
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            Log.e("FirebaseTest", "Error al inicializar Firebase: ${e.message}")
            Toast.makeText(this, "Error al inicializar Firebase: ${e.message}", Toast.LENGTH_LONG).show()
        }

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