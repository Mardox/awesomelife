package com.mardox.awesomelife.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mardox.awesomelife.app.HomeActivity;
import com.mardox.awesomelife.app.ListActivity;
import com.mardox.awesomelife.app.SingleActivity;
import com.mardox.awesomelife.app.R;

import java.util.ArrayList;

/**
 * Created by HooMan on 28/03/2014.
 */
public class MenuFunctions {

    public static boolean newTip(Context context){

        Intent aboutIntent = new Intent(context , SingleActivity.class);
        context.startActivity(aboutIntent);
        return true;
    }

    public static boolean listTip(Context context){

        Intent aboutIntent = new Intent(context , ListActivity.class);
        context.startActivity(aboutIntent);
        return true;
    }

    public static boolean settings(Context context){

        final ArrayList mSelectedItems  = new ArrayList();  // Where we track the selected items
        boolean[] ticked = new boolean[10];
        final SharedPreferences prefs = context.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        //Check is daily video is turned off
        if(prefs.getBoolean("noDailyAlert", false)){ticked[0]=false;}else{ticked[0]=true;}
        //Create the upgrade dialog


        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.settings))
                .setMultiChoiceItems(R.array.settings, ticked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);

                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with upgrade
                        if(mSelectedItems.contains(0)){
                            editor.putBoolean("noDailyAlert", false);
                        }else{
                            editor.putBoolean("noDailyAlert", true);
                        }
                        editor.commit();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();


        return true;

    }

}

