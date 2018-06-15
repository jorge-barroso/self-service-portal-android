package com.premfina.esig.selfserviceportal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.app_bar_drawer.view.*
import kotlinx.android.synthetic.main.content_drawer.*


open class Drawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        toolbar.label_text.text = sharedPreferences.getString("username", "")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        if(!sharedPreferences.contains("username"))
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val headerView = nav_view.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.navigation_username)
        val navUserEmail: TextView = headerView.findViewById(R.id.navigation_email)

        navUsername.text = sharedPreferences.getString("username", "")
        navUserEmail.text = sharedPreferences.getString("email", "")

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId)
        {
            R.id.add_button -> {
                startActivity(Intent(this, AddAgreementActivity::class.java))
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.nav_contact -> {
                startActivity(Intent(this, ContactActivity::class.java))
            }
            R.id.nav_call -> {
                item.isChecked = false
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:07752319217")
                startActivity(intent)
            }
            R.id.nav_settings -> {
                //startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
            R.id.nav_logout -> {
                sharedPreferences.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun addToFrame(layoutResID: Int, view: View? = null, params: ViewGroup.LayoutParams? = null) {
        if(view!=null)
        {
            if(params!=null)
            {
                frame_layout.addView(view, params)
            }
            else
            {
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                frame_layout.addView(view, lp)
            }
        }
        else
        {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layoutParams = ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val nestedView = inflater.inflate(layoutResID, frame_layout, false)
            frame_layout.addView(nestedView, layoutParams)
        }
    }
}
