package com.example.catchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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
    val handler = Handler()
    var task : UpdateTask? = null

    @SuppressLint("StaticFieldLeak")
    inner class UpdateTask: AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            dialogsList = tgApi.getDialogs(TUID)
        }

        override fun onPostExecute(result: Unit?) {
            setDialogsField()
            super.onPostExecute(result)
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
        task = UpdateTask()
        task!!.execute()
    }

    override fun onDestroy() {
        task!!.cancel(false)
        super.onDestroy()
    }

    private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        (0..30).forEach { i -> data.add("\$i element") }
        return data
    }

    private fun setDialogsField() {
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = DialogsRecyclerAdapter(dialogsList) {
                val intent = Intent(activity?.baseContext, Chat::class.java)
                intent.putExtra("chatName", dialogsList[it].name)
                intent.putExtra("dialogId", dialogsList[it].dialog_id.toString())

                if (dialogsList[it].unread_count > 0) {
                    TgApi().sendMarkRead("test", dialogsList[it].dialog_id)
                }

                startActivity(intent)
            }

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager.VERTICAL
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
        task = UpdateTask()
        task!!.execute()
    }
}