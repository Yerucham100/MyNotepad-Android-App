package com.example.diacious.mynotepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

import utilities.PreferenceUtils;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

    private final String TAG = "SettingsActivity.class";
    private boolean preferenceChanged = false;
    private final String PREFERENCE_CHANGED = "preference_changed";
    private Intent toPreviousActivityIntent;
    public static final String SETTINGS_ACTIVITY = "settings-activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        Intent intent = getIntent();
        if (intent != null)
            if (intent.hasExtra(PREFERENCE_CHANGED))
                preferenceChanged = intent.getBooleanExtra(PREFERENCE_CHANGED, false);
    }

    /**
     * Method to set up app background colors
     */
    private void setUpTheme() {

        int themeId = PreferenceUtils.getThemeId(this);
        setTheme(themeId);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Intent intent = new Intent(this, SettingsActivity.class);

        if (key.equals(getString(R.string.theme_key)))
            PreferenceUtils.setThemeChangedAtRuntime(true);

        Intent thatStartedThisActivityIntent = getIntent();
        if (thatStartedThisActivityIntent != null)
        {
            if (thatStartedThisActivityIntent.hasExtra(NoteActivity.NOTE_ACTIVITY))
            {
                intent.putExtra(NoteActivity.NOTE_ACTIVITY,
                        thatStartedThisActivityIntent.getStringExtra(NoteActivity.NOTE_ACTIVITY));
                intent.putExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY,
                        thatStartedThisActivityIntent.getStringExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY));
                intent.putExtra(MainActivity.NOTE_ID,
                        thatStartedThisActivityIntent.getLongExtra(MainActivity.NOTE_ID, 0));
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(PREFERENCE_CHANGED, true);

        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent thatStartedThisActivityIntent = getIntent();
        if (thatStartedThisActivityIntent != null)
        {
            if (thatStartedThisActivityIntent.hasExtra(NoteActivity.NOTE_ACTIVITY))
            {
                toPreviousActivityIntent = new Intent(this, NoteActivity.class);
                toPreviousActivityIntent.putExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY,
                        thatStartedThisActivityIntent.getStringExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY));
                toPreviousActivityIntent.putExtra(MainActivity.NOTE_ID,
                        thatStartedThisActivityIntent.getLongExtra(MainActivity.NOTE_ID, 0));
            }
            else
            {
                toPreviousActivityIntent = new Intent(this, MainActivity.class);
                toPreviousActivityIntent.putExtra(SETTINGS_ACTIVITY, SETTINGS_ACTIVITY);
            }
        }
        else
        {
            toPreviousActivityIntent = new Intent(this, MainActivity.class);
            toPreviousActivityIntent.putExtra(SETTINGS_ACTIVITY, SETTINGS_ACTIVITY);
        }

        if (preferenceChanged)
        {
            toPreviousActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
        }
        toPreviousActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toPreviousActivityIntent);
    }

}
