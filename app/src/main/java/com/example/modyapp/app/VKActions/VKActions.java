package com.example.modyapp.app.VKActions;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.TextView;
import com.example.modyapp.app.Player.MusicPlayer;
import com.example.modyapp.app.R;
import com.example.modyapp.app.Song.Song;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
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
     * @param sourceActivity - activity where textView should be changed
     *            after finishing request processing
     */
    public static void getLyrics(Integer lyrics_id,Activity sourceActivity) {
        VKActions.playerActivity = sourceActivity;
        VKApi.audio().getLyrics(
                VKParameters.from("lyrics_id", lyrics_id)).
                executeWithListener(requestLyrics);
    }

    /**
     * Set background of player depending on current playing song
     * If response is empty show standard background
     * @param searchQuery - search query for get request
     *                    for source of images for background
     * @param sourceActivity - activity to be changed after successful
     *            receiving response
     */
    public static void setBackground(final Activity sourceActivity){
        VKActions.playerActivity = sourceActivity;
        String template = "https://itunes.apple.com/search?term=";
        String searchQuery = Song.transformNameForBgSearch(
                MusicPlayer.getCurrentSong());
        String responseString = "";
        String requestUrl = template + searchQuery + "&limit=1";
        HttpResponse response = requestToItunesToGetBg(requestUrl);
        try {
            responseString = response != null ? EntityUtils.toString(response.getEntity()) : "";
        }catch(IOException ex){
            Log.i("IO","Can't get response string");
        }
        Drawable image = getBackgroundImageDrawable(responseString);
        setBackgroundInView(image);
    }

    private static HttpResponse requestToItunesToGetBg(String requestUrl){
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(new URI(requestUrl));
            return client.execute(request);
        } catch (URISyntaxException e) {
            Log.i("URISyntaxException", "ex");
        } catch (ClientProtocolException e) {
            Log.i("ClientProtocolException", "ex");
        } catch (IOException e) {
            Log.i("IOException", "ex");
        } catch(NetworkOnMainThreadException ex ){
            Log.i("NetworkThreadException", "This has to be done in mainThread");
        }
        return null;
    }

    private static Drawable getBackgroundImageDrawable(String responseString){
        try {
            JSONObject responseJSON = new JSONObject(responseString);
            if ((Integer) responseJSON.get("resultCount") != 0) {
                String bgURL = getBackgroundImageURL(responseJSON);
                return getImageFromUrl(bgURL);
            }
        }catch (JSONException ex){
            Log.i("JSONEx","Error while making JSON object from response");
        }
        return null;
    }

    private static String getBackgroundImageURL(JSONObject responseJSON){
        try {
            String bgURL =
            (String)((JSONObject)responseJSON.getJSONArray("results").get(0))
                    .get("artworkUrl100");
            if(bgURL.length()>0)
                bgURL = bgURL.replace("100x100","600x600");
            return bgURL;
        }catch(JSONException ex){
            Log.i("json","Error while getting needed data from response");
        }
        return "";
    }

    /**
     * Get object(DrawableImage) for background if response isn't empty
     * otherwise return null
     * @param bgURL - URL of image to be background
     * @return - object(DrawableImage) for background
     */
    private static Drawable getImageFromUrl(String bgURL){
        InputStream is;
        try {
            is = (InputStream) new URL(bgURL).getContent();
            return new BitmapDrawable(playerActivity.getResources(),is);
        }catch(IOException ex){
            Log.i("IOException","Failed to open stream");
        }
        return null;
    }

    /**
     * Setting background for player
     * @param image - object (DrawableImage) to be background for player
     */
    private static void setBackgroundInView(final Drawable image){
        VKActions.playerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                (VKActions.playerActivity.findViewById(R.id.player_backgroundKeeper))
                        .setBackground(image);
            }
        });
    }
}
