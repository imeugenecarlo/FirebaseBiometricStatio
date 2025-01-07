package com.example.biofirebasestatio

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    var user: FirebaseUser? by mutableStateOf(auth.currentUser)
    var message by mutableStateOf("")
    var isBiometricEnabled by mutableStateOf(false)

    fun signIn(email: String, password: String,onBiometricPrompt: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    message = ""
                    onBiometricPrompt()
                } else {
                    user = null
                    message = task.exception?.message ?: "Unknown error"
                }
            }
    }

    fun signOut() {
        user = null
        auth.signOut()
    }

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    message = ""
                } else {
                    user = null
                    message = task.exception?.message ?: "Unknown error"
                }
            }
    }

    fun enableBiometric() {
        isBiometricEnabled = true
    }

    private fun saveEmailToPreferences(context: Context, email: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }
}