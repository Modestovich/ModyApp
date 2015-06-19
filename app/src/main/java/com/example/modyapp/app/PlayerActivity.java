package com.example.modyapp.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Iterator;
import java.util.List;


public class PlayerActivity extends ActionBarActivity {

    private static boolean created = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        MusicPlayer.setView(getWindow().
                getDecorView().getRootView());// findViewById(R.layout.fragment_player_header));
    }

    public static boolean getCreation(){
        return created;
    }
    public static void Create(){
        created = true;
    }
}
