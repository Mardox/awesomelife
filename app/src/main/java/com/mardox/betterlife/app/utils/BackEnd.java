package com.mardox.betterlife.app.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mardox.betterlife.app.HomeActivity;
import com.mardox.betterlife.app.SettingsActivity;
import com.mardox.betterlife.app.WidgetProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HooMan on 29/03/2014.
 */

public class BackEnd {


    static String packageName;
    static Context contextVariable;

    public void getConcept(Context context, boolean notificationEnforced){

        ApplicationInfo packageInfo = context.getApplicationContext().getApplicationInfo();
        packageName = packageInfo.packageName;
        contextVariable = context;
        String output;

        try {

            SharedPreferences storage = contextVariable.getSharedPreferences(HomeActivity.PREFS_NAME, contextVariable.MODE_MULTI_PROCESS);

            // Set the date of the first launch
            Long firstLaunchDate = storage.getLong("firstLaunchDate", 0);

            //Calculate the day's # since the first launch
            Long currentDay =( (System.currentTimeMillis() - firstLaunchDate)/(24 * 60 * 60 * 1000));

            //Build the query
            String query = "http://30daylabs.com/cloud/api/concept?day="+currentDay.toString()+"&format=json";

            Log.i(HomeActivity.TAG, query);

            URI url = new URI(query);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);

            // Create a HashMap which stores Strings as the keys and values
            Map<String,Object> push = new HashMap<String,Object>();


            JSONObject responseJSON = new JSONObject(output);


            String title = responseJSON.getString("title");

            String description = responseJSON.getString("description");

            // Adding some values to the HashMap
            push.put("title", title);
            push.put("subtitle", description);
            push.put("externalIcon", "");


            SharedPreferences settings = contextVariable.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_MULTI_PROCESS );
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
            int[] ids = AppWidgetManager.getInstance(contextVariable).getAppWidgetIds(new ComponentName(contextVariable, WidgetProvider.class));;
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            context.sendBroadcast(intent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Execute HTTP Post Request
    }



}
