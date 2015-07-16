package com.example.modyapp.app.VKActions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.example.modyapp.app.ListActivity;
import com.example.modyapp.app.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;

public class ConfigurationVK extends AsyncTask<Void,Void,Void> {
    /**
     * My application ID in vk.com
     */
    private static final String VK_APP_ID = "4935615";
    /**
     * Token of current session for accessing VK API
     */
    private static VKAccessToken token;
    /**
     * Scope of accessing parameters for my application
     */
    private static final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.AUDIO
    };
    private Activity parentActivity;
    /**
     * Get access to VK API
     */
    private static final VKSdkListener initializationVkListener = new VKSdkListener() {
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
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.i("onAcceptUserToken",token.accessToken);
        }

    };

    public ConfigurationVK(Activity parentActivity){
        this.parentActivity = parentActivity;
    }

    public static void initVK(){
        VKSdk.initialize(initializationVkListener, VK_APP_ID);
    }

    @Override
    protected Void doInBackground(Void... params) {
        authVK();
        return null;
    }

    private void authVK(){
        VKSdk.wakeUpSession();
        VKSdk.authorize(sMyScope, true, false);
        startListActivity();
        //String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
    }

    private void startListActivity(){
        Intent slideActivity = new Intent(parentActivity, ListActivity.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(parentActivity,
                        R.anim.main_activity, R.anim.secondary_activity)
                        .toBundle();
        parentActivity.startActivity(slideActivity, bundleAnimation);
    }

}

/**
 ********************************** Docs of method authorize *************************************************
 * Starts authorization process. If VKapp is available in system, it will opens and requests access from user.
 * Otherwise UIWebView with standard UINavigationBar will be opened for access request.
 *
 * @param scope      array of permissions for your applications. All permissions you can
 * @param revoke     if true, user will allow logout (to change user)
 * @param forceOAuth sdk will use only oauth authorization, through uiwebview
 */
