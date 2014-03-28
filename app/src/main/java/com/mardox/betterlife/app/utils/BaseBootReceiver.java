package com.mardox.betterlife.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by HooMan on 2/03/14.
 */
public class BaseBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("Daily","function was triggered");
            AlarmController.setDailyVideoAlarm(context, false);

        }

    }

}
