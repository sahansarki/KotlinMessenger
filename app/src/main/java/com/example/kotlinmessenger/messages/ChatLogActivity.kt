package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.registerlogin.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if(user != null) {
            supportActionBar?.title = user!!.username
        }



        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromITem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromITem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromITem())

        findViewById<RecyclerView>(R.id.recylerview_chatlog).adapter = adapter
    }
}

class ChatFromITem : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

}
class ChatToItem : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

}

