package com.aggregator.ui.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aggregator.models.Dialog
import com.aggregator.ui.activities.R
import com.squareup.picasso.Picasso
import java.util.*


class DialogsRecyclerAdapter(private val dialogs: List<Dialog>, var onItemClick: ((Int) -> Unit)?) :
    RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dialogAvatarView: ImageView? = null
        var dialogTitleView: TextView? = null
        var dialogLastMessageView: TextView? = null
        var dialogLastTimeView: TextView? = null
        var dialogBack: ConstraintLayout? = null
        var dialogPrefix: TextView? = null

        init {
            dialogAvatarView = itemView.findViewById(R.id.dialogAvatar)
            dialogTitleView = itemView.findViewById(R.id.dialogName)
            dialogLastMessageView = itemView.findViewById(R.id.dialogLastMessage)
            dialogLastTimeView = itemView.findViewById(R.id.lastMessageTime)
            dialogBack = itemView.findViewById(R.id.dialogBack)
            dialogPrefix = itemView.findViewById(R.id.dialogPrefix)

            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
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
        if (dialogs[position].avaUrl.length > 0) {
            Picasso.with(holder.dialogAvatarView?.context).load(dialogs[position].avaUrl)
                .placeholder(R.drawable.telegram)
                .into(holder.dialogAvatarView)
        } else {
            val prefix = dialogs[position].name.take(2).toUpperCase(Locale.ROOT)
            val rnd = Random()
            val color = rnd.nextInt(5)
            if (color == 0) {
                holder.dialogAvatarView?.setImageResource(R.drawable.red)
            } else if (color == 1) {
                holder.dialogAvatarView?.setImageResource(R.drawable.green)
            } else if (color == 2) {
                holder.dialogAvatarView?.setImageResource(R.drawable.blue)
            } else if (color == 3) {
                holder.dialogAvatarView?.setImageResource(R.drawable.purple)
            } else {
                holder.dialogAvatarView?.setImageResource(R.drawable.yellow)
            }

            holder.dialogPrefix?.text = prefix
            holder.dialogPrefix?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
            holder.dialogPrefix?.setTypeface(null, Typeface.BOLD);
            holder.dialogPrefix?.setTextColor(Color.WHITE)
        }

        //holder.dialogAvatarView?.setImageResource(R.drawable.telegram)
        holder.dialogTitleView?.text = dialogs[position].name
        holder.dialogLastMessageView?.text = dialogs[position].lastMessage
        holder.dialogLastTimeView?.text = dialogs[position].date.toString()
        if (dialogs[position].unread_count > 0)
            holder.dialogBack?.setBackgroundColor(Color.parseColor("#2f2f2f"))
        else
            holder.dialogBack?.setBackgroundColor(Color.BLACK)
    }

    override fun getItemCount(): Int {
        return dialogs.size
    }
}