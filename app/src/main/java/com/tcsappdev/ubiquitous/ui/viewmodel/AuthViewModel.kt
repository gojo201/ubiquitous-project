package com.tcsappdev.ubiquitous.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState : StateFlow<AuthState> = _authState

    init{
        checkAuthState()
    }

    private fun checkAuthState(){
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String){
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    android.util.Log.d("AuthViewModel", "Login successful, setting Authenticated")
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?: "Login failed")
                }
            }
    }

    fun register(name: String, email: String, password: String){
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?: "Registration failed")
                }
            }
    }

    fun logout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState{
    object Idle : AuthState()
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    data class Error(val message: String) : AuthState()
}