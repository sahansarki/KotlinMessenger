package com.example.kotlinmessenger.registerlogin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_login)
    val login_button = findViewById<Button>(R.id.login_login_button)
    val back_to_register_textview = findViewById<TextView>(R.id.back_register_textview)

    login_button.setOnClickListener {
        val email = findViewById<TextView>(R.id.login_email).text.toString()
        val password = findViewById<TextView>(R.id.login_password).text.toString()

        Log.d("Login" , "Attempt login with email/password: ${email}/***}")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
//            .addOnCompleteListener()
//            .add

    }

    back_to_register_textview.setOnClickListener {
        finish()
    }
    }
}