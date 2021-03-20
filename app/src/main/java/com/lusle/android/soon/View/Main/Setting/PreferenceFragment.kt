package com.lusle.android.soon.View.Main.Setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.lusle.android.soon.MySuggestionProvider
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CountryPickerDialog.CountryCodeDialog
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker

/**
 * A simple [Fragment] subclass.
 */
public class PreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var pref: SharedPreferences
    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        setRegionPreference()
        setClearHistoryPreference()
    }

    private fun setUpRegion() {
        val c = CountryPicker.Builder().with(requireContext()).build().countryFromSIM
        pref.edit().putString(requireContext().getString(R.string.key_region), Gson().toJson(c)).apply()
    }

    private fun setClearHistoryPreference() {
        findPreference<Preference>(getString(R.string.key_clear_search_history))!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("알림").setMessage("삭제시 복구할 수 없습니다").setPositiveButton("삭제") { dialog, _ ->
                val suggestions = SearchRecentSuggestions(context,
                        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
                suggestions.clearHistory()
                dialog.dismiss()
            }.setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            builder.show()
            false
        }
    }

    private fun setRegionPreference() {
        findPreference<Preference>(getString(R.string.key_region))!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
            val builder = CountryPicker.Builder().with(requireContext())
                    .listener { country ->
                        preference.summary = country.name
                        pref.edit().putString(getString(R.string.key_region), Gson().toJson(country)).apply()
                    }
            builder.build().showBottomSheet(context as AppCompatActivity?)
            false
        }
        if (pref.getString(getString(R.string.key_region), "") == "") setUpRegion()
        val countryJson = pref.getString(getString(R.string.key_region), "")
        findPreference<Preference>(getString(R.string.key_region))!!.summary = Gson().fromJson(countryJson, Country::class.java).name
    }
}