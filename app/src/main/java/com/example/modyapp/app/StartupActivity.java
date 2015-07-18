package com.example.modyapp.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.vk.sdk.*;
import com.vk.sdk.api.VKError;

public class StartupActivity extends Activity {

    /**
     * My application ID in vk.com
     */
    private final String VK_APP_ID = "4935615";
    /**
     * Token of current session for accessing VK API
     */
    private VKAccessToken token;
    /**
     * Scope of accessing parameters for my application
     */
    private final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.AUDIO
    };
    /**
     * Get access to VK API
     */
    private final VKSdkListener initializationVkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            Log.i("onCaptchaError", captchaError.errorMessage);
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.i("onTokenExpired","onTokenExpired");
        }

        @Override
        public void onAccessDenied(VKError authorizationError){
            Log.i("Access denied", authorizationError.errorMessage);
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            token = newToken;
            Log.i("onReceiveNewToken",newToken.accessToken);
            startListActivity();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.i("onAcceptUserToken",token.accessToken);
            startListActivity();
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        setContentView(R.layout.activity_startup);
        authVK();
    }

    private void authVK(){
        VKSdk.initialize(initializationVkListener, VK_APP_ID, token);
        VKSdk.wakeUpSession();
        VKSdk.authorize(sMyScope, false, false);
    //when try to reEnter don't receive new token...(((
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    private void startListActivity(){
        Intent slideActivity = new Intent(StartupActivity.this, ListActivity.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(this,
                        R.anim.main_activity, R.anim.secondary_activity)
                        .toBundle();
        startActivity(slideActivity, bundleAnimation);
    }

}