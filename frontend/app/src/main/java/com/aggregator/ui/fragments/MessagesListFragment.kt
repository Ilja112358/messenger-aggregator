package com.aggregator.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aggregator.api.*
import com.aggregator.ui.adapters.DialogsRecyclerAdapter
import com.aggregator.models.Dialog
import com.aggregator.store.Callback
import com.aggregator.store.Response
import com.aggregator.store.Storage
import com.aggregator.ui.activities.ChatActivity
import com.aggregator.ui.activities.R
import kotlinx.android.synthetic.main.fragment_gmail.*
import java.util.*

class MessagesListFragment(private val apiType: String) : Fragment(), Callback {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>? = null
    private var dialogsList: List<Dialog>? = Collections.emptyList()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = when (apiType) {
            "gmail" -> "Gmail"
            else -> "Telegram"
        }
        Storage.subscribe(this)
        return inflater.inflate(R.layout.fragment_messages_list, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        Storage.onLoad(Response(1, apiType))
    }

    private fun setDialogsField() {
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = dialogsList?.let {
                DialogsRecyclerAdapter(it) {
                    val intent = Intent(activity?.baseContext, ChatActivity::class.java)
                    intent.putExtra("chatName", dialogsList!![it].name)
                    intent.putExtra("dialogId", dialogsList!![it].dialog_id)
                    intent.putExtra("api", apiType)
                    intent.putExtra("avatarUrl", dialogsList!![it].avaUrl)
                    if (dialogsList!![it].unread_count > 0) {
                        API.api[apiType]!!.sendMarkRead("test", dialogsList!![it].dialog_id)
                    }

                    startActivity(intent)
                }
            }

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager.VERTICAL
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onLoad(arg: Response<*>) {
        dialogsList = arg.response as List<Dialog>
        setDialogsField()
    }

}