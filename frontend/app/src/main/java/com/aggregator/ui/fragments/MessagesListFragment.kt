package com.aggregator.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aggregator.api.API
import com.aggregator.ui.adapters.DialogsRecyclerAdapter
import com.aggregator.models.Dialog
import com.aggregator.ui.activities.ChatActivity
import com.aggregator.ui.activities.R
import kotlinx.android.synthetic.main.fragment_gmail.*
import kotlinx.coroutines.*
import java.util.*

class MessagesListFragment(val apiType: String) : Fragment() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>? = null
    private var dialogsList: List<Dialog> = Collections.emptyList()
    private val getter = DialogsGetter()

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
        getter.setDialogs()
    }

    private fun setDialogsField() {
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = DialogsRecyclerAdapter(dialogsList) {
                val intent = Intent(activity?.baseContext, ChatActivity::class.java)
                intent.putExtra("chatName", dialogsList[it].name)
                intent.putExtra("dialogId", dialogsList[it].dialog_id)
                intent.putExtra("api", apiType)
                intent.putExtra("avatarUrl", dialogsList[it].avaUrl)
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

    @SuppressLint("StaticFieldLeak")
    inner class DialogsGetter : ViewModel() {
        fun setDialogs() {
            viewModelScope.launch {
                dialogsList = withContext(Dispatchers.IO) {
                    API.api[apiType]!!.getDialogs(TUID)
                }
                if (activity != null)
                    setDialogsField()
            }
        }
    }
}