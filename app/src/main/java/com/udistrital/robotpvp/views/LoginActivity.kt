package com.udistrital.robotpvp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.udistrital.robotpvp.views.screens.LoginScreen

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "log-UD"

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(onLoginClick = { email, password ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            Log.d(TAG, "signInWithEmail:success")

                            val intent = Intent(this, GameActivity::class.java)
                            startActivity(intent)

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }
            } )
        }
    }
}