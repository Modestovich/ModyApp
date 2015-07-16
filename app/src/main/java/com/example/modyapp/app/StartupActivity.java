package com.example.modyapp.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.modyapp.app.VKActions.ConfigurationVK;
import com.vk.sdk.*;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        initAndAuthVk();
    }

    /**
     * Initialize application with existing/creating token and
     * application id
     */
    private void initAndAuthVk() {
        VKUIHelper.onCreate(this);
        ConfigurationVK.initVK();
        ConfigurationVK authVK = new ConfigurationVK(StartupActivity.this);
        authVK.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

}