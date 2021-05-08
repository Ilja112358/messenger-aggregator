package com.example.catchat


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


/**
 * A class that represents chat dialog between two users in the application
 */
class Chat : AppCompatActivity() {
    private var mLinearLayout: LinearLayout? = null
    private var mSendButton: ImageView? = null
    private var mMessageArea: EditText? = null
    private var mScrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent: Intent = intent
        val titleView = findViewById<TextView>(R.id.navBarDialogName)
        titleView.text = intent.getStringExtra("chatName") ?: "Telegram chat"
        val backView = findViewById<ImageView>(R.id.exitDialog)
        backView.setOnClickListener {
            finish()
        }

        initializeViews()

        mSendButton?.setOnClickListener {
            val text = mMessageArea?.text ?: ""
            if (text.length > 0) {
                tgApi.sendTextMessage(TUID, 423865152L, text.toString())

                mMessageArea!!.setText(EMPTY_MESSAGE)
            }
        }

        /*val chatPair: Array<String> = java.lang.ModuleLayer.Controller.getChatPair(this@Chat).split(
            PAIR_DELIMITER
        )
        if (chatPair.size < 2) {
            finish()
        }
        mUserMessagesReference = Firebase(MESSAGES_LOCATION + chatPair[0] + DELIMITER + chatPair[1])
        mFriendMessagesReference =
            Firebase(MESSAGES_LOCATION + chatPair[1] + DELIMITER + chatPair[0])
        initSendButton(chatPair[0])
        mUserMessagesReference.addChildEventListener(object : AbstractClientChildEventListener() {
            fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val map: Map<*, *> = dataSnapshot.getValue(MutableMap::class.java)
                val message = map[MESSAGE_KEY].toString()
                val userName = map[USER_KEY].toString()
                if (userName == java.lang.ModuleLayer.Controller.getLoggedInAsString(this@Chat)) {
                    addMessageBox(message, USER_MESSAGE)
                } else {
                    addMessageBox(message, FRIEND_MESSAGE)
                }
            }
        })*/
        tgApi.getMessages(TUID, 423865152L).reversed().forEach {
            addMessageBox(it.text, FRIEND_MESSAGE)
        }
    }

    private fun initSendButton(userId: String) {
        mSendButton!!.setOnClickListener { v: View? ->
            val messageText = mMessageArea!!.text.toString()
            if (checkMessage(messageText)) {
                val map: MutableMap<String, String> =
                    HashMap()
                map[MESSAGE_KEY] = messageText
                map[USER_KEY] = userId
                //mUserMessagesReference.push().setValue(map)
                //mFriendMessagesReference.push().setValue(map)
                mMessageArea!!.setText(EMPTY_MESSAGE)
            }
        }
    }

    private fun addMessageBox(message: String, type: Int) {
        val textView = TextView(this@Chat)
        textView.text = message
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.weight = 1f
        if (type == USER_MESSAGE) {
            layoutParams.gravity = Gravity.RIGHT
            textView.setBackgroundResource(R.drawable.bubble_in)
        } else {
            layoutParams.gravity = Gravity.LEFT
            textView.setBackgroundResource(R.drawable.bubble_out)
        }
        textView.layoutParams = layoutParams
        mLinearLayout!!.addView(textView)
        mScrollView!!.fullScroll(View.FOCUS_DOWN)
    }

    private fun initializeViews() {
        mLinearLayout = findViewById(R.id.layout1)
        mSendButton = findViewById(R.id.sendButton)
        mMessageArea = findViewById(R.id.messageArea)
        mScrollView = findViewById(R.id.scrollView)
    }

    private fun checkMessage(message: String): Boolean {
        if (message.length == 0) {
            return false
        }
        for (i in 0 until message.length) {
            if (message[i] != ' ' && message[i] != '\n') {
                return true
            }
        }
        return false
    }

    companion object {
        private const val MESSAGES_LOCATION = "https://placemedemo-676f5.firebaseio.com/messages/"
        private const val DELIMITER = "_"
        private const val PAIR_DELIMITER = ","
        private const val MESSAGE_KEY = "message"
        private const val USER_KEY = "user"
        private const val EMPTY_MESSAGE = ""
        private const val USER_MESSAGE = 1
        private const val FRIEND_MESSAGE = 2
    }
}