package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.Group

class GroupAdaptor(private val groupList: ArrayList<Group>): RecyclerView.Adapter<GroupAdaptor.GroupViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupAdaptor.GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupAdaptor.GroupViewHolder, position: Int) {
       val group: Group = groupList[position]
        holder.name.text = group.groupName
    }

    override fun getItemCount(): Int {
       return groupList.size
    }

    public class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.titleTv)

    }

}