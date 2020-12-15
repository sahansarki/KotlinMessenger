package com.example.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title= "Select User"

        findViewById<RecyclerView>(R.id.recyclerview_newmessage).adapter
    }
}

/*
class CustomAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

 */