package com.example.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.registerlogin.RegisterActivity
import com.example.kotlinmessenger.registerlogin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class LatestMessagesActivity : AppCompatActivity() {
    val adapter_latestMessages = GroupAdapter<ViewHolder>()
    companion object{
        var currentUser: User? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)


        findViewById<RecyclerView>(R.id.recylerview_latestmessages).adapter = adapter_latestMessages


        getLastMessages()

        fetchCurrentUser()
        verifyUserLoggedIn()
    }

    override fun onResume() {
        super.onResume()
        adapter_latestMessages.clear()
        getLastMessages()
    }
    class LatestMessageRow(val text : String , val user: User): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.textView2).text = text
            viewHolder.itemView.findViewById<TextView>(R.id.textView).text = user.username

            val uri = user.profileImageUri
            val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
            Picasso.get().load(uri).into(targetImageView)


        }

    }
    private fun getLastMessages() {

        val db = Firebase.firestore
        val ref = db.collection("LatestMessages")
        ref.get()
                .addOnCompleteListener() {
                    for(document in it.result!!) {
                        Log.d("getLast" , "İlk result : ${it.result.toString()}")
                        val ref_user = db.collection("Users")
                        ref_user.get()
                                .addOnCompleteListener() {
                                    for(document2 in it.result!!) {
                                        if(document.data.getValue("toId") == document2.data.getValue("User Uid")) {
                                            val username = document2.data.getValue("Username").toString()
                                            val uri = document2.data.getValue("Profile Image Uri").toString()
                                            val uid = document2.data.getValue("User Uid").toString()
                                            val text = document.data.getValue("message").toString()
                                            currentUser = User(username,uri,uid)

                                            adapter_latestMessages.add(LatestMessageRow(text,currentUser!!))
                                        }
                                    }
                                }
                    }
                }


    }

    private fun fetchCurrentUser(){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid
        Log.d("LatestMessages" , "Uid : ${uid}")

        val ref = db.collection("Users")
        ref.get()
                .addOnCompleteListener{
                    for(document in it.result!!) {
                        Log.d("LatestMessages" , "Document id = ${document.id}")
                        if(document.data.getValue("User Uid")== uid) {
                            val username = document.data.getValue("Username").toString()
                            val profile = document.data.getValue("Profile Image Uri").toString()
                            val uid = document.data.getValue("User Uid").toString()
                            currentUser = User(username, profile, uid)
                            Log.d("LatestMessages" , "Current user : ${currentUser?.profileImageUri}")
                        }

                    }
                }

    }


    private fun verifyUserLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
