package com.mardox.awesomelife.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mardox.awesomelife.app.DailyConceptService;
import com.mardox.awesomelife.app.HomeActivity;

import java.util.Calendar;


/**
 * Created by HooMan on 3/03/14.
 */
public class AlarmController {


    public static final int TIMER_1 = 1;
    public static void setDailyVideoAlarm(Context context, boolean update){

        long _alarmFinal;



        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmMainIntent = new Intent(context, DailyConceptService.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, TIMER_1, alarmMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, alarmMainIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

        if(alarmUp || !update){
                Log.d(HomeActivity.TAG, "Alarm exists");
        }else{

            // Cancel current alarm
            try {
                alarmMgr.cancel(pendingAlarmIntent);
                Log.d(HomeActivity.TAG, "Alarm is deleted");
            } catch (Exception e) {
                Log.e(HomeActivity.TAG, "AlarmManager update was not canceled. " + e.toString());
            }

            //Set a new alarm
            Calendar alaramCalendar = Calendar.getInstance();
            Calendar nowCalendar = Calendar.getInstance();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String[] pieces = prefs.getString("daily_notification_time","17:00").split(":");


            int hourOfDay = Integer.parseInt(pieces[0]);
            int minuteOfDay =  Integer.parseInt(pieces[1]);

            Log.i(HomeActivity.TAG,"Alarm Change :"+hourOfDay + " and " + minuteOfDay);

            alaramCalendar.setTimeInMillis(System.currentTimeMillis());
            alaramCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            alaramCalendar.set(Calendar.MINUTE, minuteOfDay);

            //Make sure alarm is set for the next day
            if(alaramCalendar.getTimeInMillis() <= nowCalendar.getTimeInMillis())
                _alarmFinal = alaramCalendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
            else
                _alarmFinal = alaramCalendar.getTimeInMillis();

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    _alarmFinal,
                   24*60*60*1000, pendingAlarmIntent);
            Log.i(HomeActivity.TAG, "Alarm set");


        }
    }


}
