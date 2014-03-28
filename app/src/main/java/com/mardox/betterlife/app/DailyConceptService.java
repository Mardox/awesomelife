package com.mardox.betterlife.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.mardox.betterlife.app.utils.Notification;

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
 * Created by HooMan on 28/03/2014.
 */
public class DailyConceptService extends BroadcastReceiver {


    String packageName;
    Context contextVariable;

    @Override
    public void onReceive(Context context, Intent intent) {

        ApplicationInfo packageInfo = context.getApplicationContext().getApplicationInfo();
        packageName = packageInfo.packageName;
        contextVariable = context;
        getVideo.start();

    }



    Thread getVideo = new Thread(new Runnable() {

        @Override
        public void run() {

            String output;
            int random;
            try {

                SharedPreferences storage = contextVariable.getSharedPreferences(HomeActivity.PREFS_NAME, contextVariable.MODE_MULTI_PROCESS);

                // Set the date of the first launch
                Long firstLaunchDate = storage.getLong("firstLaunchDate", 0);

                //Calculate the day's # since the first launch
                Long currentDay =( (System.currentTimeMillis() - firstLaunchDate)/(24 * 60 * 60 * 1000));

                //Build the query
                String query = "http://30daylabs.com/cloud/api/concept?day="+currentDay.toString()+"&format=json";

                Log.i(HomeActivity.TAG,query);

                URI url = new URI(query);

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);

                // Create a HashMap which stores Strings as the keys and values
                Map<String,Object> push = new HashMap<String,Object>();


                JSONObject responseJSON = new JSONObject(output);


//                String[] notification_titles = contextVariable.getResources().getStringArray(R.array.notification_titles);
//                random = (int)(Math.random() * (notification_titles.length));
//                Log.i("Daily random title count", Integer.toString(notification_titles.length));
//                Log.i("Daily random title",Integer.toString(random));
//                String title = notification_titles[random];

                String title = responseJSON.getString("title");

                String description = responseJSON.getString("description");
//                String externalIcon = responseJSON.getString("videoUrl");

                // Adding some values to the HashMap
                push.put("title", title);
                push.put("subtitle", description);
                push.put("externalIcon", "");

                Log.i(HomeActivity.TAG,title);

                SharedPreferences settings = contextVariable.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_MULTI_PROCESS );
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("todayConceptTitle", title );
                editor.putString("todayConceptDescription", description );
                editor.commit();

                if(!settings.getBoolean("noDailyAlert",false)) {
                    Notification.sendNotification(contextVariable, push);
                }


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


    });
}
