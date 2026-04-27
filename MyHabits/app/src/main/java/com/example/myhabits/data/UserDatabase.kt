package com.example.myhabits.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Simulación de base de datos persistente en memoria para el prototipo.
 */
object UserDatabase {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    fun addUser(user: User) {
        _users.update { it + user }
    }

    fun findUser(email: String, password: String): User? {
        return _users.value.find { it.email == email && it.password == password }
    }

    fun exists(email: String): Boolean {
        return _users.value.any { it.email == email }
    }

    fun updateUser(oldEmail: String, updatedUser: User) {
        _users.update { currentUsers ->
            currentUsers.map { user ->
                if (user.email == oldEmail) updatedUser else user
            }
        }
    }
}
