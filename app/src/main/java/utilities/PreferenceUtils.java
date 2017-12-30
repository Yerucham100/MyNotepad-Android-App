package utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.diacious.mynotepad.PasswordActivity;
import com.example.diacious.mynotepad.R;

/**
 * Created by Akhihiero David(Yerucham) on 12/18/2017.
 */

public class PreferenceUtils
{

    private static boolean themeChangedAtRuntime = false;
    private PreferenceUtils(){}//PreferenceUtils should not be instantiated


    /**
     * Method to get the current theme color
     * @param context A context reference to access a shared preferences reference
     * @return An int corresponding to the id of the current theme color
     * @throws IllegalArgumentException if the theme in shared preferences does not match any existing theme
     */
    public static int getThemeId(Context context) throws IllegalArgumentException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme;
        int themeId = 0;
        if (sharedPreferences.contains(context.getString(R.string.theme_key)))
        {
            theme = sharedPreferences.getString(context.getString(R.string.theme_key), context.getString(R.string.blue_value));

            if (theme.equals(context.getString(R.string.blue_value)))
                themeId = R.style.BlueTheme;
            else if (theme.equals(context.getString(R.string.red_value)))
                themeId = R.style.RedTheme;
            else if (theme.equals(context.getString(R.string.yellow_value)))
                themeId = R.style.YellowTheme;
            else if (theme.equals(context.getString(R.string.pink_value)))
                themeId = R.style.PinkTheme;
            else if (theme.equals(context.getString(R.string.black_value)))
                themeId = R.style.BlackTheme;
            else
                throw new IllegalArgumentException("Unknown Theme");
        }

        return themeId;
    }

    /**
     * Method to get the primary color of the current Theme
     * @param context The calling activity context
     * @return The id of the primary color
     * @throws IllegalArgumentException if the theme in shared preferences does not match any existing theme
     */
    public static int getThemePrimaryColor(Context context) throws IllegalArgumentException
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme;
        int colorId = 0;
        if (sharedPreferences.contains(context.getString(R.string.theme_key)))
        {
            theme = sharedPreferences.getString(context.getString(R.string.theme_key), context.getString(R.string.blue_value));

            if (theme.equals(context.getString(R.string.blue_value)))
                colorId = R.color.colorBlue;
            else if (theme.equals(context.getString(R.string.red_value)))
                colorId = R.color.colorRed;
            else if (theme.equals(context.getString(R.string.yellow_value)))
                colorId = R.color.colorYellow;
            else if (theme.equals(context.getString(R.string.pink_value)))
                colorId = R.color.colorPink;
            else if (theme.equals(context.getString(R.string.black_value)))
                colorId = R.color.colorBlack;
            else
                throw new IllegalArgumentException("Unknown Theme");
        }

        return colorId;
    }

    /**
     * Method to save in shared preferences that password option is turned on
     */
    public static void passwordTurnedOn(boolean isPasswordOn, Context context)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(PasswordActivity.PASSWORD_TURNED_ON_OR_OFF_BOOL_KEY, isPasswordOn);
        editor.apply();
    }

    /**
     * Method to tell if password is set up or not
     * @param context The calling activity context
     * @return True if password is set up else false
     */
    public static boolean isPasswordOn(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(PasswordActivity.PASSWORD_TURNED_ON_OR_OFF_BOOL_KEY))
            return sharedPreferences.getBoolean(PasswordActivity.PASSWORD_TURNED_ON_OR_OFF_BOOL_KEY, false);
        return false;
    }

    /**
     * Method to change set themeChangedAtRuntime variable
     * @param newValue The new value of themeChangedAtRuntime
     */
    public static void setThemeChangedAtRuntime(boolean newValue)
    {
        themeChangedAtRuntime = newValue;
    }

    /**
     * Method to prevent main activity from launching signin activity when theme is changed at runtime
     * @return
     */
    public static boolean themeChangedAtRuntime()
    {
        return themeChangedAtRuntime;
    }

    /**
     * Method to check if a key is in shared preferences
     * @param key The key to check for
     * @param context The calling activity context
     * @return True if key is found else false
     */
    public static boolean containsKey(String key, Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains(key);
    }

}
