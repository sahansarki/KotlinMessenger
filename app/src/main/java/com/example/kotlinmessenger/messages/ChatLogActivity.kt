   package com.example.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.registerlogin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import org.w3c.dom.Text

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        if(toUser != null) {
            supportActionBar?.title = toUser!!.username

            //setupDummyData()
            listenForMessages()
            findViewById<Button>(R.id.sendbutton_chatlog).setOnClickListener {
                Log.d("Chatlog","Attempt to send message...")
                performSendMessage()
            }
        }
    }

    private fun listenForMessages() {
        val db = Firebase.firestore
        val ref = db.collection("messages")
        ref.get()
                .addOnCompleteListener() {
                    var hashmap_messages = hashMapOf<String, Long>()
                    for(document in it.result!!) {
                        //println(document.data.getValue("text").toString())
                        if(document.data.getValue("fromId") == FirebaseAuth.getInstance().uid && document.data.getValue("toId") == toUser!!.uid || document.data.getValue("toId") == FirebaseAuth.getInstance().uid && document.data.getValue("fromId") == toUser!!.uid) {
                            //Log.d("ListenForMessages" , "MessageApo : ${document.data.getValue("text")}")
                            if(document.data.getValue("timestamp") is String) {
                                document.data.getValue("timestamp").toString().toLong()
                            }
                            hashmap_messages.put(document.data.getValue("text").toString(),document.data.getValue("timestamp").toString().toLong())
                        }

                    }
                    val result = hashmap_messages.toList().sortedBy { (_, value) -> value }.toMap()
                    for (entry in result) {
                        println("Key: " + entry.key)
                        println(" Value: " + entry.value)
                    }
                    for(i in result) {
                        for(document in it.result!!) {
                            if(document.data.getValue("text") == i.key && document.data.getValue("fromId") == FirebaseAuth.getInstance().uid) {
                                Log.d("listenForMessagesFromTo" , "message : ${i.key} ")
                                val currentUser = LatestMessagesActivity.currentUser
                                adapter.add(ChatFromITem(i.key,currentUser!!))

                            }
                            else{
                                if(document.data.getValue("fromId") == toUser!!.uid) {
                                    Log.d("listenForMessagesFromTo" , "message : ${i.key} ")
                                    adapter.add(ChatToItem(i.key,toUser!!))
                                }

                            }
                        }
                    }

                    //findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter

                }


    }


    private fun performSendMessage(){
        // how do we actually send a message to firebase..

        val text = findViewById<TextView>(R.id.edittext_chatlog).text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser!!.uid

        if(fromId == null) return
        val db = Firebase.firestore
        val chatMessage = ChatMessage(db.collection("messages").id , text , fromId,toId,System.currentTimeMillis()/1000)
        Log.d("ChatLogPerform","messages id = ${db.collection("messages").id}")
        val chatMessage2 = chatMessage.text
        val currentUser = LatestMessagesActivity.currentUser
        adapter.add(ChatFromITem(chatMessage2,currentUser!!))

        val latestmessage = hashMapOf(
                "fromId" to "$fromId",
                "message" to "$chatMessage2",
                "toId" to "$toId"
        )


        val ref = db.collection("LatestMessages").document("$fromId-$toId")
        //val mesaj_ref = ref.collection("$toId").document("$fromId")
        ref.set(latestmessage, SetOptions.merge())
        findViewById<TextView>(R.id.edittext_chatlog).text = ""
        //findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter

        db.collection("messages").add(chatMessage).addOnSuccessListener {
            Log.d("ChatLogPerform","Saved our chat message : ${it.id}") //*****
        }

    }
}
   class ChatFromITem(val text: String , val user : User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textView_from_row).text = text
        val uri = user.profileImageUri
        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_from_row)
        Picasso.get().load(uri).into(targetImageView)

    }

}
class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textView_to_row).text = text
        // load our user image into the imageview
        val uri = user.profileImageUri
        val targetImagiView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_to_row)
        Picasso.get().load(uri).into(targetImagiView)
    }

}

