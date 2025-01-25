package com.example.s2joelarias.screens.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.s2joelarias.model.CategoryExpense
import com.example.s2joelarias.model.Gender
import com.example.s2joelarias.model.User

class UserViewModel : ViewModel() {
    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private val _expenses = mutableStateListOf<CategoryExpense>()
    val expenses: List<CategoryExpense> = _expenses

    // Lambda para calcular total de gastos
    val calculateTotal = { expenses: List<CategoryExpense> ->
        expenses.sumOf { it.amount }
    }

    init {
        _expenses.addAll(
            listOf(
                CategoryExpense("Alimentación", 35000.0, Icons.Default.Restaurant.hashCode(), 0xFFFF9800),
                CategoryExpense("Transporte", 15000.0, Icons.Default.DirectionsCar.hashCode(), 0xFF2196F3),
                CategoryExpense("Servicios", 25000.0, Icons.Default.Home.hashCode(), 0xFF4CAF50),
                CategoryExpense("Salud", 10000.0, Icons.Default.LocalHospital.hashCode(), 0xFFE91E63)
            )
        )
    }

    fun addUser(email: String, password: String, gender: Gender): Boolean {
        if (_users.value.size >= 5) return false
        if (_users.value.any { it.email == email }) return false

        val user = User(email, password, gender)
        _users.value = _users.value + user
        return true
    }

    fun validateLogin(email: String, password: String): Boolean {
        return _users.value.any { it.email == email && it.password == password }
    }

    fun validatePasswords(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}