package com.aggregator.ui.activities


import android.annotation.SuppressLint
import android.content.*
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aggregator.api.API
import com.aggregator.api.TgApi
import com.aggregator.models.Message
import com.aggregator.store.Callback
import com.aggregator.store.RespType
import com.aggregator.store.Response
import com.aggregator.store.Storage
import com.aggregator.ui.fragments.TUID
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.net.URLConnection
import java.io.File
import java.util.*
import java.util.regex.Pattern


//

/**
 * A class that represents chat dialog between two users in the application
 */
class ChatActivity : AppCompatActivity(), Callback {
    private var mLinearLayout: LinearLayout? = null
    private var mSendButton: ImageView? = null
    private var mMessageArea: EditText? = null
    private var mScrollView: ScrollView? = null
    private var dialogId: String? = null
    private var navDialogAvatarView: CircleImageView? = null
    private var navDialogAvatarStub: TextView? = null
    private var apiType: String? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        Storage.subscribe(this)
        val intent: Intent = intent
        apiType = intent.getStringExtra("api")!!
        val titleView = findViewById<TextView>(R.id.navBarDialogName)
        val avatar = findViewById<TextView>(R.id.navDialogAvatarStub)
        val chatName = intent.getStringExtra("chatName") ?: "Telegram chat"
        titleView.text = chatName

        registerForContextMenu(titleView)
        registerForContextMenu(avatar)

        val backView = findViewById<ImageView>(R.id.exitDialog)
        backView.setOnClickListener {
            finish()
        }
        initializeViews(chatName)

        mSendButton?.setOnClickListener {
            val text = mMessageArea?.text ?: ""
            if (text.length > 0) {
                dialogId?.let { it1 -> API.api[apiType!!]!!.sendTextMessage(TUID, it1, text.toString()) }
                val curDate = Calendar.getInstance().time
                val calCur = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
                calCur.time = curDate
                val ts = calCur[Calendar.HOUR_OF_DAY].toString() + ":" + calCur[Calendar.MINUTE].toString().padStart(2, '0')
                addMessageBox("Me", ts, text.toString(), USER_MESSAGE)

                val scrollView = findViewById<ScrollView>(R.id.scrollView)
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }

                mMessageArea!!.setText(EMPTY_MESSAGE)
            }
        }


        //addImageMessageBox(null, "20:20", "http://84.252.137.106/avatars/-1001103359717.jpg", FRIEND_MESSAGE)
        //addImageMessageBox(null, "20:21", "http://84.252.137.106/photos/5240047619848385219.jpg", FRIEND_MESSAGE)
        //addImageMessageBox(null, "20:21", "http://84.252.137.106/files/0a4dcb92fa2d3c601b58d72720d6bec4.jpg", FRIEND_MESSAGE)
        //progressBar.visibility = View.VISIBLE
        dialogId?.let {
            Storage.onLoad(Response(RespType.getMESSAGES, Pair(apiType!!, it)))
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
//            messagesGetter.getMessages()
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.copy_name -> {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val titleView = findViewById<TextView>(R.id.navBarDialogName)
                clipboard.setText( titleView.text )

                return true
            }
            R.id.copy_nick -> {
                dialogId?.let {

                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val user_nick = TgApi().getUsernameById(TUID, it)
                    clipboard.setText( user_nick )

                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addFileMessageBox(userName: String?, timestamp: String?, fileName: String, type: Int) {
        if ((userName != null) && (userName != "me") && (userName != "not me")){
            val inflater = LayoutInflater.from(this)
            val textMessageView = inflater.inflate(R.layout.file_message_box, mLinearLayout, false)

            val messageSubmitterView = textMessageView.findViewById<TextView>(R.id.userName)
            val messageFileContentView = textMessageView.findViewById<Button>(R.id.messageFileContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageSubmitterView.text = userName
            messageSubmitterView.setTypeface(null, Typeface.BOLD);
            messageSubmitterView.setTextColor(Color.WHITE)
            messageTimestampView.text = timestamp

            messageFileContentView.text = "FILE: " + fileName
            messageFileContentView.setOnClickListener {
                val file = File(baseContext.filesDir.absolutePath + "/" + fileName)

                val myMime: MimeTypeMap = MimeTypeMap.getSingleton()
                val newIntent = Intent(Intent.ACTION_VIEW)

                val mimeType: String =
                    myMime.getMimeTypeFromExtension(file.extension).toString()
                println(applicationContext.packageName.toString())
                newIntent.setDataAndType(
                    getUriForFile(applicationContext, applicationContext.packageName.toString() +
                            ".provider", file), mimeType)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                try {
                    startActivity(newIntent)
                } catch (e: ActivityNotFoundException) {
                }
            }

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
            val textMessageView = inflater.inflate(R.layout.file_message_box_dialog, mLinearLayout, false)
            val messageFileContentView = textMessageView.findViewById<Button>(R.id.messageFileContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageTimestampView.text = timestamp

            messageFileContentView.text = "FILE: " + fileName
            messageFileContentView.setOnClickListener {
                val file = File(baseContext.filesDir.absolutePath + "/" + fileName)

                val myMime: MimeTypeMap = MimeTypeMap.getSingleton()
                val newIntent = Intent(Intent.ACTION_VIEW)

                val mimeType: String =
                    myMime.getMimeTypeFromExtension(file.extension).toString()
                println(applicationContext.packageName.toString())
                newIntent.setDataAndType(
                    getUriForFile(applicationContext, applicationContext.packageName.toString() +
                            ".provider", file), mimeType)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                try {
                    startActivity(newIntent)
                } catch (e: ActivityNotFoundException) {
                }
            }

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

    private fun addImageMessageBox(userName: String?, timestamp: String?, imageUrl: String, type: Int) {
        if ((userName != null) && (userName != "me") && (userName != "not me")){
            val inflater = LayoutInflater.from(this)
            val textMessageView = inflater.inflate(R.layout.image_message_box, mLinearLayout, false)

            val messageSubmitterView = textMessageView.findViewById<TextView>(R.id.userName)
            val messageImageContentView = textMessageView.findViewById<ImageView>(R.id.messageImageContent)
            val messageTimestampView = textMessageView.findViewById<TextView>(R.id.messageBoxTimestamp)

            messageImageContentView.setOnClickListener {
                println("IMAGE WAS CLICKED!\n")
                val myIntent = Intent(this, ImageViewActivity::class.java)
                myIntent.putExtra("url", imageUrl)
                startActivity(myIntent)
            }

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


            messageImageContentView.setOnClickListener {

            }

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

            messageContentView.makeLinks(
                getEmailAddressesInString(message)!!.map {
                    Pair(it, View.OnClickListener {view ->
                        val contactIntent = Intent(this, ChatActivity::class.java)
                        contactIntent.putExtra("chatName", it)
                        contactIntent.putExtra("dialogId", "")
                        contactIntent.putExtra("api", "gmail")
                        contactIntent.putExtra("avatarUrl", "")

                        startActivity(contactIntent)
                    })
                })

            messageContentView.makeLinks(
                getLinksInString(message)!!.map {
                    Pair(it, View.OnClickListener { view ->
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        startActivity(browserIntent)
                    })
                })
            messageContentView.makeLinks(
                getNicksInString(message)!!.map{
                    Pair(it, View.OnClickListener { view ->
                        val nickDId = TgApi().getIdByUsername(TUID, it)
                        println(nickDId)
                        val contactIntent = Intent(this, ChatActivity::class.java)
                        contactIntent.putExtra("chatName", it)
                        contactIntent.putExtra("dialogId", nickDId)
                        contactIntent.putExtra("api", "telegram")
                        contactIntent.putExtra("avatarUrl", "")
                        startActivity(contactIntent)
                    })
                })
            messageContentView.movementMethod = LinkMovementMethod.getInstance();
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
            messageContentView.makeLinks(
                getEmailAddressesInString(message)!!.map {
                    Pair(it, View.OnClickListener {view ->
                        val contactIntent = Intent(this, ChatActivity::class.java)
                        contactIntent.putExtra("chatName", it)
                        contactIntent.putExtra("dialogId", "")
                        contactIntent.putExtra("api", "gmail")
                        contactIntent.putExtra("avatarUrl", "")

                        startActivity(intent)
                    })
                })

            messageContentView.makeLinks(
                getLinksInString(message)!!.map {
                    Pair(it, View.OnClickListener { view ->
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        startActivity(browserIntent)
                    })
                })
            messageContentView.makeLinks(
                getNicksInString(message)!!.map{
                    Pair(it, View.OnClickListener { view ->
                        dialogId?.let {it1 ->
                            val cunick = TgApi().getUsernameById(TUID, it1)
                            if (it != cunick) {
                                val nickDId = TgApi().getIdByUsername(TUID, it)
                                val contactIntent = Intent(this, ChatActivity::class.java)
                                contactIntent.putExtra("chatName", it)
                                contactIntent.putExtra("dialogId", nickDId)
                                contactIntent.putExtra("api", "telegram")
                                contactIntent.putExtra("avatarUrl", "")
                                startActivity(contactIntent)
                            }
                        }

                    })
                })
            messageContentView.movementMethod = LinkMovementMethod.getInstance();
        }
    }

    private fun getEmailAddressesInString(text: String): ArrayList<String>? {
        val emails: ArrayList<String> = ArrayList()
        val matcher =
            Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")
                .matcher(text)
        while (matcher.find()) {
            emails.add(matcher.group())
        }
        return emails
    }

    private fun getLinksInString(text: String): ArrayList<String>? {
        val links: ArrayList<String> = ArrayList()
        val matcher =
            Pattern.compile("\\b(https?|ftp|file|):\\/\\/\\S*")
                .matcher(text)
        while (matcher.find()) {
            links.add(matcher.group())
        }
        return links
    }

    private fun getNicksInString(text: String): ArrayList<String>? {
        val nicks: ArrayList<String> = ArrayList()
        val matcher =
            Pattern.compile("\\B@\\S+")
                .matcher(text)
        while (matcher.find()) {
            nicks.add(matcher.group())
        }
        return nicks
    }

    private fun TextView.makeLinks(links: List<Pair<String, View.OnClickListener>>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
                    textPaint.color = textPaint.linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
            //      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun initializeViews(chatName: String) {
        val avatarUrl: String = intent.getStringExtra("avatarUrl")!!
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
        private const val MESSAGE_KEY = "message"
        private const val USER_KEY = "user"
        private const val EMPTY_MESSAGE = ""
        private const val USER_MESSAGE = 1
        private const val FRIEND_MESSAGE = 2
    }

    override fun onLoad(arg: Response<*>) {
        (arg.response as List<Message>).forEach {
            var messageType = FRIEND_MESSAGE
            if (it.isUserMessage) {
                messageType = USER_MESSAGE
            }
            if (it.attachementUrl.length > 0) {
                if (it.attachementType == "photo") {
                    addImageMessageBox(it.userName, it.timestamp, it.attachementUrl, messageType)
                } else {
                    val splitted = it.attachementUrl.split("/")
                    try {
                        PRDownloader.download(
                            it.attachementUrl,
                            baseContext.filesDir.absolutePath,
                            splitted[splitted.size - 1]
                        )
                            .build()
                            .start(object : OnDownloadListener {
                                override fun onDownloadComplete() {
                                    //readFile(splitted[splitted.size - 1])
                                    println("ok")
                                }

                                override fun onError(error: com.downloader.Error?) {
                                    Toast.makeText(baseContext, "Failed to download the " + it.attachementUrl, Toast.LENGTH_SHORT)
                                        .show()
                                }

                            })
                    } catch (e: Exception) {
                        print(e.toString())
                    }
                    addFileMessageBox(it.userName, it.timestamp, splitted[splitted.size - 1], messageType)
                }
            } else {
                addMessageBox(it.userName, it.timestamp, it.text, messageType)
            }

        }
        mScrollView?.post(Runnable { mScrollView?.fullScroll(ScrollView.FOCUS_DOWN) })

        val progressBar = findViewById<ProgressBar>(R.id.progress)
        progressBar.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        Storage.unsubscribe(this)
        super.onDestroy()
    }
}