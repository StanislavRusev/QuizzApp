package com.example.quizzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzapp.R
import com.example.quizzapp.services.User

class UserAdapter(private val userList: List<User>): RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val position: TextView = itemView.findViewById(R.id.recycler_position)
        val username: TextView = itemView.findViewById(R.id.recycler_username)
        val points: TextView = itemView.findViewById(R.id.recycler_points)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_users, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.position.setText((position+1).toString())
        holder.username.setText(currentUser.name)
        holder.points.setText(currentUser.points.toString())
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}