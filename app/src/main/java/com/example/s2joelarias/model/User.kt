package com.example.s2joelarias.model

enum class Gender {
    MASCULINO,
    FEMENINO,
    OTRO
}

data class User(
    val uid: String = "",       // ID único de Firebase
    val email: String = "",
    val password: String = "",
    val gender: Gender? = null,
    val acceptedTerms: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Función para convertir el usuario a un Map para Firebase
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "gender" to gender?.name,
            "acceptedTerms" to acceptedTerms,
            "createdAt" to createdAt
        )
    }

    companion object {
        // Función para crear un User desde un Map de Firebase
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                uid = map["uid"] as? String ?: "",
                email = map["email"] as? String ?: "",
                gender = (map["gender"] as? String)?.let { Gender.valueOf(it) },
                acceptedTerms = map["acceptedTerms"] as? Boolean ?: false,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}