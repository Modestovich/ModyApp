package com.example.modyapp.app.VKActions;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.TextView;
import com.example.modyapp.app.R;
import com.vk.sdk.api.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Created by Mody on 7/6/2015.
 */
public class VKActions {
    private static String lyricsText;
    private static Activity playerActivity;
    private static VKRequest.VKRequestListener requestLyrics = new VKRequest.VKRequestListener() {
        @Override
        public void onError(VKError error) {
            lyricsText = "";
            Log.i("Lyrics error", error.toString());
        }

        @Override
        public void onComplete(VKResponse response) {
            try {
                lyricsText = new JSONObject(response.responseString)
                        .getJSONObject("response").getString("text");
                ((TextView)playerActivity.findViewById(R.id.player_lyrics))
                        .setText(lyricsText);
            }catch(JSONException ex){
                Log.i("Json Text",response.responseString);
            }
        }
    };

    public static String  getLyrics(Integer lyrics_id,Activity act) {
        VKActions.playerActivity = act;
        VKApi.audio().getLyrics(
                VKParameters.from("lyrics_id", lyrics_id)).
                executeWithListener(requestLyrics);
        return lyricsText;
    }
    public static void getBackground(String searchQuery,final Activity act){
        VKActions.playerActivity = act;
        String template = "https://itunes.apple.com/search?term=";
        String responseString = "";
        String requestUrl = template + searchQuery;
        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(new URI(requestUrl));
            //request.setURI(new URI(requestUrl));
            response = client.execute(request);
        } catch (URISyntaxException e) {
            Log.i("URISyntaxException", "ex");
        } catch (ClientProtocolException e) {
            Log.i("ClientProtocolException", "ex");
        } catch (IOException e) {
            Log.i("IOException", "ex");
        } catch(NetworkOnMainThreadException ex ){
            Log.i("NetworkThreadException", "ex");
        }
        try {
            responseString = response != null ? EntityUtils.toString(response.getEntity()) : "";
        }catch(IOException ex){
            Log.i("IO","Can't get response string");
        }
        Drawable image = null;
        if(responseString.length()>0) {
            try {
                JSONObject responseJSON = new JSONObject(responseString);
                if((Integer)responseJSON.get("resultCount")!=0){
                    //at first get needed object -> then get needed parameter
                    String bgURL = (String)((JSONObject)responseJSON.getJSONArray("results").get(0))
                            .get("artworkUrl100");
                    if(bgURL.length()>0)
                        bgURL = bgURL.replace("100x100","600x600");
                    image = ImageFromUrl(bgURL);
                }
            }catch(JSONException ex){
                Log.i("json","Error while getting needed data from response");
            }
        }
        setBackground(image);
    }
    private static Drawable ImageFromUrl(String bgURL){
        InputStream is;
        try {
            is = (InputStream) new URL(bgURL).getContent();
            //Drawable image = new BitmapDrawable(is);
            return new BitmapDrawable(is);
        }catch(IOException ex){
            Log.i("IOException","Failed to open stream");
        }
        Log.i("Return null",":(((");
        return null;
    }

    private static void setBackground(final Drawable image){
        VKActions.playerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                (VKActions.playerActivity.findViewById(R.id.player_backgroundKeeper))
                        .setBackground(image);
            }
        });
    }
}
