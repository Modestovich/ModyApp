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

    /**
     * Get lyrics of current playing song
     * @param lyrics_id - id of lyrics of current song
     * @param act - activity where textView should be changed
     *            after finishing request processing
     */
    public static void getLyrics(Integer lyrics_id,Activity act) {
        VKActions.playerActivity = act;
        VKApi.audio().getLyrics(
                VKParameters.from("lyrics_id", lyrics_id)).
                executeWithListener(requestLyrics);
    }

    /**
     * Set background of player depending on current playing song
     * If response is empty show standard background
     * @param searchQuery - search query for get request
     *                    for source of images for background
     * @param act - activity to be changed after successful
     *            receiving response
     */
    public static void getBackground(String searchQuery,final Activity act){
        VKActions.playerActivity = act;
        String template = "https://itunes.apple.com/search?term=";
        String responseString = "";
        String requestUrl = template + searchQuery + "&limit=1";
        HttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(new URI(requestUrl));
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
        Log.i("Time of response","Total elapsed http request/response time in milliseconds: " +
                (System.currentTimeMillis() - startTime));
    }

    /**
     * Get object(DrawableImage) for background if response isn't empty
     * otherwise return null
     * @param bgURL - URL of image to be background
     * @return - object(DrawableImage) for background
     */
    private static Drawable ImageFromUrl(String bgURL){
        InputStream is;
        try {
            is = (InputStream) new URL(bgURL).getContent();
            return new BitmapDrawable(is);
        }catch(IOException ex){
            Log.i("IOException","Failed to open stream");
        }
        return null;
    }

    /**
     * Setting background for player
     * @param image - object (DrawableImage) to be background for player
     */
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
