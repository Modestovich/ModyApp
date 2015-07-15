package com.example.modyapp.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/28_Days_Later.ttf");
        ((TextView) findViewById(R.id.modyStartUp)).setTypeface(typeface);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}