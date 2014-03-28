package com.mardox.betterlife.app;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.mardox.betterlife.app.utils.AlarmController;


public class HomeActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "better_life" ;
    public static final String TAG = "BetterLife";

    Context context = this;
    SharedPreferences storage;

    String title ;
    String description ;

    TextView conceptTitleTextView ;
    TextView conceptSubtitleTextView ;

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

        //set daily pick alaram
        AlarmController.setDailyVideoAlarm(context, false);

    }


    @Override
    protected void onStart(){
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
        setTheConcept();
    };


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

    private void setTheConcept(){

        title = storage.getString("todayConceptTitle", "Hello");
        description = storage.getString("todayConceptDescription", "Main Subtitle");


        conceptTitleTextView = (TextView) findViewById(R.id.concept_main_title);
        conceptSubtitleTextView = (TextView) findViewById(R.id.concept_main_subtitle);

        conceptTitleTextView.setText(title);
        conceptSubtitleTextView.setText(description);

    }




//    public void showTimePickerDialog(View v) {
//        DialogFragment newFragment = new TimePickerFragment(){
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                // Do something with the time chosen by the user
//                // Restore preferences
//                SharedPreferences storage = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
//                SharedPreferences.Editor editor = storage.edit();
//
//                editor.putInt("alarmHourOfDay", hourOfDay);
//                editor.putInt("alarmMinuteOfDay", minute);
//                editor.commit();
//
//                //set daily pick alaram
//                AlarmController.setDailyVideoAlarm(context);
//
//
//            }
//        };
//        newFragment.show(getSupportFragmentManager(), "timePicker");
//
//    }



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
