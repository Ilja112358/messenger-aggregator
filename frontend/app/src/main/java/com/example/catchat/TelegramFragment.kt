package com.example.catchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import com.example.myapplication.ui.home.Api

class TelegramFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_telegram, container, false)
        val submitButton = view?.findViewById<Button>(R.id.submit_button)
        val textView = view?.findViewById<TextView>(R.id.text_field)
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)

        submitButton?.setOnClickListener {
            textView?.text = Api().getHash("Are you Slava Marlow?\n")
        }

        return view
    }
}