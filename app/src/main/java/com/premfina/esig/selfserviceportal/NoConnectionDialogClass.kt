package com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

class NoConnectionDialogClass : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(0)
                    .setPositiveButton(0) { _, _ ->

                    }
                    .setNegativeButton(0) { _, _ ->
                        it.finish()
                    }

                    .create()
        } ?: throw IllegalStateException("")
    }
}