package com.example.catchat

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_gmail.*


class GmailFragment : Fragment() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DialogsRecyclerAdapter.MyViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = activity?.getSharedPreferences("mysettings", Context.MODE_PRIVATE)
        val edit = sharedPref?.edit()
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Telegram"

        if (sharedPref?.contains(TUID)!!) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.content_frame, MessagesListFragment("gmail"))
            transaction?.commit()

            return inflater.inflate(R.layout.fragment_messages_list, container, false)
        } else {
            val view = inflater.inflate(R.layout.fragment_telegram, container, false)
            val submitButton = view?.findViewById<Button>(R.id.submit_button)
            val phoneField = view?.findViewById<EditText>(R.id.phoneInput)

            submitButton?.setOnClickListener {
                val phoneText = phoneField?.text ?: ""
                println(phoneText)
                val codeHash = "hehehe"//TgApi().sendPhone(TUID, phoneText.toString())
                println(codeHash)

                if (sharedPref?.contains(TUID)!!) {
                    assert(false)
                }

                val alertBuilder = AlertDialog.Builder(context!!)
                val inflaterInner = LayoutInflater.from(context)
                val viewCode = inflaterInner.inflate(R.layout.code_input, null) as LinearLayout
                alertBuilder.setView(viewCode)
                val codeInputView = viewCode.findViewById<EditText>(R.id.input_code)
                alertBuilder.setTitle("Enter code")
                alertBuilder.setCancelable(true)
                alertBuilder.setPositiveButton("Send") { dialogInterface: DialogInterface, i: Int ->
                    //TgApi().sendCode(TUID, phoneText.toString(), codeInputView.text.toString(), codeHash)
                    println("codeSent")

                    val unixTime = System.currentTimeMillis() / 1000L
                    edit?.putString(TUID, unixTime.toString())
                    edit?.apply()

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.content_frame, MessagesListFragment("gmail"))
                    transaction?.commit()
                    //TODO : swap
                }

                alertBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
                alertBuilder.create().show()
            }
            return view
        }
    }
}