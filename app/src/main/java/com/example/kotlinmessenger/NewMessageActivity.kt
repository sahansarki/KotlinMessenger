package com.example.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlin.reflect.typeOf

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title= "Select User"
        /*
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        findViewById<RecyclerView>(R.id.recyclerview_newmessage).adapter = adapter
        */
        fetchUsers()
    }
    private fun fetchUsers(){
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("Users")
        ref.get()
            .addOnCompleteListener {
                val adapter = GroupAdapter<ViewHolder>()

                if(it.isSuccessful){
                    for(document in it.result!!) {
                        Log.d("NewMessage",document.data.toString())
                        val username = document.data.getValue("Username").toString()
                        val profile = document.data.getValue("Profile Image Uri").toString()
                        val uid = document.data.getValue("User Uid").toString()
                        val user = User(username,profile,uid)
                        if(user != null) {
                            Log.d("NewMessage" , user.username)
                            adapter.add(UserItem(user))
                        }

                    }
                    findViewById<RecyclerView>(R.id.recyclerview_newmessage).adapter = adapter
                }
            }

        }

    }



class UserItem(val user : User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // will be called in our list for each user object later on..

        viewHolder.itemView.findViewById<TextView>(R.id.username_textview_new_message).text = user.username
        Picasso.get().load(user.profileImageUri).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageview_new_message))
    }

}
// alttaki yerine groupie.
/*
class CustomAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

 */