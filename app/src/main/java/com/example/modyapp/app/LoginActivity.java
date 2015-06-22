package com.example.modyapp.app;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.modyapp.app.Song.DownloadMusic;
import com.example.modyapp.app.Song.Song;
import com.vk.sdk.*;
import com.vk.sdk.api.*;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.io.*;
import java.util.ArrayList;

public class LoginActivity extends ActionBarActivity {


    private final String VK_APP_ID = "4935615";
    private VKAccessToken token;
    private ListView songList;
    private ArrayAdapter<Song> songsAdapter;
   // private MediaPlayer player = new MediaPlayer();
    private DownloadMusic downloadTask;
    private ProgressDialog mProgressDialog;

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Song clickedSong = (Song) songList.getItemAtPosition(position);
            MusicPlayer.Start(clickedSong,position);
            Intent playerActivity = new Intent( getApplicationContext(), PlayerActivity.class);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.main_activity, R.anim.secondary_activity)
                            .toBundle();
            /*if(PlayerActivity.getCreation()){
                PlayerActivity.Create();
            }else {
                playerActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //playerActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            }*/
            startActivity(playerActivity, bndlanimation);
            //downloadSong(clickedSong);
        }

        private void downloadSong(Song clickedSong) {
            downloadTask = new DownloadMusic(LoginActivity.this,mProgressDialog);
            downloadTask.execute(clickedSong);
        }
    };
    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            Toast.makeText(getApplicationContext(), "Captcha error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.i("onTokenExpired","onTokenExpired");
            //Toast.makeText(getApplicationContext(), "Token expired", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAccessDenied(VKError authorizationError){
            Log.i("Access denied", authorizationError.errorMessage);
            //Toast.makeText(getApplicationContext(), "Access denied", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            token = newToken;
            Log.i("onReceiveNewToken",newToken.accessToken);
         //  loginText.setText(newToken.accessToken);
         //   Toast.makeText(getApplicationContext(), newToken.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //loginText.setText(token.accessToken);
            //Toast.makeText(getApplicationContext(), token.accessToken, Toast.LENGTH_SHORT).show();
            Log.i("onAcceptUserToken",token.accessToken);
        }
    };
    private VKRequest.VKRequestListener vkListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            VkAudioArray songs = (VkAudioArray) response.parsedModel;
            final ArrayList<Song> listOfSongs = new ArrayList<Song>();
            for(VKApiAudio song: songs){
                listOfSongs.add(new Song(song));
            }
            songsAdapter = new ArrayAdapter<Song>(getApplicationContext(),
                    R.layout.list_view_music,listOfSongs){

                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    View view = convertView;
                    if(view==null){
                        view = getLayoutInflater().inflate(R.layout.list_view_music,
                                parent,false);
                    }

                    Song current_song = listOfSongs.get(position);
                    TextView name = (TextView) view.findViewById(R.id.song_name);
                    name.setText(current_song.getName());
                    TextView duration = (TextView) view.findViewById(R.id.song_duration);
                    duration.setText(current_song.getDuration());
                    return view;
                }
            };
            songList.setAdapter(songsAdapter);
            MusicPlayer.populateMusicPlayer(listOfSongs);
        }

        @Override
        public void onError(VKError error) {
            Log.i("VKError",error.toString());
            //Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
        }
    };
    private static final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            //VKScope.NOHTTPS,
            VKScope.AUDIO
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        synchronized (getApplicationContext()) {
            initializeAndAuthorizeVk();
        }
        InitializeProgress();

        getFilesListFromDirectory(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString());

        synchronized (getApplicationContext()) {
            populateMusicList();
        }
    }

    private void deleteFilesFromDirectory(String folderPath){
        File[] files = (new File(folderPath)).listFiles();
        for(File file: files){
            file.delete();
        }
    }

    private void getFilesListFromDirectory(String folderPath) {
        /*Properties prop = new Properties();
        prop.getProperty("id");
        File[] files = (new File(folderPath)).listFiles();
        for(File file: files){
            InputStream fProp = null;
            try {
                fProp = new FileInputStream(file.getPath());

                prop.load(fProp);
                Log.i("FilesFromDirectory+Param", file.getName() + ":" +
                        prop.getProperty("id"));
            }catch(FileNotFoundException ex){
                Log.i("FileProperty",ex.getMessage());
            }catch(IOException ex){
                Log.i("FileProperty",ex.getMessage());
            }finally {
                if(fProp!=null)
                    try{
                        fProp.close();
                    }catch(IOException ex){
                        Log.i("FileProperty",ex.getMessage());
                    }
            }
        }*/
    }

    private void InitializeProgress() {
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setMessage("Downloading ...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private void populateMusicList() {
        songList = (ListView) findViewById(R.id.songList);
        songList.setOnItemClickListener(itemClickListener);
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(vkListener);
    }

    private void initializeAndAuthorizeVk() {
        VKUIHelper.onCreate(this);

        VKSdk.initialize(sdkListener, VK_APP_ID);
        /*if(!VKSdk.wakeUpSession()){
            VKSdk.authorize(sMyScope, true, false);
        }
        else {
            VKSdk.authorize(sMyScope, false, false);
        }*/
        VKSdk.wakeUpSession();
        VKSdk.authorize(sMyScope, false, false);
        //VKSdk.authorize(sMyScope, true, false);

        //String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        //player.stop();
    }
}