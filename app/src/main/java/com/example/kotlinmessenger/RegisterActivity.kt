
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
                    Log.d("RegisterActivity","Buil Version kısmı.")
                    val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    findViewById<ImageView>(R.id.selectphoto_imageview_register).setImageBitmap(bitmap)
                    findViewById<Button>(R.id.select_photo_button).alpha = 0f
                    /*
                    val bitmapDrawable = BitmapDrawable(bitmap)
                    findViewById<Button>(R.id.select_photo_button).background = bitmapDrawable
                    uploadImageToFirebaseStorage()
                    */
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

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
            Log.d("RegisterActivity" , " Foto Uri alınamadı ,  Başarısız ")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Succesfully uploaded image : ${it.metadata?.path}")


                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity", "File Location:$it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val db = Firebase.firestore

        val userMap = hashMapOf<String,Any>()
        userMap.put("User Uid",uid)
        userMap.put("Username",findViewById<TextView>(R.id.username_edittext_register).text.toString())
        userMap.put("Profile Image Uri",profileImageUri)

        db.collection("Users").add(userMap).addOnSuccessListener {
            Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")

            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // önceki aktiviteleri bitirmek için
            startActivity(intent)
        }.addOnFailureListener{
            Toast.makeText(this,it.localizedMessage.toString(),Toast.LENGTH_LONG).show()
        }


    }
}
class User(val uid: String , val username: String , val profileImageUri: String)
