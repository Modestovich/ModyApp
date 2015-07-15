package com.example.modyapp.app;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.modyapp.app.Song.DownloadMusic;
import com.example.modyapp.app.Song.Song;
import com.example.modyapp.app.Song.SongAdapter;
import com.vk.sdk.*;
import com.vk.sdk.api.*;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class ListActivity extends ActionBarActivity {

    /**
     * My application ID in vk.com
     */
    private final String VK_APP_ID = "4935615";
    /**
     * Token of current session for accessing VK API
     */
    private VKAccessToken token;

    private ListView songListView;
    private Button scrollToTop;
    private SongAdapter songsAdapter;
    private DownloadMusic downloadTask;
    private ProgressDialog mProgressDialog;
    private ArrayList<Song> listOfSongs;

    /**
     * Search needed songs from list by entering key-words
     */
    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(listOfSongs!=null && listOfSongs.size()>0) {
                songsAdapter.getFilter().filter(newText);
                songsAdapter.notifyDataSetChanged();
            }
            return true;
        }
    };

    /**
     * Starting/Resuming playerActivity and playing song
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Song clickedSong = (Song) songListView.getItemAtPosition(position);
            if(MusicPlayer.getCurrentSong()!=null &&
                    MusicPlayer.getCurrentSong().id != clickedSong.getId()) {
                MusicPlayer.startAfterClearingConditions(clickedSong,position);
            }
            else{
                MusicPlayer.Start(clickedSong,position);
            }
            Intent playerActivity = new Intent( getApplicationContext(), PlayerActivity.class);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.main_activity, R.anim.secondary_activity)
                            .toBundle();
            if(MusicPlayer.getCurrentSong()!=null){
                playerActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(playerActivity, bndlanimation);
            //downloadSong(clickedSong);
        }

        private void downloadSong(Song clickedSong) {
            downloadTask = new DownloadMusic(ListActivity.this,mProgressDialog);
            downloadTask.execute(clickedSong);
        }
    };

    /**
     * Scroll to top of list
     */
    private Button.OnClickListener scrollUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(songListView!=null){
                songListView.smoothScrollToPositionFromTop(0,0,0);
            }
        }
    };

    /**
     * Get access to VK API
     */
    private final VKSdkListener sdkListener = new VKSdkListener() {
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

    /**
     * Get songs from my profile from vk.com
     */
    private VKRequest.VKRequestListener vkListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            VkAudioArray songs = (VkAudioArray) response.parsedModel;
            listOfSongs = new ArrayList<>();
            for(VKApiAudio song: songs){
                listOfSongs.add(new Song(song));
            }
            songsAdapter = new SongAdapter(getApplicationContext(), listOfSongs);
            songListView.setAdapter(songsAdapter);
            MusicPlayer.populateMusicPlayer(listOfSongs);
        }

        @Override
        public void onError(VKError error) {
            Log.i("VKError",error.toString());
        }
    };

    /**
     * Animating button "Scroll to top" when top of list isn't visible
     */
    private AbsListView.OnScrollListener isScrollToTopAvailable = new AbsListView.OnScrollListener() {
        private ValueAnimator animator;
        float scale;
        private boolean mustBeVisible = false;


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            scale = getResources().getDisplayMetrics().density;
            if(firstVisibleItem!=0){
                if(!mustBeVisible) {
                    createAndMakeAnimation(0,25);
                    mustBeVisible = true;
                }
            }
            else {
                if(mustBeVisible) {
                    createAndMakeAnimation(25,0);
                    mustBeVisible = false;
                }
            }
        }
        private void createAndMakeAnimation(Integer from, Integer to){
            if(animator!=null)
                animator.end();
            animator = ValueAnimator.ofInt(from,to);
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    scrollToTop.getLayoutParams().height = (int) (value * scale);
                    scrollToTop.requestLayout();
                }
            });
            animator.start();
        }
    };

    /**
     * Scope of accessing parameters for my application
     */
    private static final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        synchronized (getApplicationContext()){
            initializeAndAuthorizeVk();
        }
        InitializeProgress();
       /* getFilesListFromDirectory(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString());*/
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
        Properties prop = new Properties();
        prop.getProperty("id");
        File[] files = (new File(folderPath)).listFiles();
        for(File file: files){
            InputStream fProp = null;
            try {
                fProp = new FileInputStream(file.getPath());

                prop.load(fProp);
                Log.i("FilesFromDirectory+Prm", file.getName() + ":" +
                        prop.getProperty("id"));
            }catch(IOException ex){
                Log.i("FileProperty",ex.toString());
            }finally {
                if(fProp!=null)
                    try{
                        fProp.close();
                    }catch(IOException ex){
                        Log.i("FileProperty",ex.getMessage());
                    }
            }
        }
    }

    private void InitializeProgress() {
        mProgressDialog = new ProgressDialog(ListActivity.this);
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

    /**
     * Populating music list with my audio from vk.com
     */
    private void populateMusicList() {
        songListView = (ListView) findViewById(R.id.songList);
        songListView.setOnItemClickListener(itemClickListener);
        songListView.setOnScrollListener(isScrollToTopAvailable);
        scrollToTop = (Button) findViewById(R.id.scroll_to_top);
        scrollToTop.setOnClickListener(scrollUp);
        ((SearchView) findViewById(R.id.searchMusic))
                .setOnQueryTextListener(searchListener);
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(vkListener);
    }


    /**
     * Initialize application with existing/creating token and
     * application id
     */
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


    /**
     * Checking if current playing element is in list of visible - make mark on it
     * and otherwise if previous is in current playing - put off mark from it
     *
     * OnPostResume instead of onResume because view can be not fully displayed
     * so fires NullPointerException
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(MusicPlayer.getCurrentSong()!=null) {
            Integer needle = MusicPlayer.getCurrentSong().id;
            Log.i("Current playing songId", needle + "");
            for (int i = songListView.getFirstVisiblePosition();
                 i <= songListView.getLastVisiblePosition(); i++) {
                Log.i("Song#" + i, ((Song) songListView.getAdapter().getItem(i)).getId() + "");
                try {
                    if (needle.equals(((Song) songListView.getAdapter().getItem(i)).getId()))
                        (songListView.getChildAt(i).
                                findViewById(R.id.player_playing)).
                                setVisibility(View.VISIBLE);
                    else {
                            (songListView.getChildAt(i).
                                    findViewById(R.id.player_playing)).
                                    setVisibility(View.INVISIBLE);
                    }
                } catch (NullPointerException ex) {
                    Log.e("NullPointerException", "View of song#"+i+" wasn't found");
                }
            }
        }
    }

    /**
     * Destroy current VK API session
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }
}