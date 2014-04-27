package com.mardox.awesomelife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends Activity {

    ArrayList<HashMap<String, String>> itemsList = new ArrayList<HashMap<String,String>>();
    GridView tipsList;
    ProgressBar prgLoading;
    ListAdapter adapter;
    ProgressBar progBar;

    View rootView;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        setTitle("Your Tips");

        //Log.i(HomeActivity.TAG, "List activity: " + HomeActivity.userID);
        progBar = (ProgressBar) findViewById(R.id.prgLoading);
        adapter= new ListViewAdapter(this, itemsList, this);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        tipsList = (GridView) findViewById(R.id.tips_list);
        tipsList.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();

        itemsList.clear();
        progBar.setVisibility(View.VISIBLE);
        String API_URL = getString(R.string.backend_url)+"api/concepts?format=json&uid="+HomeActivity.userID;
        new GetTips().execute(API_URL);

    }




    private class GetTips extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... apiURL) {
            //start building result which will be json string
            StringBuilder apiFeedBuilder = new StringBuilder();
            //should only be one URL, receives array
            for (String searchURL : apiURL) {
                HttpClient apiClient = new DefaultHttpClient();
                try {
                    //pass search URL string to fetch
                    HttpGet apiGet = new HttpGet(searchURL);
                    //execute request
                    HttpResponse apiResponse = apiClient.execute(apiGet);
                    //check status, only proceed if ok
                    StatusLine searchStatus = apiResponse.getStatusLine();
                    //Log.e("myApp", "I am here ");
                    if (searchStatus.getStatusCode() == 200) {
                        //get the response
                        HttpEntity apiEntity = apiResponse.getEntity();
                        InputStream entityContent = apiEntity.getContent();
                        //process the results
                        InputStreamReader readerInput = new InputStreamReader(entityContent);
                        BufferedReader tweetReader = new BufferedReader(readerInput);
                        String lineIn;
                        while ((lineIn = tweetReader.readLine()) != null) {
                            apiFeedBuilder.append(lineIn);
                        }
                    }
                    else
                        Log.e("myApp","Whoops - something went wrong with status code!"
                                + searchStatus.getStatusCode());
                }
                catch(Exception e){
                    Log.e("myApp", "Whoops - something went wrong with httpObject!");
                    // e.printStackTrace();
                }
            }
            //return result string
            return apiFeedBuilder.toString();
        }


        protected void onPostExecute(String result) {
            //start preparing result string for display
                try {
                    //get JSONObject from result
//                    JSONObject JSONResult = new JSONObject(result);
                    JSONArray JSONResult = new JSONArray(result);

                    //loop through each item in the tweet array
                    for (int t = 0; t < JSONResult.length(); t++) {

                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject item;
                        item = JSONResult.getJSONObject(t);
                        String itemID = item.getString("id");
                        String title = item.getString("title");
                        String description = item.getString("description");

                        // adding each child node to HashMap key =&gt; value

                        map.put(HomeActivity.KEY_TITLE, title);
                        map.put(HomeActivity.KEY_DESCRIPTION, description);
                        map.put(HomeActivity.KEY_ID, itemID);
                        Log.i(HomeActivity.TAG,"Data:" + title);
                        itemsList.add(map);

                    }

                } catch (Exception e) {
                    Log.e(HomeActivity.TAG, "Whoops:" + e.toString());
                    noResultErrorDialog();
                    e.printStackTrace();
                }

                //inflating the list view
                //check result exists
                if (!itemsList.isEmpty()) {

                    prgLoading.setVisibility(View.GONE);
                    tipsList.invalidateViews();
                    tipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        HashMap<String, String> item;

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            item = itemsList.get(position);
                            String item_id = item.get("id");
                            String item_title = item.get("title");
                            String item_description = item.get("description");
                            Intent editActivity = new Intent(context, NewActivity.class);
                            editActivity.putExtra("id", item_id);
                            editActivity.putExtra("title", item_title);
                            editActivity.putExtra("description", item_description);
                            startActivity(editActivity);
                        }

                    });

                } else {
                    prgLoading.setVisibility(View.GONE);
                }


        }

    }



    /**
     * Network connection error dialog
     */
    private void networkErrorDialog() {

        //Create the upgrade dialog
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(R.string.no_internet_message)
                .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // reset the request

                    }
                })
                .setIcon(R.drawable.ic_action_dark_error)
                .show();
    }



    /**
     * Network connection error dialog
     */
    private void noResultErrorDialog() {

         //Create the upgrade dialog
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(R.string.no_search_results)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // reset the request

                    }
                })
                .setIcon(R.drawable.ic_action_dark_error)
                .show();
    }






}
