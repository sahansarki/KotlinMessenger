
package com.example.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val alread_have_account = findViewById<TextView>(R.id.alread_have_account_textView)
        val register_button = findViewById<Button>(R.id.register_button_register)
        register_button.setOnClickListener {
            performRegister()
        }
        alread_have_account.setOnClickListener {
            Log.d("MainActivity" , "Try to show login activity")
            //launch the login activity somehow
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
    private fun performRegister(){
        val email = findViewById<TextView>(R.id.email_edittext_register).text.toString()
        val password = findViewById<TextView>(R.id.password_edittext_register).text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter text in email/password",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity" , " Email is $email" )
        Log.d("MainActivity" , " Password is : ${password}")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener

                    // else if succesfully
                    Log.d("Main", "Succesfully created user with uid : ${it.result!!.user!!.uid}")
                }
                .addOnFailureListener {
                    Log.d("Main" , "Fail to create user: ${it.message}")
                    Toast.makeText(this,"Fail to create user: ${it.message}",Toast.LENGTH_SHORT).show()
                }


    }

}
