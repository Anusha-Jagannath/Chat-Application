package com.example.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.model.Chat
import com.example.chatapp.service.AuthenticationService
import kotlinx.android.synthetic.main.sent.view.*

class ChatAdaptor(val context: Context, val messageList: ArrayList<Chat>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1) {
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        } else {
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            if(currentMessage.message.equals("photo")){
               holder.photo.visibility = View.VISIBLE
                holder.sentMessage.visibility = View.GONE
                Glide.with(context).load(currentMessage.imageUrl).placeholder(R.drawable.ic_baseline_person_24_white)
                    .into(holder.photo)
            }
                holder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveViewHolder
            if(currentMessage.message.equals("photo")) {
                holder.userPhoto.visibility = View.VISIBLE
                holder.receiveMessage.visibility = View.GONE
                Glide.with(context).load(currentMessage.imageUrl).placeholder(R.drawable.ic_baseline_person_24).into(holder.userPhoto)
            }
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (AuthenticationService().getUid().equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val photo = itemView.findViewById<ImageView>(R.id.userPhoto)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val userPhoto = itemView.findViewById<ImageView>(R.id.userReceivePhoto)

    }

}