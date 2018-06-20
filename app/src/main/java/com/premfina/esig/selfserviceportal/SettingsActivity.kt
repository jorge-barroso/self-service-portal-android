package com.premfina.esig.selfserviceportal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem


class SettingsActivity : AppCompatActivity() {
    companion object {
        const val ownbrowserPreferenceKey: String = "ownbrowser_preference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
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
