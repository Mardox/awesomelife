package com.mardox.awesomelife.app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.mardox.awesomelife.app.utils.AlarmController;
import com.mardox.awesomelife.app.utils.BackEnd;


public class HomeActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "better_life" ;
    public static final String TAG = "awesomelife";

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


        //Show help overlay
        boolean overlay_shown = storage.getBoolean("helpOverlay", false);
        if(!overlay_shown){
            //Call the set overlay, you can put the logic of checking if overlay is already called
            // with a simple sharedpreference
            showOverLay();
        }

        conceptTitleTextView = (TextView) findViewById(R.id.concept_main_title);
        conceptSubtitleTextView = (TextView) findViewById(R.id.concept_main_subtitle);

        //Handler to update UI after the backend thread
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
        AlarmController.setDailyVideoAlarm(context, true);

        //Get the first concept
        startBackendCall();

        //App rate offer
        appOfferDialog();

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

    //Thread to get the first concept upon the first launch of the app
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
     * Show the overlay guide
     */
    private void showOverLay(){


        final Dialog step0 = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        step0.setContentView(R.layout.help_overlay_step_0);
        LinearLayout introLayout = (LinearLayout) step0.findViewById(R.id.overlayLayout);
        introLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Dialog step1 = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                step1.setContentView(R.layout.help_overlay_step_1);
                LinearLayout firstLayout = (LinearLayout) step1.findViewById(R.id.overlayLayout);
                firstLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        final Dialog step2 = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                        step2.setContentView(R.layout.help_overlay_step_2);
                        LinearLayout secondLayout = (LinearLayout) step2.findViewById(R.id.overlayLayout);
                        secondLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {

                                final Dialog step3 = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                                step3.setContentView(R.layout.help_overlay_step_3);
                                LinearLayout thirdLayout = (LinearLayout) step3.findViewById(R.id.overlayLayout);
                                thirdLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        step0.dismiss();
                                        step1.dismiss();
                                        step2.dismiss();
                                        step3.dismiss();
                                        // We need an Editor object to make preference changes.
                                        // All objects are from android.context.Context
                                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putBoolean("helpOverlay", true);

                                        // Commit the edits!
                                        editor.commit();

                                    }
                                });

                                step3.show();
                            }
                        });

                        step2.show();
                    }
                });

                step1.show();
            }

        });
        step0.show();
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



    /**
     * appOfferDialog Dialog
     */
    public void appOfferDialog() {

        final int LAUNCHES_UNTIL_UPGRADE_PROMPT = 2;
        final int DAYS_UNTIL_UPGRADE_PROMPT = 2;

        SharedPreferences prefs = getSharedPreferences(HomeActivity.PREFS_NAME, MODE_MULTI_PROCESS);
        Boolean dont_show_rate_again  = prefs.getBoolean("dontshowrateagain", false);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long firstLaunchDate = prefs.getLong("firstLaunchDate", 0);
        if (firstLaunchDate == 0) {
            firstLaunchDate = System.currentTimeMillis();
            editor.putLong("firstLaunchDate", firstLaunchDate);
        }


        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_UPGRADE_PROMPT ) {
            if (System.currentTimeMillis() >= firstLaunchDate +
                    (DAYS_UNTIL_UPGRADE_PROMPT * 24 * 60 * 60 * 1000)) {

                //generate a random number [1,2]
                int randomDay = 1 + (int)(Math.random()*3);
                if(randomDay == 1) {
                    //Upgrade offer

                }else if(!dont_show_rate_again && randomDay == 2){
                    rateDialog(editor);
                }

            }
        }

        editor.commit();

    }





    private void rateDialog(final SharedPreferences.Editor editor){

        final String APP_PNAME = getPackageName();

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rate_offer_title))
                .setItems(R.array.rating_response, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                //Rate Now
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                            case 1:
                                //Rate Later
                            case 2:
                                //Never
                                if (editor != null) {
                                    editor.putBoolean("dontshowrateagain", true);
                                    editor.commit();
                                }
                        }

                    }

                })
                .setIcon(R.drawable.ic_action_dark_important)
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
        }else if (id == R.id.action_help) {

            showOverLay();
        }
        return super.onOptionsItemSelected(item);
    }

}
