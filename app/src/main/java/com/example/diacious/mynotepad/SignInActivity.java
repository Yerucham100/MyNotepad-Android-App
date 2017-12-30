package com.example.diacious.mynotepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import utilities.PreferenceUtils;

public class SignInActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private Button loginButton;
    public static final String PASSWORD_VERIFIED_KEY = "password-verified";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        passwordEditText = (EditText) findViewById(R.id.verify_password_et);
        loginButton = (Button) findViewById(R.id.login_btn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     * Method to set up app background colors
     */
    private void setUpTheme() {

        int themeId = PreferenceUtils.getThemeId(this);
        setTheme(themeId);
    }

    /**
     * Method to login to my notepad
     */
    private void login()
    {
        String errorMsg = getString(R.string.incorrect_password);
        String passwordFieldBlank = getString(R.string.blank_password);

        Toast errorMsgToast = Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT);

        String enteredPassword = passwordEditText.getText().toString();
        String correctPassword = null;
        if (enteredPassword.trim().isEmpty())
        {
            Toast.makeText(this, passwordFieldBlank, Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(PasswordActivity.PASSWORD_KEY))
        {
            correctPassword = sharedPreferences.getString(PasswordActivity.PASSWORD_KEY, "");
        }

        if (!correctPassword.equals(enteredPassword))
        {
            errorMsgToast.show();
            return;
        }
        else
        {
            Intent toMainActivityIntent = new Intent(this, MainActivity.class);
            toMainActivityIntent.putExtra(PASSWORD_VERIFIED_KEY, true);
            finish();
            startActivity(toMainActivityIntent);
        }
    }
}
