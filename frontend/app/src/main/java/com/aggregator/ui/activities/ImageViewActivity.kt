package com.aggregator.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aggregator.api.API
import com.aggregator.ui.fragments.TUID
import com.squareup.picasso.Picasso
import uk.co.senab.photoview.PhotoViewAttacher
import java.util.*

class ImageViewActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val intent: Intent = intent
        val titleView = findViewById<TextView>(R.id.navBarDialogName)
        titleView.text = "Image View"
        val backView = findViewById<ImageView>(R.id.exitDialog)
        backView.setOnClickListener {
            finish()
        }

        val imageUrl = intent.getStringExtra("url")

        val messageImageContentView = findViewById<ImageView>(R.id.imageViewField)

        Picasso.with(this).load(imageUrl)
            .placeholder(R.drawable.telegram)
            .into(messageImageContentView)

        var pAttacher = PhotoViewAttacher(messageImageContentView)
        pAttacher.update()

    }
}
