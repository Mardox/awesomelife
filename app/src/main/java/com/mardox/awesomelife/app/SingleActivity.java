package com.mardox.awesomelife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SingleActivity extends Activity {


    EditText itemTitle;
    EditText itemDescription;
    String ID;
    String TITLE;
    String DESCRIPTION;
    Context context = this;
    private static Handler handler;
    ProgressBar progBar;
    RelativeLayout mainLayout;
    ScrollView mainSV;
    boolean editFlag;

    ParseUser user;

    ParseObject parseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);


        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        ID = i.getStringExtra("id");
        TITLE = i.getStringExtra("title");
        DESCRIPTION = i.getStringExtra("description");

        progBar = (ProgressBar) findViewById(R.id.prgLoading);
        itemTitle = (EditText) findViewById(R.id.new_tip_title);
        itemDescription = (EditText) findViewById(R.id.new_tip_description);
        mainLayout = (RelativeLayout) findViewById(R.id.main_single_layout);
        mainSV = (ScrollView) findViewById(R.id.main_single_sv);

        itemTitle.setText(TITLE);
        itemDescription.setText(DESCRIPTION);


        Spinner locationSpinner = (Spinner) findViewById(R.id.location_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> locationSdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        locationSdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        locationSpinner.setAdapter(locationSdapter);


        Spinner deviceSpinner = (Spinner) findViewById(R.id.device_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> deviceAdapter = ArrayAdapter.createFromResource(this,
                R.array.device_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        deviceSpinner.setAdapter(deviceAdapter);



        if(ID != null) {
            itemTitle.setEnabled(false);
            itemDescription.setEnabled(false);
            setTitle("Update Tip");

            mainSV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableEdit();
                }
            });


            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTip");
            query.getInBackground(ID, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        parseItem = object;
                    } else {
                        // something went wrong
                    }
                }
            });

        }else{
            setTitle("New Tip");
        }

        user = ParseUser.getCurrentUser();

    }



    private void addItem(){

        progBar.setVisibility(View.VISIBLE);

        if(!itemTitle.getText().toString().equals("") && ID == null ) {

            //Parse: Add the user tip
            ParseObject parseTip = new ParseObject("UserTip");
            parseTip.put("title", itemTitle.getText().toString() );
            parseTip.put("description", itemDescription.getText().toString() );
            parseTip.put("user", user);
            parseTip.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    finish();
                }
            });

        }else if(!itemTitle.getText().toString().equals("") && ID != null && editFlag) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTip");

            // Retrieve the object by id
            query.getInBackground(ID, new GetCallback<ParseObject>() {
                public void done(ParseObject updateTip, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data.
                        updateTip.put("title", itemTitle.getText().toString());
                        updateTip.put("description", itemDescription.getText().toString());
                        updateTip.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                finish();
                            }
                        });
                    }
                }
            });

        }else{
            finish();
        }

    }


    /**
     * Delete dialog
     */
    private void deleteDialog() {

        //Create the upgrade dialog
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.delete_item_dialog_title))
                .setMessage(R.string.delte_dialog_message)
                .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // reset the request
                        //Parse: Add the user tip
                        progBar.setVisibility(View.VISIBLE);
                        parseItem.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    private  void enableEdit(){
        itemTitle.setEnabled(true);
        itemDescription.setEnabled(true);
        editFlag = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(ID !=null)
            getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                addItem();
                return true;
            case R.id.menu_action_edit:
                enableEdit();
                return true;
            case R.id.menu_action_delete:
                deleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Override the back button
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event)
    {
        if (keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            addItem();
        }
        return true;
    }





}
