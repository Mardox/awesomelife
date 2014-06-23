package com.mardox.awesomelife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.mardox.awesomelife.app.utils.MenuFunctions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends Activity {

    ArrayList<HashMap<String, String>> itemsList = new ArrayList<HashMap<String,String>>();
    GridView tipsList;
    ProgressBar prgLoading;
    ListAdapter adapter;
    ProgressBar progBar;

    ParseUser user;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Your Tips");

        progBar = (ProgressBar) findViewById(R.id.prgLoading);
        adapter= new ListViewAdapter(this, itemsList, this);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        tipsList = (GridView) findViewById(R.id.tips_list);
        tipsList.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();

        itemsList.clear();
        progBar.setVisibility(View.VISIBLE);


        user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserTip");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e != null) {
                    // There was an error
                } else {
                    // results have all the Posts the current user liked.

                    if(results.size()>0) {

                        //loop through each item in the tweet array
                        for (int t = 0; t < results.size(); t++) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            ParseObject item;
                            item = results.get(t);
                            String itemID = item.getObjectId();
                            String title = item.getString("title");
                            String description = item.getString("description");

                            // adding each child node to HashMap key =&gt; value

                            map.put(HomeActivity.KEY_TITLE, title);
                            map.put(HomeActivity.KEY_DESCRIPTION, description);
                            map.put(HomeActivity.KEY_ID, itemID);
                            Log.i(HomeActivity.TAG,"Data:" + title);
                            itemsList.add(map);

                        }

                    }

                    //inflating the list view
                    //check result exists
                    if (!itemsList.isEmpty()) {
                        tipsList.invalidateViews();
                        tipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            HashMap<String, String> item;

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                item = itemsList.get(position);
                                String item_id = item.get("id");
                                String item_title = item.get("title");
                                String item_description = item.get("description");
                                Intent editActivity = new Intent(context, SingleActivity.class);
                                editActivity.putExtra("id", item_id);
                                editActivity.putExtra("title", item_title);
                                editActivity.putExtra("description", item_description);
                                startActivity(editActivity);
                            }

                        });

                    }

                    prgLoading.setVisibility(View.GONE);


                }
            }
        });


    }


    //Update the list view on change in the single item view
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode) {
//            case (MY_CHILD_ACTIVITY) : {
//                if (resultCode == Activity.RESULT_OK) {
//                    // TODO Extract the data returned from the child Activity.
//                }
//                break;
//            }
//        }
//    }


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_action_add:
                MenuFunctions.newTip(context);
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
