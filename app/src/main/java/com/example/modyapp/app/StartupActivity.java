package com.example.modyapp.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import com.example.modyapp.app.Fragments.StartUpProgress;


import static com.example.modyapp.app.Fragments.StartUpProgress.*;

/**
 * Created by Mody on 26.05.2015.
 */
public class StartupActivity extends Activity {

    private TextView modyStartUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        modyStartUp = (TextView) findViewById(R.id.modyStartUp);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/28_Days_Later.ttf");
        modyStartUp.setTypeface(typeface);
        //new StartUpProgress.ProgressingBar().execute();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}