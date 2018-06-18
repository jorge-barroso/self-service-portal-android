package com.premfina.esig.selfserviceportal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem


class SettingsActivity : AppCompatActivity() {
    companion object {
        const val ownbrowserPreferenceKey: String = "ownbrowser_preference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /*supportActionBar?.setDisplayShowHomeEnabled(true)*/

        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> {
                false
            }
        }
    }
}
