package com.tcsappdev.ubiquitous.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.tcsappdev.ubiquitous.utils.AuthValidation
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
        if (!AuthValidation.isValidEmail(email) || !AuthValidation.isValidPassword(password)) {
            _authState.value = AuthState.Error("Invalid email or password")
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
        if (!AuthValidation.isValidName(name)){
            _authState.value = AuthState.Error("Please enter your name")
            return
        }
        if (!AuthValidation.isValidEmail(email)){
            _authState.value = AuthState.Error("Please enter a valid email")
            return
        }
        if (!AuthValidation.isValidPassword(password)){
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener {
                            _authState.value = AuthState.Authenticated
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?: "Registration failed")
                }
            }
    }

    fun logout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun changePassword(currentPassword: String, newPassword: String){
        val user = auth.currentUser
        val email = user?.email ?: return

        val credential = com.google.firebase.auth.EmailAuthProvider
            .getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { reAuthTask ->
                if(reAuthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if(updateTask.isSuccessful) {
                                _authState.value = AuthState.Success
                            } else {
                                _authState.value = AuthState.Error(
                                    updateTask.exception?.message ?: "Failed to update password"
                                )
                            }
                        }
                } else {
                    _authState.value = AuthState.Error("Current password is incorrect.")
                }
            }
    }
}

sealed class AuthState{
    object Idle : AuthState()
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    object Success: AuthState()
    data class Error(val message: String) : AuthState()
}