package com.example.diacious.mynotepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;

import com.example.diacious.mynotepad.MainActivity;
import com.example.diacious.mynotepad.R;

import java.util.prefs.Preferences;

/**
 * Created by Akhihiero David(Yerucham) on 12/18/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat
{
    Preference passwordPreference;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.note_prefs);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        passwordPreference =  preferenceScreen.getPreference(1);
        passwordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent toPasswordActivityIntent = new Intent(getContext(), PasswordActivity.class);
                startActivity(toPasswordActivityIntent);
                return true;
            }
        });

        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        int prefCount = preferenceScreen.getPreferenceCount();

        for (int i = 0;i < prefCount;i++)
        {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference))
            {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    /**
     * Method to set preference summary
     * @param preference Preference object
     * @param value The current value of the preference
     */
    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference)
        {
           ListPreference listPreference = (ListPreference) preference;
           int listPreferenceIndex =  listPreference.findIndexOfValue(value);
           listPreference.setSummary(listPreference.getEntries()[listPreferenceIndex]);
        }
    }
}
