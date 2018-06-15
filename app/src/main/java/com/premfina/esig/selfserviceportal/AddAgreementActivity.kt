package com.premfina.esig.selfserviceportal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class AddAgreementActivity : Drawer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(R.layout.activity_add_agreement)
    }
}
