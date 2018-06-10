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
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.app_bar_drawer.*


open class Drawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var frameLayout: FrameLayout
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        setSupportActionBar(toolbar)

        frameLayout = findViewById(R.id.frame_layout)
        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> return true
            else -> return super.onOptionsItemSelected(item)
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
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:07752319217")
                startActivity(intent)
                item.isChecked = false
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
                frameLayout.addView(view, params)
            }
            else
            {
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                frameLayout.addView(view, lp)
            }
        }
        else
        {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layoutParams = ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val nestedView = inflater.inflate(layoutResID, frameLayout, false)
            frameLayout.addView(nestedView, layoutParams)
        }
    }
}
