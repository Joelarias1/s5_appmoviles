package com.example.s2joelarias.screens.viewmodel

import android.location.Location
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private val _expenses = mutableStateListOf<CategoryExpense>()
    val expenses: List<CategoryExpense> = _expenses

    private val _registrationState = mutableStateOf<RegistrationState>(RegistrationState.Idle)
    val registrationState: State<RegistrationState> = _registrationState

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    // location
    private val _location = mutableStateOf<Location?>(null)
    val location: State<Location?> = _location

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

    fun registerUser(email: String, password: String, gender: Gender, acceptedTerms: Boolean) {
        if (!acceptedTerms) {
            _registrationState.value = RegistrationState.Error("Debes aceptar los términos y condiciones")
            return
        }

        _registrationState.value = RegistrationState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    val user = User(
                        uid = firebaseUser?.uid ?: "",
                        email = email,
                        gender = gender,
                        acceptedTerms = acceptedTerms
                    )

                    firebaseUser?.uid?.let { uid ->
                        usersRef.child(uid).setValue(user.toMap())
                            .addOnSuccessListener {
                                _registrationState.value = RegistrationState.Success
                                _users.value = _users.value + user
                            }
                            .addOnFailureListener { e ->
                                _registrationState.value = RegistrationState.Error(
                                    e.message ?: "Error al guardar datos"
                                )
                            }
                    }
                } else {
                    _registrationState.value = RegistrationState.Error(
                        task.exception?.message ?: "Error en el registro"
                    )
                }
            }
    }

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(
                        task.exception?.message ?: "Error al iniciar sesión"
                    )
                }
            }
    }

    fun validatePasswords(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.length >= 6
    }

    fun logout() {
        auth.signOut()
        _loginState.value = LoginState.Idle
    }

    fun getCurrentUser(): User? {
        return auth.currentUser?.let { firebaseUser ->
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                password = "",
                gender = null
            )
        }
    }

    fun updateLoginState(state: LoginState) {
        _loginState.value = state
    }

    fun updateRegistrationState(state: RegistrationState) {
        _registrationState.value = state
    }

    fun updateLocation(newLocation: Location) {
        _location.value = newLocation
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}