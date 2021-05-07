package com.example.catchat

import TgApiGrpc
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.example.myapplication.ui.home.Api
import com.example.myapplication.ui.home.TgApi

class TelegramFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_telegram, container, false)
        val submitButton = view?.findViewById<Button>(R.id.submit_button)

        val phoneField = view?.findViewById<EditText>(R.id.phoneInput)

        submitButton?.setOnClickListener {
            val phoneText = phoneField?.text ?: ""
            println(phoneText)
            val codeHash = "stubHash"//TgApi().sendPhone("uid", phoneText.toString())
            println(codeHash)


            val alertBuilder = AlertDialog.Builder(context!!)
            val inflaterInner = LayoutInflater.from(context)
            val viewCode = inflaterInner.inflate(R.layout.code_input, null) as LinearLayout
            alertBuilder.setView(viewCode)
            val codeInputView = viewCode.findViewById<EditText>(R.id.input_code)
            alertBuilder.setTitle("Enter code")
            alertBuilder.setCancelable(true)
            alertBuilder.setPositiveButton("Send") { dialogInterface: DialogInterface, i: Int ->
                //TgApi().sendCode("uid", phoneText.toString(), codeInputView.text.toString(), codeHash)
                println("codeSent")
            }
            alertBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
            alertBuilder.create().show()

        }
        return view
    }
}