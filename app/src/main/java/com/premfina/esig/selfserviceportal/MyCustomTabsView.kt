package com.premfina.esig.selfserviceportal

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.*
import android.support.v4.content.ContextCompat

class MyCustomTabsView(ctx: Context, url: String, otherUrls: ArrayList<String> = ArrayList(0)) {
    private val CUSTOM_TAB_PACKAGE_NAME: String = "com.android.chrome"
    private lateinit var customTabsClient: CustomTabsClient
    private val customTabsIntent: CustomTabsIntent
    private val defaultUrl: Uri = Uri.parse(url)
    private val ctx: Context = ctx
    init{
        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                customTabsClient = client
            }

            override fun onServiceDisconnected(name: ComponentName) {

            }
        }

        val bundles = ArrayList<Bundle>(otherUrls.size)
        for(otherUrls in otherUrls)
        {
            val newBundle = Bundle()
            newBundle.putParcelable(CustomTabsService.KEY_URL, Uri.parse(otherUrls))
            bundles.add(newBundle)
        }

        CustomTabsClient.bindCustomTabsService(ctx, CUSTOM_TAB_PACKAGE_NAME, connection)
        customTabsClient.warmup(0)
        val customTabsSession = customTabsClient.newSession(object: CustomTabsCallback(){})
        customTabsSession.mayLaunchUrl(defaultUrl, Bundle(), bundles)

        customTabsIntent = CustomTabsIntent.Builder(customTabsSession)
                .setToolbarColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(ctx, R.color.colorPrimary))))
                .setSecondaryToolbarColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))))
                .build()
    }

    fun show(url: Uri = defaultUrl) {
        customTabsIntent.launchUrl(ctx, defaultUrl)
    }
}