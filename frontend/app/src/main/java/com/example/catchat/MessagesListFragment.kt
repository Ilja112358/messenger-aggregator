package com.example.catchat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.models.Dialog
import kotlinx.android.synthetic.main.fragment_gmail.*
import java.util.*

class MessagesListFragment : Fragment() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>? = null
    private var dialogsList: List<Dialog> = Collections.emptyList()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Telegram Dialogs"

        dialogsList = tgApi.getDialogs(TUID)
        return inflater.inflate(R.layout.fragment_messages_list, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = DialogsRecyclerAdapter(dialogsList, {
                val intent = Intent(getActivity()?.getBaseContext(), Chat::class.java)
                intent.putExtra("chatName", dialogsList[it].name)
                intent.putExtra("dialogId", dialogsList[it].dialog_id.toString())

                startActivity(intent)
            })

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager.VERTICAL
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        (0..30).forEach { i -> data.add("\$i element") }
        return data
    }

    fun updateDialogs() {

    }
}