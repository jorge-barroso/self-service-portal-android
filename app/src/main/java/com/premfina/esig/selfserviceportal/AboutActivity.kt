package com.premfina.esig.selfserviceportal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal.AppPackages
import com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal.AppsUri
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.app_bar_drawer.*

class AboutActivity : Drawer() {

    private lateinit var tabsView: MyCustomTabsView
    private lateinit var homeUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.About)
        addToFrame(R.layout.activity_about)

        bottom_menu.menu.setGroupCheckable(0, false, true)

        brokerPreferences = getSharedPreferences("Broker", Context.MODE_PRIVATE)
        fca_note.text = brokerPreferences.getString("fca_note", fca_note.text.toString())
        legal_advice_note.text = brokerPreferences.getString("legal_advice_notice", legal_advice_note.text.toString())
        homeUrl = brokerPreferences.getString("home_url", "https://www.premfina.com")!!

        broker_label.text = brokerPreferences.getString("name", broker_label.text.toString())

        if (brokerPreferences.contains("facebook")) {
            facebook_logo.visibility = View.VISIBLE
            facebook_logo.setOnClickListener {
                try {
                    val facebookAppInfo = packageManager.getApplicationInfo(AppPackages.FACEBOOK, 0)
                    val baseUrl = brokerPreferences.getString("facebook", null)!!
                    if (facebookAppInfo.enabled)
                        openSocialApp(AppsUri.FACEBOOK + baseUrl)
                    else
                        openSocialNetwork(baseUrl)
                } catch (e: PackageManager.NameNotFoundException) {
                    openSocialNetwork(brokerPreferences.getString("facebook", null)!!)
                }
            }
        }
        if (brokerPreferences.contains("twitter")) {
            twitter_logo.visibility = View.VISIBLE
            twitter_logo.setOnClickListener {
                try {
                    val twitterAppInfo = packageManager.getApplicationInfo(AppPackages.TWITTER, 0)
                    val baseUrl = brokerPreferences.getString("twitter", null)!!
                    if (twitterAppInfo.enabled)
                        openSocialApp(AppsUri.TWITTER + baseUrl)
                    else
                        openSocialNetwork(baseUrl)
                } catch (e: PackageManager.NameNotFoundException) {
                    openSocialNetwork(brokerPreferences.getString("twitter", null)!!)
                }
            }
        }
        if (brokerPreferences.contains("linkedin")) {
            linkedin_logo.visibility = View.VISIBLE
            linkedin_logo.setOnClickListener {
                try {
                    val linkedinAppInfo = packageManager.getApplicationInfo("com.linkedin.android", 0)
                    val baseUrl = brokerPreferences.getString("linkedin", null)!!
                    if (linkedinAppInfo.enabled)
                        openSocialApp(AppsUri.LINKEDIN + baseUrl)
                    else
                        openSocialNetwork(baseUrl)
                } catch (e: PackageManager.NameNotFoundException) {
                    openSocialNetwork(brokerPreferences.getString("linkedin", null)!!)
                }
            }
        }

        tabsView = MyCustomTabsView(this, homeUrl, arrayListOf(brokerPreferences.getString("facebook", "")!!, brokerPreferences.getString("twitter", "")!!, brokerPreferences.getString("linkedin", "")!!))

        if (!brokerPreferences.getString("funded", "true")!!.toBoolean())
            about_title.text = about_title.text.replace("Premfina".toRegex(), brokerPreferences.getString("name", "Premfina")!!)
    }

    fun openPortal(view: View) {
        tabsView.show()
    }

    fun openSocialNetwork(url: String) {
        tabsView.show(Uri.parse(url))
    }

    fun openSocialApp(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
