package com.lusle.android.soon.View.Main.Setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.gson.Gson
import com.lusle.android.soon.MySuggestionProvider
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CountryPickerDialog.CCPCountry
import com.lusle.android.soon.Util.CountryPickerDialog.CountryCodeDialog
import com.lusle.android.soon.Util.CountryPickerDialog.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
public class PreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var pref: SharedPreferences

    private val DEFAULT_NAME_CODE = "kr"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        setUpReleaseAlarmPreference()
        setUpCompanyAlarmPreference()
        setRegionPreference()
        setClearHistoryPreference()
        setUpAppInfoPreference()
        setLincensePreference()
    }

    private fun setLincensePreference() {
        findPreference<Preference>(getString(R.string.key_license))!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            false
        }
    }

    private fun setUpAppInfoPreference() {
        findPreference<Preference>(getString(R.string.key_app_info))!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            findNavController().navigate(R.id.action_preferenceFragment_to_appInfoFragment)
            false
        }
    }

    private fun setUpCompanyAlarmPreference() {
        findPreference<Preference>(getString(R.string.key_company_alarm))!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            findNavController().navigate(R.id.action_preferenceFragment_to_companyAlarmSettingFragment)
            false
        }
    }

    private fun setUpReleaseAlarmPreference() {
        findPreference<Preference>(getString(R.string.key_release_alarm))!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            findNavController().navigate(R.id.action_preferenceFragment_to_releaseAlarmSettingFragment)
            false
        }
    }

    private fun setUpRegion() {
        val country = CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, CountryCodePicker.Language.KOREAN, DEFAULT_NAME_CODE)
        pref.edit().putString(requireContext().getString(R.string.key_region), Gson().toJson(country)).apply()
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
            val countryCodePicker = CountryCodePicker(context)
            val country = Gson().fromJson(
                    pref.getString(getString(R.string.key_region), null),
                    CCPCountry::class.java
            )
            countryCodePicker.setDialogKeyboardAutoPopup(false)
            countryCodePicker.selectedCountry = country
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.KOREAN)
            countryCodePicker.setOnCountryChangeListener {
                val country = countryCodePicker.selectedCountry
                preference.summary = country.name
                pref.edit().putString(getString(R.string.key_region), Gson().toJson(country)).apply()
            }
            CountryCodeDialog.openCountryCodeDialog(countryCodePicker, pref.getString(getString(R.string.key_region), null))
            false
        }
        if (pref.getString(getString(R.string.key_region), null) == null) setUpRegion()
        val countryJson = pref.getString(getString(R.string.key_region), "")
        findPreference<Preference>(getString(R.string.key_region))!!.summary = Gson().fromJson(countryJson, CCPCountry::class.java).name
    }
}