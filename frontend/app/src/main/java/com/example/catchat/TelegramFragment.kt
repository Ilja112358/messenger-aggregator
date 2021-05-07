package com.example.catchat

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment


class TelegramFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_telegram, container, false)
        val submitButton = view?.findViewById<Button>(R.id.submit_button)
        val phoneField = view?.findViewById<EditText>(R.id.phoneInput)

        phoneField?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                phoneField?.hideKeyboard()
        }

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