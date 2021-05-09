package com.aggregator.ui.activities


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
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
import com.aggregator.models.Message
import com.aggregator.ui.fragments.TUID
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
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

        messagesGetter.getMessages()
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

            messageContentView.setTextColor(Color.WHITE)
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

    @SuppressLint("StaticFieldLeak")
    inner class MessagesGetter : ViewModel() {
        val messages : List<Message>? = null
        fun getMessages() {
            viewModelScope.launch {
                dialogId?.let {
                    withContext(Dispatchers.IO) { API.api[apiType!!]!!.getMessages(TUID, it).sortedBy { it.unixTs } }.forEach {
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
                                            }

                                            override fun onError(error: com.downloader.Error?) {
                                                Toast.makeText(baseContext, "Failed to download the " + it.attachementUrl, Toast.LENGTH_SHORT)
                                                    .show()
                                            }

                                        })
                                } catch (e: Exception) {
                                }

                                addFileMessageBox(it.userName, it.timestamp, splitted[splitted.size - 1], messageType)
                            }
                        } else {
                            addMessageBox(it.userName, it.timestamp, it.text, messageType)
                        }

                    }
                }
                mScrollView?.post(Runnable { mScrollView?.fullScroll(ScrollView.FOCUS_DOWN) })

                val progressBar = findViewById<ProgressBar>(R.id.progress)
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun readFile(fileName: String) {
        return try {
            try {
                println("filesDir")
                baseContext.filesDir.list().forEach { println(it) }
                println("cacheDir")
                baseContext.cacheDir.list().forEach { println(it) }
                println("codeCacheDir")
                baseContext.codeCacheDir.list().forEach { println(it) }
                println("dataDir")
                baseContext.dataDir.list().forEach { println(it) }
                println("END")
                //baseContext.externalCacheDir
                //fileName
                val file = File(baseContext.filesDir.absolutePath + "/" + fileName)
                println(file.totalSpace)
                println(file.isDirectory)
                println(file.isAbsolute)
                println(file.isFile)
            } catch (e: Exception) {
                println(e.toString())
            }

            val reader = BufferedReader(InputStreamReader(baseContext.openFileInput(fileName)))
            reader.use {
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                val text = sb.toString()
                Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()
            }
        } catch (ex: FileNotFoundException) {
            Toast.makeText(baseContext, "Error in reading the file $fileName", Toast.LENGTH_SHORT)
                .show()
        }
    }
    /**
     * Background Async Task to download file
     */
    /*internal class DownloadFileFromURL() : AsyncTask<String?, String?, String?>() {
        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        override fun onPreExecute() {
            super.onPreExecute()
            //showDialog(progress_bar_type)
        }

        /**
         * Downloading file in background thread
         */
        protected override fun doInBackground(vararg x: String?): String? {
            var count: Int = 0
            try {
                val url = URL(x[0])
                //val connection: URLConnection = url.openConnection()
                //connection.connect()

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                //val lenghtOfFile: Int = connection.getContentLength()

                // download the file
                val input: InputStream = BufferedInputStream(
                    url.openStream(),
                    8192
                )

                // Output stream
                val output: OutputStream = FileOutputStream(
                    Environment
                        .getExternalStorageDirectory().toString()
                            + "/" + x[1]
                )
                println(Environment
                    .getExternalStorageDirectory().toString()
                        + "/" + x[1])
                val data = ByteArray(1024)
                var total: Long = 0
                while ((input.read(data).also({ count = it })) != -1) {
                    total += count.toLong()
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    //publishProgress("" + ((total * 100) / lenghtOfFile).toInt())

                    // writing data to file
                    output.write(data, 0, count)
                }

                // flushing output
                output.flush()

                // closing streams
                output.close()
                input.close()
            } catch (e: Exception) {
                //Log.e("Error: ", e.message)
            }
            return Environment.getExternalStorageDirectory().toString() + "/" + x[1]
        }

        /**
         * Updating progress bar
         */
        protected override fun onProgressUpdate(vararg values: String?) {
            // setting progress percentage
            //pDialog.setProgress(progress[0].toInt())
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String?) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type)
        }
    }*/
}