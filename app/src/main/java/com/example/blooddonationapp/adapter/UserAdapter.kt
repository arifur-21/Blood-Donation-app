package com.example.blooddonationapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonationapp.R
import com.example.blooddonationapp.model.User
import com.squareup.picasso.Picasso


class UserAdapter(
        private var context: Context,
        private var userList: MutableList<User>):
        RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_display_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       var user: User = userList[position]
        holder.type.text = user.type

        if (user.call.equals("donor")){
            holder.call.visibility = View.VISIBLE
        }
        holder.userName.text = user.name
        holder.bloodGroup.text = user.bloodgroup
        holder.phoneNumber.text = user.phone

        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.userProfile)

            holder.call.setOnClickListener{
                val phoneUri = Uri.parse("tel:" + userList.get(position).call)
                val callIntent = Intent(Intent.ACTION_DIAL, phoneUri)
                if (callIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(callIntent)
                }
            }

    }

    override fun getItemCount(): Int {
      return userList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val userName = itemView.findViewById<TextView>(R.id.username)
        val type = itemView.findViewById<TextView>(R.id.usertype)
        val phoneNumber = itemView.findViewById<TextView>(R.id.userphoneNumber)
        val bloodGroup = itemView.findViewById<TextView>(R.id.userbloodGroup)
        val userProfile = itemView.findViewById<ImageView>(R.id.userProfileImage)
        val call = itemView.findViewById<ImageButton>(R.id.callbtnId)
    }
    fun setData(userList: ArrayList<User>){
        this.userList = userList
        notifyDataSetChanged()
    }

}