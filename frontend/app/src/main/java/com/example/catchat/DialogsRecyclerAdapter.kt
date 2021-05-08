package com.example.catchat

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.models.Dialog
import java.net.URI
import java.net.URL

class DialogsRecyclerAdapter(private val dialogs: List<Dialog>) :
    RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dialogAvatarView: ImageView? = null
        var dialogTitleView: TextView? = null
        var dialogLastMessageView: TextView? = null
        var dialogLastTimeView: TextView? = null

        init {
            dialogAvatarView = itemView.findViewById(R.id.dialogAvatar)
            dialogTitleView = itemView.findViewById(R.id.dialogName)
            dialogLastMessageView = itemView.findViewById(R.id.dialogLastMessage)
            dialogLastTimeView = itemView.findViewById(R.id.lastMessageTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val image = BitmapFactory.decodeStream(URL("https://images.app.goo.gl/WPM37NyC6fDXR7rf7").openConnection().getInputStream())
        //val uri = Uri.parse("https://images.app.goo.gl/WPM37NyC6fDXR7rf7")
        holder.dialogAvatarView?.setImageResource(R.drawable.telegram)
        holder.dialogTitleView?.text = dialogs[position].name
        holder.dialogLastMessageView?.text = dialogs[position].lastMessage
        holder.dialogLastTimeView?.text = dialogs[position].date
    }

    override fun getItemCount(): Int {
        return dialogs.size
    }
}