package com.example.diacious.mynotepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import utilities.PreferenceUtils;

public class PasswordActivity extends AppCompatActivity
{

    private EditText passwordEditText;
    private EditText reenterPasswordEditText;
    private TextView passwordStateTextView;
    private Button savePasswordButton;
    private CheckBox showPasswordCheckBox;
    private Switch passwordASwitch;

    public static final String PASSWORD_KEY = "password_key_for_shared_prefs";
    public static final String PASSWORD_TURNED_ON_OR_OFF_BOOL_KEY = "is_password_on";
    private boolean wasPasswordTurnedOnOrOff = false;
    private boolean valueForPasswordTurnedOnOrOff = false;



    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        setUpTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordEditText = (EditText) findViewById(R.id.password_et);
        reenterPasswordEditText = (EditText) findViewById(R.id.password_reenter_et);
        passwordStateTextView = (TextView) findViewById(R.id.password_state_tv);
        passwordASwitch = (Switch) findViewById(R.id.password_switch);

        passwordASwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasPasswordTurnedOnOrOff = true;
                if (passwordASwitch.isChecked())
                {
                    passwordEditText.setVisibility(View.VISIBLE);
                    reenterPasswordEditText.setVisibility(View.VISIBLE);
                    showPasswordCheckBox.setVisibility(View.VISIBLE);
                    savePasswordButton.setVisibility(View.VISIBLE);
                    valueForPasswordTurnedOnOrOff = true;
                    passwordStateTextView.setText(getString(R.string.password_enabled));

                }
                else
                {
                    passwordEditText.setVisibility(View.INVISIBLE);
                    reenterPasswordEditText.setVisibility(View.INVISIBLE);
                    showPasswordCheckBox.setVisibility(View.INVISIBLE);
                    savePasswordButton.setVisibility(View.INVISIBLE);
                    valueForPasswordTurnedOnOrOff = false;
                    passwordStateTextView.setText(getString(R.string.password_disabled));
                }
            }
        });
        showPasswordCheckBox = (CheckBox) findViewById(R.id.show_password_checkbox);

        showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPasswordCheckBox.isChecked())
                {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    reenterPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else
                {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    reenterPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        savePasswordButton = (Button) findViewById(R.id.save_password_btn);

        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePassword())
                {
                    Intent toSettingsActivityIntent = new Intent(PasswordActivity.this, SettingsActivity.class);
                    startActivity(toSettingsActivityIntent);
                }
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(PASSWORD_TURNED_ON_OR_OFF_BOOL_KEY))
        {
            if (preferences.getBoolean(PASSWORD_TURNED_ON_OR_OFF_BOOL_KEY, false))
            {
                passwordASwitch.setChecked(true);
                passwordASwitch.callOnClick();
            }
        }

    }

    /**
     * Method to set up app background colors
     */
    private void setUpTheme() {

        int themeId = PreferenceUtils.getThemeId(this);
        setTheme(themeId);
    }

    /**
     * Method to save the password to shared preferences
     * @return True if successful, else false
     */
    private boolean savePassword()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String errorMsgForMismatchedPasswords = getString(R.string.mismatched_passwords);
        Toast showErrorMsgForMismatchedPasswords = Toast.makeText(this, errorMsgForMismatchedPasswords, Toast.LENGTH_SHORT);

        String errorMsgForBlankPassword = getString(R.string.blank_password);
        Toast showErrorMsgForBlankPassword = Toast.makeText(this, errorMsgForBlankPassword, Toast.LENGTH_SHORT);

        String password = passwordEditText.getText().toString();
        String reenteredPassword = reenterPasswordEditText.getText().toString();
        if (!password.equals(reenteredPassword))
        {
            showErrorMsgForMismatchedPasswords.show();
            passwordEditText.setText("");
            reenterPasswordEditText.setText("");
            return false;
        }
        else if (password.trim().isEmpty())
        {
            showErrorMsgForBlankPassword.show();
            return false;
        }
        else if (password.trim().length() < 4)
        {
            Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_SHORT).show();
            return false;
        }
        editor.putString(PASSWORD_KEY, password);
        editor.apply();

        return true;
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

        Intent toSettingsActivityIntent = new Intent(PasswordActivity.this, SettingsActivity.class);
        startActivity(toSettingsActivityIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wasPasswordTurnedOnOrOff)
        {
            if (PreferenceUtils.containsKey(PASSWORD_KEY, this))
            {
                PreferenceUtils.passwordTurnedOn(valueForPasswordTurnedOnOrOff, this);
                overridePendingTransition(0, 0);
            }
            else
            {
                PreferenceUtils.passwordTurnedOn(false, this);
                overridePendingTransition(0, 0);
            }
        }
        finish();
    }
}
