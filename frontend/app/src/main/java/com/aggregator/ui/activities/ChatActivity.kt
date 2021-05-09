package com.aggregator.ui.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aggregator.api.API
import com.aggregator.models.Message
import com.aggregator.ui.fragments.TUID
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
//

/**
 * A class that represents chat dialog between two users in the application
 */
class ChatActivity : AppCompatActivity() {
    private var mLinearLayout: LinearLayout? = null
    private var mSendButton: ImageView? = null
    private var mMessageArea: EditText? = null
    private var mScrollView: ScrollView? = null
    private var dialogId: String? = null
    private var navDialogAvatarView: CircleImageView? = null
    private var navDialogAvatarStub: TextView? = null
    private var apiType: String? = null
    private val messagesGetter = MessagesGetter()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent: Intent = intent
        apiType = intent.getStringExtra("api")!!
        val titleView = findViewById<TextView>(R.id.navBarDialogName)
        val chatName = intent.getStringExtra("chatName") ?: "Telegram chat"
        titleView.text = chatName
        val backView = findViewById<ImageView>(R.id.exitDialog)
        backView.setOnClickListener {
            finish()
        }
        initializeViews(chatName)

        mSendButton?.setOnClickListener {
            val text = mMessageArea?.text ?: ""
            if (text.length > 0) {
                dialogId?.let { it1 -> API.api[apiType!!]!!.sendTextMessage(TUID, it1, text.toString()) }
                addMessageBox("Name", "20:20", text.toString(), USER_MESSAGE)

                val scrollView = findViewById<ScrollView>(R.id.scrollView)
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }

                mMessageArea!!.setText(EMPTY_MESSAGE)
            }
        }

        messagesGetter.getMessages()
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

    private fun addImageMessageBox(userName: String?, timestamp: String?, imageUrl: String, type: Int) {
        if ((userName != null) && (userName != "me") && (userName != "not me")){
            val inflater = LayoutInflater.from(this)
            val textMessageView = inflater.inflate(R.layout.image_message_box, mLinearLayout, false)
            val messageSubmitterView = textMessageView.findViewById<TextView>(R.id.userName)
            val messageImageContentView = textMessageView.findViewById<ImageView>(R.id.messageImageContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageSubmitterView.text = userName
            messageSubmitterView.setTypeface(null, Typeface.BOLD);
            messageSubmitterView.setTextColor(Color.WHITE)
            messageTimestampView.text = timestamp

            Picasso.with(this).load(imageUrl)
                .placeholder(R.drawable.telegram)
                .into(messageImageContentView)

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            if (type == USER_MESSAGE) {
                layoutParams.gravity = Gravity.RIGHT
                textMessageView.setBackgroundResource(R.drawable.bubble_in_new)
            } else {
                layoutParams.gravity = Gravity.LEFT
                textMessageView.setBackgroundResource(R.drawable.bubble_out_new)
            }

            //textView.setPadding(32, 32, 32, 32)
            textMessageView.layoutParams = layoutParams
            //messageContentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
            val divider = View(this@ChatActivity)
            val dividerLayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                16
            )
            divider.setBackgroundColor(Color.BLACK)
            divider.layoutParams = dividerLayoutParams
            mLinearLayout!!.addView(divider)

            mLinearLayout!!.addView(textMessageView)
            mScrollView!!.fullScroll(View.FOCUS_DOWN)
        } else {
            val inflater = LayoutInflater.from(this)
            val textMessageView = inflater.inflate(R.layout.image_message_box_dialog, mLinearLayout, false)
            val messageImageContentView = textMessageView.findViewById<ImageView>(R.id.messageImageContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageTimestampView.text = timestamp

            Picasso.with(this).load(imageUrl)
                .placeholder(R.drawable.telegram)
                .into(messageImageContentView)

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            if (type == USER_MESSAGE) {
                layoutParams.gravity = Gravity.RIGHT
                textMessageView.setBackgroundResource(R.drawable.bubble_in_new)
            } else {
                layoutParams.gravity = Gravity.LEFT
                textMessageView.setBackgroundResource(R.drawable.bubble_out_new)
            }

            //textView.setPadding(32, 32, 32, 32)
            textMessageView.layoutParams = layoutParams
            val divider = View(this@ChatActivity)
            val dividerLayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                16
            )
            divider.setBackgroundColor(Color.BLACK)
            divider.layoutParams = dividerLayoutParams
            mLinearLayout!!.addView(divider)

            mLinearLayout!!.addView(textMessageView)
            mScrollView!!.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun addMessageBox(userName: String?, timestamp: String?, message: String, type: Int) {
        if ((userName != null) && (userName != "me") && (userName != "not me")){
            val inflater = LayoutInflater.from(this)
            val textMessageView = inflater.inflate(R.layout.text_message_box, mLinearLayout, false)
            val messageSubmitterView = textMessageView.findViewById<TextView>(R.id.userName)
            val messageContentView = textMessageView.findViewById<TextView>(R.id.messageContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageSubmitterView.text = userName
            messageSubmitterView.setTypeface(null, Typeface.BOLD);
            messageSubmitterView.setTextColor(Color.WHITE)
            messageTimestampView.text = timestamp
            messageContentView.text = message
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            if (type == USER_MESSAGE) {
                layoutParams.gravity = Gravity.RIGHT
                textMessageView.setBackgroundResource(R.drawable.bubble_in_new)
            } else {
                layoutParams.gravity = Gravity.LEFT
                textMessageView.setBackgroundResource(R.drawable.bubble_out_new)
            }

            //textView.setPadding(32, 32, 32, 32)
            messageContentView.setTextColor(Color.WHITE)
            textMessageView.layoutParams = layoutParams
            //messageContentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
            val divider = View(this@ChatActivity)
            val dividerLayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                16
            )
            divider.setBackgroundColor(Color.BLACK)
            divider.layoutParams = dividerLayoutParams
            mLinearLayout!!.addView(divider)

            mLinearLayout!!.addView(textMessageView)
            mScrollView!!.fullScroll(View.FOCUS_DOWN)
        } else {
            val inflater = LayoutInflater.from(this)
            val textMessageView = inflater.inflate(R.layout.text_message_box_dialog, mLinearLayout, false)
            val messageContentView = textMessageView.findViewById<TextView>(R.id.messageContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageTimestampView.text = timestamp
            messageContentView.text = message
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            if (type == USER_MESSAGE) {
                layoutParams.gravity = Gravity.RIGHT
                textMessageView.setBackgroundResource(R.drawable.bubble_in_new)
            } else {
                layoutParams.gravity = Gravity.LEFT
                textMessageView.setBackgroundResource(R.drawable.bubble_out_new)
            }

            //textView.setPadding(32, 32, 32, 32)
            messageContentView.setTextColor(Color.WHITE)
            textMessageView.layoutParams = layoutParams
            messageContentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
            val divider = View(this@ChatActivity)
            val dividerLayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                16
            )
            divider.setBackgroundColor(Color.BLACK)
            divider.layoutParams = dividerLayoutParams
            mLinearLayout!!.addView(divider)

            mLinearLayout!!.addView(textMessageView)
            mScrollView!!.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun initializeViews(chatName: String) {
        val avatarUrl: String = intent.getStringExtra("avatarUrl")!!
        println("URLLLL")
        println(avatarUrl)
        navDialogAvatarView = findViewById(R.id.navDialogAvatar)
        if (avatarUrl.length > 0) {
            Picasso.with(this).load(avatarUrl)
                .placeholder(R.drawable.telegram)
                .into(navDialogAvatarView)
        } else {
            val prefix = chatName.take(2).toUpperCase(Locale.ROOT)
            val rnd = Random()
            val color = rnd.nextInt(5)
            if (color == 0) {
                navDialogAvatarView?.setImageResource(R.drawable.red)
            } else if (color == 1) {
                navDialogAvatarView?.setImageResource(R.drawable.green)
            } else if (color == 2) {
                navDialogAvatarView?.setImageResource(R.drawable.blue)
            } else if (color == 3) {
                navDialogAvatarView?.setImageResource(R.drawable.purple)
            } else {
                navDialogAvatarView?.setImageResource(R.drawable.yellow)
            }

            navDialogAvatarStub = findViewById(R.id.navDialogAvatarStub)
            navDialogAvatarStub?.text = prefix
            navDialogAvatarStub?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
            navDialogAvatarStub?.setTypeface(null, Typeface.BOLD);
            navDialogAvatarStub?.setTextColor(Color.WHITE)
        }
        mLinearLayout = findViewById(R.id.layout1)
        mSendButton = findViewById(R.id.sendButton)
        mMessageArea = findViewById(R.id.messageArea)
        mScrollView = findViewById(R.id.scrollView)
        dialogId = intent.getStringExtra("dialogId")
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

    @SuppressLint("StaticFieldLeak")
    inner class MessagesGetter : ViewModel() {
        val messages : List<Message>? = null
        fun getMessages() {
            viewModelScope.launch {
                dialogId?.let {
                    withContext(Dispatchers.IO) { API.api[apiType!!]!!.getMessages(TUID, it).reversed() }.forEach {
                        var messageType = FRIEND_MESSAGE
                        if (it.isUserMessage) {
                            messageType = USER_MESSAGE
                        }
                        addMessageBox(it.userName, it.timestamp, it.text, messageType)
                    }
                }
                mScrollView?.post(Runnable { mScrollView?.fullScroll(ScrollView.FOCUS_DOWN) })
            }
        }
    }
}