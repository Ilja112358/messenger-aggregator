package com.aggregator.ui.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aggregator.api.API
import com.aggregator.ui.adapters.DialogsRecyclerAdapter
import com.aggregator.models.Dialog
import com.aggregator.ui.activities.ChatActivity
import com.aggregator.ui.activities.R
import kotlinx.android.synthetic.main.fragment_gmail.*
import java.util.*

class MessagesListFragment(val apiType: String) : Fragment() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>? = null
    private var dialogsList: List<Dialog> = Collections.emptyList()
    val handler = Handler()

    inner class UpdateTask: AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Telegram Dialogs"

        return inflater.inflate(R.layout.fragment_messages_list, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        handler.post(
            object : Runnable {
                override fun run() {
                    setDialogsField()
                    handler.postDelayed(this, 3000)
                }
            }
        )
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        (0..30).forEach { i -> data.add("\$i element") }
        return data
    }

    private fun setDialogsField() {
        dialogsList = API.api[apiType]!!.getDialogs(TUID)
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = DialogsRecyclerAdapter(dialogsList) {
                val intent = Intent(activity?.baseContext, ChatActivity::class.java)
                intent.putExtra("chatName", dialogsList[it].name)
                intent.putExtra("dialogId", dialogsList[it].dialog_id.toString())
                intent.putExtra("api", apiType)
                if (dialogsList[it].unread_count > 0) {
                    API.api[apiType]!!.sendMarkRead("test", dialogsList[it].dialog_id)
                }

                startActivity(intent)
            }

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager.VERTICAL
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }
}