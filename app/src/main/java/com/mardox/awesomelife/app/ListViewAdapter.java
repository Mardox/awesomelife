package com.mardox.awesomelife.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HooMan on 12/08/13.
 */
public class ListViewAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public ListViewAdapter(Activity a, ArrayList<HashMap<String, String>> d, Context context) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = inflater.inflate(R.layout.tip_list_row, null);
        assert vi != null;
        TextView title = (TextView)vi.findViewById(R.id.row_item_title); // title
        TextView description = (TextView)vi.findViewById(R.id.row_item_description); // title


        HashMap<String, String> video;
        video = data.get(position);

        title.setText(video.get(HomeActivity.KEY_TITLE));
        description.setText(video.get(HomeActivity.KEY_DESCRIPTION));
        return vi;
    }



}



