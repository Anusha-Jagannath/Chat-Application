package com.example.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.home.ChatActivity
import com.example.chatapp.home.GroupActivity
import com.example.chatapp.model.Group

class GroupAdaptor(val context: Context, private val groupList: ArrayList<Group>) :
    RecyclerView.Adapter<GroupAdaptor.GroupViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupAdaptor.GroupViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.group_user, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupAdaptor.GroupViewHolder, position: Int) {
        val group: Group = groupList[position]
        holder.name.text = group.groupName
        holder.itemView.setOnClickListener {
            val intent = Intent(context, GroupActivity::class.java)
            intent.putExtra("groupName", group.groupName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    public class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.titleTv)
    }
}