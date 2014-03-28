package com.mardox.betterlife.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mardox.betterlife.app.utils.BackEnd;

/**
 * Created by HooMan on 28/03/2014.
 */
public class DailyConceptService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        final Context mContext = context;

        new Thread(new Runnable() {

            @Override
            public void run() {
                BackEnd backendConnection = new BackEnd();
                backendConnection.getConcept(mContext);
            }
        }).start();


    }




}
