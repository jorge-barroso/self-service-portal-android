package com.premfina.esig.selfserviceportal

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.*
import android.support.v4.content.ContextCompat

class MyCustomTabsView(ctx: Context, url: String, otherUrls: ArrayList<String> = ArrayList(0)) {
    private val CUSTOM_TAB_PACKAGE_NAME: String = "com.android.chrome"
    private lateinit var customTabsClient: CustomTabsClient
    private lateinit var customTabsIntent: CustomTabsIntent
    private val defaultUrl: Uri = Uri.parse(url)
    private val ctx: Context = ctx
    private var ready: Boolean = false
    init{
        val bundles = ArrayList<Bundle>(otherUrls.size)
        for(otherUrls in otherUrls)
        {
            val newBundle = Bundle()
            newBundle.putParcelable(CustomTabsService.KEY_URL, Uri.parse(otherUrls))
            bundles.add(newBundle)
        }

        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                customTabsClient = client
                customTabsClient.warmup(0)
                val customTabsSession = customTabsClient.newSession(object: CustomTabsCallback(){})
                customTabsSession.mayLaunchUrl(defaultUrl, Bundle(), bundles)

                customTabsIntent = CustomTabsIntent.Builder(customTabsSession)
                        .setToolbarColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))))
                        .setSecondaryToolbarColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(ctx, R.color.colorPrimary))))
                        .setCloseButtonIcon(BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_arrow_back_white_86dp))
                        .build()
                customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse(Intent.URI_ANDROID_APP_SCHEME.toString() + "//" + ctx.packageName))
                ready = true
            }

            override fun onServiceDisconnected(name: ComponentName) {

            }
        }

        CustomTabsClient.bindCustomTabsService(ctx, CUSTOM_TAB_PACKAGE_NAME, connection)
    }

    fun show(url: Uri = defaultUrl) {
        customTabsIntent.launchUrl(ctx, defaultUrl)
    }

    fun isReady() : Boolean {
        return ready
    }
}