package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.UserDetails
import com.google.firebase.firestore.auth.User

class UserSelectAdaptor(private val userList: ArrayList<UserDetails>): RecyclerView.Adapter<UserSelectAdaptor.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSelectAdaptor.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.select_user,parent,false)
        return UserSelectAdaptor.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserSelectAdaptor.MyViewHolder, position: Int) {
        val user: UserDetails = userList[position]
        holder.name.text = user.userName
        holder.status.text = user.status
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    public class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.titleTv)
        val status: TextView = itemView.findViewById(R.id.subTitleTv)
        val check: CheckBox = itemView.findViewById(R.id.timeTv)
    }
}