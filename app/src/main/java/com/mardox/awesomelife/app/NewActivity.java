package com.mardox.awesomelife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NewActivity extends Activity {


    Button submitButton;
    EditText itemTitle;
    EditText itemDescription;
    String ID;
    String TITLE;
    String DESCRIPTION;
    Context context = this;
    private static Handler handler;
    ProgressBar progBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);


        Intent i = getIntent();
        ID = i.getStringExtra("id");
        TITLE = i.getStringExtra("title");
        DESCRIPTION = i.getStringExtra("description");

        progBar = (ProgressBar) findViewById(R.id.prgLoading);
        submitButton = (Button) findViewById(R.id.button_new_submit);
        itemTitle = (EditText) findViewById(R.id.new_tip_title);
        itemDescription = (EditText) findViewById(R.id.new_tip_description);

        itemTitle.setText(TITLE);
        itemDescription.setText(DESCRIPTION);

        if(ID != null) {
            itemTitle.setEnabled(false);
            itemDescription.setEnabled(false);
            submitButton.setText("Update");
            submitButton.setVisibility(View.GONE);
            setTitle("Update Tip");
        }else{
            Menu deleteMenu = (Menu) findViewById(R.id.menu_action_delete);
            setTitle("Add a New Tip");
        }


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemTitle.getText().toString().equals("")) {
                    progBar.setVisibility(View.VISIBLE);
                    if (postItemData.getState() == Thread.State.NEW)
                        postItemData.start();
                }else{
                    //empty item title error
                }
            }
        });



        //Handler to update UI after the backend thread
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progBar.setVisibility(View.GONE);
                finish();
            }
        };


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
                        progBar.setVisibility(View.VISIBLE);
                        if (deleteItem.getState() == Thread.State.NEW)
                            deleteItem.start();


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
        submitButton.setVisibility(View.VISIBLE);
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




    Thread postItemData = new Thread( new Runnable() {

        @Override
        public void run() {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.backend_url)+"api/concept.json");


            // Create a new HttpClient and Post Header
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("title", itemTitle.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("description", itemDescription.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("uid", HomeActivity.userID));

            if(ID != null) {
                nameValuePairs.add(new BasicNameValuePair("itemid", ID));
            }

            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);

                Message msg = new Message();
                handler.sendMessage(msg);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    });




    Thread deleteItem = new Thread( new Runnable() {

        @Override
        public void run() {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(getString(R.string.backend_url)+"api/delete_concept?format=json&uid="+ HomeActivity.userID+"&itemid="+ID);


            try {
                if(!ID.isEmpty() && !HomeActivity.userID.isEmpty()) {
                    HttpResponse response = httpclient.execute(httpget);
                }

                Message msg = new Message();
                handler.sendMessage(msg);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    });








}
