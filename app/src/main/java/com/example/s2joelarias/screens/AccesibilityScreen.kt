package com.example.s2joelarias.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.s2joelarias.screens.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(navController: NavController, viewModel: UserViewModel) {
    var accessibilityOption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var savedData by remember { mutableStateOf<Map<String, String>?>(null) }

    LaunchedEffect(Unit) {
        val currentUser = viewModel.getCurrentUser()
        currentUser?.let { user ->
            viewModel.getAccessibilityData(
                userId = user.uid,
                onSuccess = { data ->
                    savedData = data
                    accessibilityOption = data["accessibilityOption"] ?: ""
                    description = data["description"] ?: ""
                },
                onFailure = { e ->
                    // Manejar el error
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accesibilidad") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = accessibilityOption,
                    onValueChange = { accessibilityOption = it },
                    label = { Text("Opción de accesibilidad") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Button(
                    onClick = {
                        viewModel.saveAccessibilityData(accessibilityOption, description)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }

                if (savedData != null) {
                    Text("Datos guardados: $savedData")
                }
            }
        }
    )
}