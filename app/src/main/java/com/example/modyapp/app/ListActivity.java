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
import com.example.modyapp.app.Player.MusicPlayer;
import com.example.modyapp.app.Song.DownloadMusic;
import com.example.modyapp.app.Song.Song;
import com.example.modyapp.app.Song.SongAdapter;
import com.vk.sdk.*;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class ListActivity extends ActionBarActivity {

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
                    MusicPlayer.getCurrentSong().getId().equals(clickedSong.getId())) {
                MusicPlayer.startAfterClearingConditions(clickedSong,position);
            }
            else{
                MusicPlayer.Start(clickedSong,position);
            }
            Intent playerActivity = new Intent( getApplicationContext(), PlayerActivity.class);
            Bundle bundleAnimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.main_activity, R.anim.secondary_activity)
                            .toBundle();
            if(MusicPlayer.getCurrentSong()!=null){
                playerActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(playerActivity, bundleAnimation);
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
        private ValueAnimator animator;
        private float scale;

        @Override
        public void onClick(View v) {
            if(songListView!=null){
                songListView.smoothScrollToPositionFromTop(0,0,0);
                hideScrollButtonWhileUp();
            }
        }
        private void hideScrollButtonWhileUp(){
            scale = getResources().getDisplayMetrics().density;
            animator = ValueAnimator.ofInt(scrollToTop.getHeight(),0);
            animator.setDuration(200);
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
                    createAndMakeAnimation(scrollToTop.getHeight(),0);
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
     * Get songs from my profile from vk.com
     */
    private VKRequest.VKRequestListener populateList = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            VkAudioArray songs = (VkAudioArray) response.parsedModel;
            listOfSongs = new ArrayList<Song>();
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

    public void logOut(View view){
        VKSdk.logout();
        Intent startUpActivity = new Intent( getApplicationContext(), StartupActivity.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(),
                        R.anim.main_activity, R.anim.secondary_activity)
                        .toBundle();
        startActivity(startUpActivity, bundleAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        songListView = (ListView) findViewById(R.id.songList);
        songListView.setOnItemClickListener(itemClickListener);
        songListView.setOnScrollListener(isScrollToTopAvailable);
        scrollToTop = (Button) findViewById(R.id.scroll_to_top);
        scrollToTop.setOnClickListener(scrollUp);
        ((SearchView) findViewById(R.id.searchMusic))
                .setOnQueryTextListener(searchListener);
        populateMusicList();
        //InitializeProgress();
       /* getFilesListFromDirectory(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString());*/
    }

    /**
     * Populating music list with my audio from vk.com
     */
    private void populateMusicList(){
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(populateList);
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
     * Checking if current playing element is in list of visible - make mark on it
     * and otherwise if previous is in current playing - put off mark from it
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(MusicPlayer.getCurrentSong()!=null) {
            Integer needle = MusicPlayer.getCurrentSong().getId();
            Integer startPoint = songListView.getFirstVisiblePosition();
            Integer endPoint = songListView.getLastVisiblePosition();
            for (int i = startPoint;i<= endPoint; i++) {
                try {
                    if (needle.equals(((Song) songListView.getAdapter().getItem(i)).getId()))
                        (songListView.getChildAt(i-startPoint).
                                findViewById(R.id.player_playing)).
                                setVisibility(View.VISIBLE);
                    else {
                            (songListView.getChildAt(i-startPoint).
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