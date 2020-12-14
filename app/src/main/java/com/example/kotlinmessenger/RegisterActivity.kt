
package com.example.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alread_have_account = findViewById<TextView>(R.id.alread_have_account_textView)
        val register_button = findViewById<Button>(R.id.register_button_register)
        val select_photo_button = findViewById<Button>(R.id.select_photo_button)

        register_button.setOnClickListener {
            performRegister()
        }
        alread_have_account.setOnClickListener {
            Log.d("RegisterActivity" , "Try to show login activity")
            //launch the login activity somehow
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo_button.setOnClickListener {
            Log.d("RegisterActivity" , "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            if (selectedPhotoUri != null) {

                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    val bitmapDrawable = BitmapDrawable(bitmap)
                    findViewById<Button>(R.id.select_photo_button).background = bitmapDrawable
                    uploadImageToFirebaseStorage()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("RegisterActivity" , "İf'e girmemiş...")
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected image was..

            val selectedPhotoUri = data.data
            Log.d("RegisterAcitivity" , "SelectedPhotoUri = $selectedPhotoUri")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            findViewById<Button>(R.id.select_photo_button).setBackgroundDrawable(bitmapDrawable)
            //select_photo_button!!.setBackgroundDrawable(bitmapDrawable)
        }
        else{
            Log.d("RegisterActivity" , "İf'e girmemiş...")
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
*/
    private fun performRegister(){
        val email = findViewById<TextView>(R.id.email_edittext_register).text.toString()
        val password = findViewById<TextView>(R.id.password_edittext_register).text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter text in email/password",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity" , " Email is $email" )
        Log.d("RegisterActivity" , " Password is : ${password}")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener

                    // else if succesfully
                    Log.d("RegisterActivity", "Succesfully created user with uid : ${it.result!!.user!!.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity" , "Fail to create user: ${it.message}")
                    Toast.makeText(this,"Fail to create user: ${it.message}",Toast.LENGTH_SHORT).show()
                }


    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) {
            Log.d("RegisterActivity" , " Başarısız")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity" , "Succesfully uploaded image : ${it.metadata?.path}")
                }
       /*
        ref.downloadUrl.addOnSuccessListener {
            Log.d("RegisterActivity" , "File Location:$it")
        }

        */
    }

}
