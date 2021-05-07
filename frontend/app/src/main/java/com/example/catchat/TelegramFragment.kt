package com.example.catchat

import android.R.attr.x
import android.R.attr.y
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.ui.home.TgApi

public val TUID = "TUID"

class TelegramFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = activity?.getSharedPreferences("mysettings", Context.MODE_PRIVATE)
        val edit = sharedPref?.edit()
        var view1 = inflater.inflate(R.layout.fragment_telegram_auth, container, false)
        var view = inflater.inflate(R.layout.fragment_telegram, container, false)
        //val view1
        val submitButton = view?.findViewById<Button>(R.id.submit_button)
        val phoneField = view?.findViewById<EditText>(R.id.phoneInput)

        if (sharedPref?.contains(TUID)!!) {
            val textV = view1?.findViewById<TextView>(R.id.textV)
            val name = sharedPref.getString(TUID, "")
            textV?.text = "Your TUID is $name\n"

            val exitButton = view1?.findViewById<Button>(R.id.exit_button)
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
            return view1 //TODO: view1
        } else {

            submitButton?.setOnClickListener {
                val phoneText = phoneField?.text ?: ""
                println(phoneText)
                val codeHash = "hahahehe" //TgApi().sendPhone("uid4", phoneText.toString())
                println(codeHash)

                val unixTime = System.currentTimeMillis() / 1000L
                edit?.putString(TUID, unixTime.toString())
                edit?.apply()

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
                    //TgApi().sendCode("uid4", phoneText.toString(), codeInputView.text.toString(), codeHash)
                    println("codeSent")
                }

                alertBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
                alertBuilder.create().show()

                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.content_frame, TelegramAuthFragment())
                transaction?.commit()
                //TODO : swap
            }
            return view
        }
    }
}