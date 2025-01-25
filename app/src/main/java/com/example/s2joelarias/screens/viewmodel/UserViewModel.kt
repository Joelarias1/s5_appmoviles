package com.example.s2joelarias.screens.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.s2joelarias.model.Gender
import com.example.s2joelarias.model.User

class UserViewModel : ViewModel() {
    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

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