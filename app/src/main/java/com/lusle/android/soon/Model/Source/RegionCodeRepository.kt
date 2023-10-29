package com.lusle.android.soon.Model.Source

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CountryPickerDialog.CCPCountry
import com.lusle.android.soon.Util.CountryPickerDialog.CountryCodePicker

class RegionCodeRepository(private val context: Context) {

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    val regionCode: String
        get() {
            var country = Gson().fromJson(pref.getString(context.getString(R.string.key_region), ""), CCPCountry::class.java)
            if (country == null) {
                val countryPicker = CountryCodePicker(context)
                countryPicker.resetToDefaultCountry()
                country = countryPicker.defaultCountry
                pref.edit().putString(context.getString(R.string.key_region), Gson().toJson(country)).apply()
            }
            return country!!.nameCode
        }
}