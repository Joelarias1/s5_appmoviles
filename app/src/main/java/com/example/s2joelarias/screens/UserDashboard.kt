package com.example.s2joelarias.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.s2joelarias.screens.viewmodel.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

private fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: UserViewModel
) {
    try {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        viewModel.updateLocation(it)
                    } ?: run {
                        Toast.makeText(
                            context,
                            "No se pudo obtener la ubicación. Activa el GPS",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Error al obtener ubicación: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun openGoogleMaps(context: Context, location: Location) {
    try {
        val gmmIntentUri = Uri.parse(
            "geo:${location.latitude},${location.longitude}?q=cajeros+automaticos+cerca"
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Si Google Maps no está instalado
            Toast.makeText(
                context,
                "Por favor instala Google Maps para ver los cajeros cercanos",
                Toast.LENGTH_LONG
            ).show()

            try {
                // Intenta abrir Play Store
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                // Si Play Store no está disponible, abre en navegador
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
                    )
                )
            }
        }
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error al abrir el mapa: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
private fun CategoryItem(
    icon: ImageVector,
    name: String,
    amount: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Categoría $name: $amount"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = "Ícono de categoría $name",
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            amount,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun getCategoryIcon(iconHash: Int): ImageVector {
    return when (iconHash) {
        Icons.Default.Restaurant.hashCode() -> Icons.Default.Restaurant
        Icons.Default.DirectionsCar.hashCode() -> Icons.Default.DirectionsCar
        Icons.Default.Home.hashCode() -> Icons.Default.Home
        Icons.Default.LocalHospital.hashCode() -> Icons.Default.LocalHospital
        else -> Icons.Default.ShoppingCart
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(navController: NavController, viewModel: UserViewModel) {
    val users by viewModel.users
    val totalExpenses = viewModel.calculateTotal(viewModel.expenses)

    // Ubicación
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var showATMDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (locationGranted) {
            getCurrentLocation(context, fusedLocationClient, viewModel)
        } else {
            Toast.makeText(context, "Se necesitan permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = "Ícono de finanzas",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Mis Finanzas",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        viewModel.currentUserName.value?.let { userName ->
                            Text(
                                text = "¡Hola, $userName!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                actions = {
                    // Botón de ubicación
                    IconButton(onClick = { showATMDialog = true }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Buscar cajeros",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Botón de cerrar sesión
                    IconButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.currentUserName.value?.let { userName ->
                        Text(
                            "¡Bienvenido/a $userName!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Gestiona tus finanzas de manera fácil y accesible",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Resumen Financiero
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Presupuesto
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Presupuesto",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    "$150,000",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        // Gastos
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Gastos",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    "$$totalExpenses",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFFE91E63)
                                )
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = (totalExpenses / 150000).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Categorías de Gastos
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Categorías de Gastos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar categoría",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Divider()
                    viewModel.expenses.forEach { expense ->
                        CategoryItem(
                            icon = getCategoryIcon(expense.icon),
                            name = expense.category,
                            amount = "$${expense.amount}",
                            color = Color(expense.color)
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Herramientas de Accesibilidad",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("accessibility") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Datos de user")
                        }
                        Button(
                            onClick = { /* TODO: Tutorial */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Help, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tutorial")
                        }
                    }
                }
            }

            // Mostrar ubicación si está disponible
            viewModel.location.value?.let { location ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Mi ubicación actual",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            TextButton(
                                onClick = {
                                    location?.let { loc ->
                                        openGoogleMaps(context, loc)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver cajeros")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Lat: ${location.latitude}")
                        Text("Lon: ${location.longitude}")
                    }
                }

            }
        }

    }



    // Diálogo de ATMs
    if (showATMDialog) {
        AlertDialog(
            onDismissRequest = { showATMDialog = false },
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            title = { Text("Buscar Cajeros Cercanos") },
            text = { Text("¿Deseas buscar cajeros automáticos cerca de tu ubicación?") },
            modifier = Modifier.semantics {
                contentDescription = "Diálogo para buscar cajeros automáticos cercanos"
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                        showATMDialog = false
                    }
                ) {
                    Text("Buscar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showATMDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}