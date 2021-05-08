package com.example.catchat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class TelegramAuthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_telegram_auth, container, false)
        val sharedPref = activity?.getSharedPreferences("mysettings", Context.MODE_PRIVATE)
        val edit = sharedPref?.edit()
        val textV = view?.findViewById<TextView>(R.id.textV)
        val name = sharedPref?.getString(TUID, "")
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Telegram Dialogs"
        textV?.text = "Your TUID is $name\n"

        val exitButton = view?.findViewById<Button>(R.id.exit_button)
        exitButton?.setOnClickListener {
            println("CLICKED")
            if (sharedPref?.contains(TUID)!!) {
                println("WRITING REMOVED")
                edit?.remove(TUID)
                edit?.apply()
                //TODO: swap
            } else {
                assert(false)
            }

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.content_frame, TelegramFragment())
            transaction?.commit()
        }
        return view
    }
}