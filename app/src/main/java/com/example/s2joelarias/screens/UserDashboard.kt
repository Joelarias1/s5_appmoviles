package com.example.s2joelarias.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.s2joelarias.screens.viewmodel.UserViewModel
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


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
    val lastUser = users.lastOrNull()
    val totalExpenses = viewModel.calculateTotal(viewModel.expenses)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Botón de cerrar sesión"
                        }
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Ícono de cerrar sesión",
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Resumen financiero mensual" },
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Ícono de presupuesto",
                                tint = Color(0xFF4CAF50)
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
                                    modifier = Modifier.semantics {
                                        contentDescription = "Presupuesto: 150,000 pesos"
                                    }
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Ícono de gastos",
                                tint = Color(0xFFE91E63)
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
                                    modifier = Modifier.semantics {
                                        contentDescription = "Gastos: $totalExpenses pesos"
                                    }
                                )
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = (totalExpenses / 150000).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .semantics {
                                contentDescription = "Barra de progreso: ${(totalExpenses / 150000 * 100).toInt()}% del presupuesto gastado"
                            }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Lista de categorías de gastos" },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Categorías de Gastos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics {
                            contentDescription = "Título: Categorías de gastos"
                        }
                    )
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

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Botón para agregar nuevo gasto"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Ícono de agregar",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Agregar Gasto",
                    color = Color.White
                )
            }
        }
    }
}