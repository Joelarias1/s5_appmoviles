package com.example.s2joelarias.model

enum class Gender {
    MASCULINO,
    FEMENINO,
    OTRO
}

data class User(
    val email: String,
    val password: String,
    val gender: Gender?,
    val acceptedTerms: Boolean = false,
    val id: Int? = null
)