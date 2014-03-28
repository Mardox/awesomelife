package com.mardox.betterlife.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.mardox.betterlife.app.utils.AlarmController;

/**
 * Created by HooMan on 29/03/2014.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_DAILY_NOTIFICATION_SWITCH = "daily_notification_key";
    public static final String KEY_PREF_DAILY_NOTIFICATION_TIME = "daily_notification_time";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_PREF_DAILY_NOTIFICATION_SWITCH)) {
            // Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            // connectionPref.setSummary(sharedPreferences.getString(key, ""));

        }

        AlarmController.setDailyVideoAlarm(this);

        Log.i(HomeActivity.TAG,"Main Pref Change");

    }


    @Override
    protected void onStart(){
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    };


    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
