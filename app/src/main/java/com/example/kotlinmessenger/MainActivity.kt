
package com.example.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val alread_have_account = findViewById<TextView>(R.id.alread_have_account_textView)
        val register_button = findViewById<Button>(R.id.register_button_register)
        register_button.setOnClickListener {
            val email = findViewById<TextView>(R.id.email_edittext_register)
            val password = findViewById<TextView>(R.id.password_edittext_register)

            Log.d("MainActivity" , " Email is " + email.text)
            Log.d("MainActivity" , " Password is : ${password.text}")

        }
        alread_have_account.setOnClickListener {
            Log.d("MainActivity" , "Try to show login activity")
            //launch the login activity somehow
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }

}
