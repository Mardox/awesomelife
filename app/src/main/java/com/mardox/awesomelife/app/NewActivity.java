package com.mardox.awesomelife.app;

import android.app.Activity;
import android.os.Bundle;

public class NewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        setTitle("Add a New Tip");

    }
}
