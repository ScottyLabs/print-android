package org.scottylabs.print;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tom on 2/18/2018.
 */

public class SettingsManager {

    private static String SETTING_ANDREW_ID="ANDREW_ID";
    private static String SETTINGS_NAME="SETTINGS";

    private Context context;

    public SettingsManager(Context context) {
        this.context = context;
    }

    // Loads the andrew ID from settings, returns null if not set
    public String getAndrewId() {
        SharedPreferences settings;
        settings = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        return settings.getString(SETTING_ANDREW_ID,"");
    }

    public void setAndrewId(String andrewId) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SETTING_ANDREW_ID,andrewId);
        editor.apply();
    }

}
