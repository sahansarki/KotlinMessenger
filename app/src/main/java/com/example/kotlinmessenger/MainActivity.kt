package com.example.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = findViewById<TextView>(R.id.email_edittext_register)
        val password = findViewById<TextView>(R.id.password_edittext_register)

        Log.d("MainActivity" , " Email is " + email)
        Log.d("MainActivity" , " Password is " + password)

    }

}