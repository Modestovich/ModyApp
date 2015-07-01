package com.example.modyapp.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.modyapp.app.Song.Song;
import com.vk.sdk.api.*;
import com.vk.sdk.api.model.VKApiAudio;


public class PlayerActivity extends Activity {

    private SeekBar barSeeking;
    private TextView textSeeking;
    private TextView lyricsText;
    private UpdateTimeBar updateScreen;
    private View.OnTouchListener mouseEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_UP:
                    MusicPlayer.MouseUp();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        UpdateTime();//Infinity listening for updating time of song
        (findViewById(R.id.player_main)).setOnTouchListener(mouseEvent);
    }

    private void UpdateState(){
        VKApiAudio song = MusicPlayer.getCurrentSong();
        ((TextView) findViewById(R.id.player_artist)).setText(song.artist);
        ((TextView) findViewById(R.id.player_title)).setText(song.title);
        ((TextView) findViewById(R.id.player_song_number))
                .setText((MusicPlayer.getCurrentPosition()+1)+" of "
                        + MusicPlayer.getListLength());
        ((TextView) findViewById(R.id.player_to_finish))
                .setText(Song.transformDuration(song.duration));
        ((SeekBar) findViewById(R.id.player_song_progressBar))
                .setMax(song.duration);
    }

    private void UpdateTime(){
        lyricsText = (TextView) findViewById(R.id.player_lyrics);
        barSeeking = (SeekBar) findViewById(R.id.player_song_progressBar);
        textSeeking = (TextView) findViewById(R.id.player_progress);
        updateScreen = new UpdateTimeBar();
        updateScreen.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateScreen.cancel(true);
        if(!MusicPlayer.isPlaying())
            MusicPlayer.clearCondition();
    }

    private class UpdateTimeBar extends AsyncTask<Void, Integer, Void> {
        private Integer songId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            barSeeking.setProgress(0);
            barSeeking.setMax(MusicPlayer.
                    getCurrentSong().duration);
            songId = MusicPlayer.getCurrentSong().id;
            while(true){
               //if(max>barSeeking.getProgress()){
               if(songId==MusicPlayer.getCurrentSong().id){
                   try {
                       Thread.sleep(1000);
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }
                   try{
                       if(!MusicPlayer.isStopSeeking())
                           publishProgress(MusicPlayer.getSeeking());
                   }catch(IllegalStateException ex){
                       Log.i("Ill state", "Ill state");
                   }
               }else{
                   publishProgress();
               }
                if(isCancelled())
                    break;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values.length>0) {
                barSeeking.setProgress(values[0] / 1000);
                textSeeking.setText(Song.transformDuration(values[0] / 1000));
            }else {
                songId = MusicPlayer.getCurrentSong().id;
                UpdateState();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}