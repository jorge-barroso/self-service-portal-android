package com.premfina.esig.selfserviceportal


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        /*val switchPreference = findPreference(SettingsActivity.ownbrowserPreferenceKey) as SwitchPreference
        switchPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            Log.i(this.javaClass.simpleName, preference.key+": "+newValue.toString())
            switchPreference.isChecked = newValue.toString().toBoolean()
            newValue.toString().toBoolean()
        }*/
    }
}

