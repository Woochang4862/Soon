package com.lusle.android.soon.View.Main.Setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lusle.android.soon.MySuggestionProvider;
import com.lusle.android.soon.R;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private SharedPreferences preferences;

    public static PreferenceFragment newInstance() {
        PreferenceFragment fragment = new PreferenceFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        setRegionPreference();
        setClearSearchHistory();
    }

    private void setRegionPreference() {
        Preference regionPref = findPreference(getString(R.string.key_region));
        Country currC = new Gson().fromJson(preferences.getString(getString(R.string.key_region), ""), Country.class);
        if(currC == null) currC = new CountryPicker.Builder().with(getContext()).build().getCountryFromSIM();
        regionPref.setSummary(currC.getName());
        regionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CountryPicker.Builder builder =
                        new CountryPicker.Builder().with(getContext())
                                .listener(new OnCountryPickerListener() {
                                    @Override
                                    public void onSelectCountry(Country country) {
                                        preference.setSummary(country.getName());
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString(getString(R.string.key_region), new Gson().toJson(country));
                                        editor.apply();
                                    }
                                });

                builder.build().showBottomSheet((AppCompatActivity) getContext());
                return false;
            }
        });
    }

    private void setClearSearchHistory() {
        findPreference(getString(R.string.key_clear_search_history)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setTitle("알림")
                        .setMessage("삭제시 복수할 수 없습니다")
                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("삭제", (dialog, which) -> {
                            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(), MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                            suggestions.clearHistory();
                            dialog.dismiss();
                            Toast.makeText(getContext(), "검색 기록이 삭제 되었습니다", Toast.LENGTH_SHORT).show();
                        });
                builder.show();
                return false;
            }
        });
    }
}
