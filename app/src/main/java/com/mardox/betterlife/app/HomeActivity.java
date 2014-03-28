package com.mardox.betterlife.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mardox.betterlife.app.utils.AlarmController;
import com.mardox.betterlife.app.utils.BaseBootReceiver;
import com.mardox.betterlife.app.utils.MenuFunctions;


public class HomeActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "BetterLife" ;
    public static final String TAG = "BetterLife";

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Start the daily video alarm - reboot persistant
        ComponentName receiver = new ComponentName(context, BaseBootReceiver.class);
        PackageManager pm = context.getPackageManager();


        //set daily pick alaram
        AlarmController.dailyVideoAlarm(context);
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
