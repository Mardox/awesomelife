package com.mardox.awesomelife.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.RemoteInput;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Map;

public class ProjectActivity extends ActionBarActivity {


    Context context = this;

    Button initiateProjectBT;

    private static final String ACTION_RESPONSE = "com.mardox.awesomelife.app.REPLY";

    private BroadcastReceiver mReceiver;


    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    String replyLabel;

    String[] replyChoices;

    String projectTitle;
    String projectDescription;
    String currentNodeObjectId;

    ParseObject currentNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processResponse(intent);
            }
        };


        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_app_client_key));

        initiateProjectBT = (Button) findViewById(R.id.initiate_project_bt);

        replyLabel = getString(R.string.reply_label);
        replyChoices = getResources().getStringArray(R.array.reply_choices);

        initiateProjectBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the project object from the back-end
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Package");
                query.getInBackground("gX5O11uJva", new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            // project object
                            currentNodeObjectId = object.getString("startAction");
                            projectTitle = object.getString("title");
                            projectDescription = object.getString("description");
                            executeSequence(currentNodeObjectId);
                        } else {
                            // something went wrong
                        }
                    }
                });

            }
        });


        }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(ACTION_RESPONSE));
    }

    @Override
    protected void onPause() {
//        NotificationManagerCompat.from(this).cancel(0);
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    public void executeSequence(String currentNodeObjectId){


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Package");
        query.getInBackground(currentNodeObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // project object
                    currentNode = object;

                    int notificationId = 0;

                    // Create intent for reply action
                    //Cancel the current on reply from the notification
                    Intent replyIntent = new Intent(ACTION_RESPONSE);
                    PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent,
                            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

                    // Build the notification
                    NotificationCompat.Builder replyNotificationBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_action_about)
                                    .setContentTitle(object.getString("title"))
                                    .setContentText(object.getString("description"))
                                    .setContentIntent(replyPendingIntent);

                    // Create the remote input
                    RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                            .setLabel(replyLabel)
                            .setChoices(replyChoices)
                            .build();

                    // Create wearable notification and add remote input
                    Notification replyNotification =
                            new WearableNotifications.Builder(replyNotificationBuilder)
                                    .addRemoteInputForContentIntent(remoteInput)
                                    .build();

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(context);

                    // Build the notification and issues it with notification manager.
                    notificationManager.notify(notificationId, replyNotification);


                } else {
                    // something went wrong
                }
            }
        });

    }


    private void processResponse(Intent intent) {
        String text = intent.getStringExtra(EXTRA_VOICE_REPLY);
        if (text != null && !text.equals("")) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
