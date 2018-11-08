package com.premfina.esig.selfserviceportal

import android.os.Bundle
import kotlinx.android.synthetic.main.app_bar_drawer.*


class SettingsActivity : Drawer() {
    companion object {
        const val ownbrowserPreferenceKey: String = "ownbrowser_preference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, SettingsFragment()).commit()

        bottom_menu.menu.setGroupCheckable(0, false, true)
    }
}
