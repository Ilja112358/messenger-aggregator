package com.example.catchat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Telegram"
        toolbar.setBackgroundColor(Color.parseColor("#859D7D"))
        setSupportActionBar(toolbar)

        val transaction = supportFragmentManager.beginTransaction()
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var fragment: Fragment? = null
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        when (id) {
            R.id.nav_gmail -> {
                toolbar?.title = "Gmail"
                fragment = GmailFragment()
            }
            R.id.nav_facebook -> {
                toolbar?.title = "Facebook"
                fragment = FacebookFragment()
            }
            else -> {
                toolbar?.title = "Telegram"
                fragment = TelegramFragment()
            }
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
