package com.mardox.awesomelife.app.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mardox.awesomelife.app.HomeActivity;
import com.mardox.awesomelife.app.R;
import com.mardox.awesomelife.app.SettingsActivity;
import com.mardox.awesomelife.app.WidgetProvider;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HooMan on 29/03/2014.
 */

public class BackEnd {


    static String packageName;
    static Context contextVariable;

    public void getConcept(final Context context, final boolean notificationEnforced){

        ApplicationInfo packageInfo = context.getApplicationContext().getApplicationInfo();
        packageName = packageInfo.packageName;
        contextVariable = context;

        SharedPreferences storage =
                contextVariable.getSharedPreferences(HomeActivity.PREFS_NAME, contextVariable.MODE_MULTI_PROCESS);

        // Set the date of the first launch
        Long firstLaunchDate = storage.getLong("firstLaunchDate", 0);

        //Calculate the day's # since the first launch
        Long currentDay = ( (System.currentTimeMillis() - firstLaunchDate)/(24 * 60 * 60 * 1000));

        // Create a HashMap which stores Strings as the keys and values
        final Map<String,Object> push = new HashMap<String,Object>();

        Parse.initialize(context, context.getString(R.string.parse_app_id), context.getString(R.string.parse_app_client_key));
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", user.getObjectId().toString());
        ParseCloud.callFunctionInBackground("getDaysTip", params, new FunctionCallback<Map<String, Object>>() {
            public void done(Map<String, Object> mapObject, ParseException e) {
                if (e == null) {

                    String title = mapObject.get("title").toString();
                    String description = mapObject.get("description").toString();

                    // Adding some values to the HashMap
                    push.put("title", title);
                    push.put("subtitle", description);
                    push.put("externalIcon", "");

                    SharedPreferences settings =
                            contextVariable.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_MULTI_PROCESS );
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("todayConceptTitle", title );
                    editor.putString("todayConceptDescription", description );
                    editor.commit();

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(contextVariable);

                    if(notificationEnforced && sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION_SWITCH, true)) {
                        Notification.sendNotification(contextVariable, push);
                        Log.i(HomeActivity.TAG,title);
                    }

                    //Update the widgets
                    Intent intent = new Intent(contextVariable, WidgetProvider.class);
                    intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                    // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
                    // since it seems the onUpdate() is only fired on that:
                    int[] ids = AppWidgetManager.getInstance(contextVariable).getAppWidgetIds(new ComponentName(contextVariable, WidgetProvider.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                    context.sendBroadcast(intent);

                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isScreenOn();
                    Log.i(HomeActivity.TAG, "is screen on:" + isScreenOn );

                } else {
                    Log.e(HomeActivity.TAG, e.toString());
                }
            }
        });

    }



}
