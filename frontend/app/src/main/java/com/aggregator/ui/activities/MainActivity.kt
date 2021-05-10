package com.aggregator.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.aggregator.catchat.*
import com.aggregator.ui.fragments.FacebookFragment
import com.aggregator.ui.fragments.GmailFragment
import com.aggregator.ui.fragments.TelegramFragment
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Telegram"
        //toolbar.setBackgroundColor(Color.parseColor("#859D7D"))
        setSupportActionBar(toolbar)
        val d = resources.getDrawable(R.drawable.ic_tab_background)
        getActionBar()?.setBackgroundDrawable(d)
        val transaction = supportFragmentManager.beginTransaction()
        //transaction.add(R.id.content_frame, GmailFragment())
        transaction.add(R.id.content_frame, TelegramFragment())
        transaction.commit()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = object: ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        ) {
            override fun onDrawerStateChanged(newState: Int) {
                hideSoftKeyboard()
                super.onDrawerStateChanged(newState)
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        drawer.setOnTouchListener { v, event ->
            this.hideSoftKeyboard()
            false
        }
        toolbar.setOnTouchListener { v, event ->
            this.hideSoftKeyboard()
            false
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null

        // Initialize PRDownloader with read and connection timeout
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment: Fragment = when (item.itemId) {
            R.id.nav_facebook -> FacebookFragment()
            R.id.nav_gmail -> GmailFragment()
            else -> TelegramFragment()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment)
        transaction.commit()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
