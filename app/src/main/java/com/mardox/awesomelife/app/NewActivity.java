package com.mardox.awesomelife.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    Button deleteButton;
    EditText newItemTitle;
    EditText newItemDescription;
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
        deleteButton = (Button) findViewById(R.id.button_delete);
        newItemTitle = (EditText) findViewById(R.id.new_tip_title);
        newItemDescription = (EditText) findViewById(R.id.new_tip_description);

        newItemTitle.setText(TITLE);
        newItemDescription.setText(DESCRIPTION);

        if(ID != null) {
            submitButton.setText("Update");
            deleteButton.setVisibility(View.VISIBLE);
            setTitle("Update Tip");
        }else{
            setTitle("Add a New Tip");
        }


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newItemTitle.getText().toString().equals("")) {
                    progBar.setVisibility(View.VISIBLE);
                    if (postItemData.getState() == Thread.State.NEW)
                        postItemData.start();
                }else{
                    //empty item title error
                }
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newItemTitle.getText().toString().equals("")) {
                    progBar.setVisibility(View.VISIBLE);
                    if (deleteItem.getState() == Thread.State.NEW)
                        deleteItem.start();
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



    Thread postItemData = new Thread( new Runnable() {

        @Override
        public void run() {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.backend_url)+"api/concept.json");


            // Create a new HttpClient and Post Header
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("title", newItemTitle.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("description", newItemDescription.getText().toString()));
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
