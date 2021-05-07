package com.example.catchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.myapplication.ui.home.Api

class InboxFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inbox, container, false)
        val submitButton = view?.findViewById<Button>(R.id.submit_button)

        submitButton?.setOnClickListener {
            println(Api().getHash("Are you Slava Marlow?\n"))
        }

        return view
    }

    fun onClickSubmit(view: View) {

    }
}