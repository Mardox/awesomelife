package com.mardox.betterlife.app;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mardox.betterlife.app.utils.AlarmController;
import com.mardox.betterlife.app.utils.MenuFunctions;


public class HomeActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "BetterLife" ;
    public static final String TAG = "BetterLife";

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        // Restore preferences
        SharedPreferences storage = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = storage.edit();

        // Set the date of the first launch
        Long firstLaunchDate = storage.getLong("firstLaunchDate", 0);
        if (firstLaunchDate == 0) {
            firstLaunchDate = System.currentTimeMillis();
            editor.putLong("firstLaunchDate", firstLaunchDate);
            editor.commit();
        }


        String title = storage.getString("todayConceptTitle", "Hello");
        String description = storage.getString("todayConceptDescription", "Main Subtitle");


        TextView conceptTitleTextView = (TextView) findViewById(R.id.concept_main_title);
        TextView conceptSubtitleTextView = (TextView) findViewById(R.id.concept_main_subtitle);

        conceptTitleTextView.setText(title);
        conceptSubtitleTextView.setText(description);


        //set daily pick alaram
        AlarmController.dailyVideoAlarm(context);


        //Dismiss the notifications
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.cancel(1);

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
            MenuFunctions.settings(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
