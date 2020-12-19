   package com.example.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.registerlogin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import org.w3c.dom.Text

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if(user != null) {
            supportActionBar?.title = user!!.username

            //setupDummyData()
            listenForMessages()
            findViewById<Button>(R.id.sendbutton_chatlog).setOnClickListener {
                Log.d("Chatlog","Attempt to send message...")
                performSendMessage()
            }
        }
    }

    /*
    for(document in it.result!!) {
                        Log.d("NewMessage",document.data.toString())
                        val username = document.data.getValue("Username").toString()
                        val profile = document.data.getValue("Profile Image Uri").toString()
                        val uid = document.data.getValue("User Uid").toString()
                        val user = User(username, profile, uid)
                        if(user != null) {
                            Log.d("NewMessage" , user.username)
                            adapter.add(UserItem(user))
                        }

                    }
     */
    private fun listenForMessages() {
        val db = Firebase.firestore
        val ref = db.collection("messages")
        ref.get()
                .addOnCompleteListener() {
                    for(document in it.result!!) {
                        if(document.data.getValue("fromId") == FirebaseAuth.getInstance().uid) {
                            adapter.add(ChatFromITem(document.data.getValue("text").toString()))
                        }
                        else{
                            val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                            if(document.data.getValue("toId") == user!!.uid) {
                                adapter.add(ChatToItem(document.data.getValue("text").toString()))
                            }
                        }
                    }
                    findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter

                }


    }


    private fun performSendMessage(){
        // how do we actually send a message to firebase..

        val text = findViewById<TextView>(R.id.edittext_chatlog).text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid

        if(fromId == null) return
        val db = Firebase.firestore
        val chatMessage = ChatMessage(db.collection("messages").id,text,fromId,toId,System.currentTimeMillis()/1000)


        db.collection("messages").add(chatMessage).addOnSuccessListener {
            Log.d("ChatLog","Saved our chat message : ${it.id}") //*****
        }
    }
    private fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromITem("FROM MESSAGAESSSSSSSSSSSSSS"))
        adapter.add(ChatToItem("TO MESSAGESSSSSSSSSSSSS\nTOMESSAGEEE"))


        findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter
    }
}



   class ChatFromITem(val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textView_from_row).text = text
    }

}
class ChatToItem(val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textView_to_row).text = text
    }

}

