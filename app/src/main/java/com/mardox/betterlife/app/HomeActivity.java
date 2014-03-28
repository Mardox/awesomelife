package com.mardox.betterlife.app;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.mardox.betterlife.app.utils.AlarmController;
import com.mardox.betterlife.app.utils.BackEnd;


public class HomeActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "better_life" ;
    public static final String TAG = "BetterLife";

    Context context = this;
    SharedPreferences storage;

    String title ;
    String description ;

    TextView conceptTitleTextView ;
    TextView conceptSubtitleTextView ;

    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Restore preferences
        storage = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = storage.edit();

        // Set the date of the first launch
        Long firstLaunchDate = storage.getLong("firstLaunchDate", 0);
        if (firstLaunchDate == 0) {
            firstLaunchDate = System.currentTimeMillis();
            editor.putLong("firstLaunchDate", firstLaunchDate);
            editor.commit();
        }


        conceptTitleTextView = (TextView) findViewById(R.id.concept_main_title);
        conceptSubtitleTextView = (TextView) findViewById(R.id.concept_main_subtitle);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                description = storage.getString("todayConceptDescription", "");
                conceptSubtitleTextView.setText(description);

                title = storage.getString("todayConceptTitle", "");
                conceptTitleTextView.setText(title);
            }
        };

        //set daily pick alarm, no forced update
        AlarmController.setDailyVideoAlarm(context, false);

        startBackendCall();

    }


    @Override
    protected void onStart(){
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    };


    private void startBackendCall(){

        //Check if the internet connection is available
        if(!isOnline()){
            networkErrorDialog();
            return;
        }


        backendCall.start();

    }

    Thread backendCall = new Thread(new Runnable() {

        @Override
        public void run() {

            try {
                //If the current concept already exists
                if(storage.getString("todayConceptTitle", "").equals("")) {
                    BackEnd backendConnection = new BackEnd();
                    //get the new concept from the backend without the notification trigger
                    backendConnection.getConcept(context, false);
                }

                Message msg = new Message();
                handler.sendMessage(msg);
            } catch (Exception e) {
                Log.v("Error", e.toString());
            }

        }
    });


    @Override
    protected void onResume(){
        super.onResume();
        //Dismiss the notifications
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.cancel(1);

    };

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }


    /**
     * Check if Internet connectuion is available
     */

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    /**
     * Network connection error dialog
     */
    private void networkErrorDialog() {

        //Create the upgrade dialog
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(R.string.no_internet_message)
                .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // reset the request
                        startBackendCall();
                    }
                })
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(context , SettingsActivity.class);
            context.startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
