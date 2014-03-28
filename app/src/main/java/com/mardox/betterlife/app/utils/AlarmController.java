package com.mardox.betterlife.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.mardox.betterlife.app.DailyConceptService;
import com.mardox.betterlife.app.HomeActivity;

import java.util.Calendar;


/**
 * Created by HooMan on 3/03/14.
 */
public class AlarmController {


    public static final int TIMER_1 = 1;
    public static void setDailyVideoAlarm(Context context){

        long _alarmFinal;

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmMainIntent = new Intent(context, DailyConceptService.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, TIMER_1, alarmMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel current alarm
        try {
            alarmMgr.cancel(pendingAlarmIntent);
//            Log.d(HomeActivity.TAG, "Alarm is deleted");
        } catch (Exception e) {
            Log.e(HomeActivity.TAG, "AlarmManager update was not canceled. " + e.toString());
        }

        //Set a new alarm
        Calendar alaramCalendar = Calendar.getInstance();
        Calendar nowCalendar = Calendar.getInstance();

        SharedPreferences storage = context.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_MULTI_PROCESS );

        int hourOfDay = storage.getInt("alarmHourOfDay", 17);
        int minuteOfDay = storage.getInt("alarmMinuteOfDay", 00);

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
