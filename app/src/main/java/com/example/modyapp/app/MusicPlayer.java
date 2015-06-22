package com.example.modyapp.app;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.modyapp.app.Song.Song;
import com.vk.sdk.api.model.VKApiAudio;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mody on 03.06.2015.
 */
public final class MusicPlayer {

    private static MediaPlayer player = null;
    private static ArrayList<Song> songs;
    private static int length;//milliseconds
    private static Song currentSong;
    private static Integer currentPosition;//position of current song in list
    private static boolean isPlaying;
    private static boolean stopSeeking;
    private static boolean seekTo;

    private static MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            isPlaying = true;
            stopSeeking = false;
            seekTo = false;
        }
    };
    private static MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Next();
        }
    };


    private MusicPlayer(){
    }

    public static void Start(Song song, Integer position){
        if(currentSong==null || !song.equals(currentSong)){
            Log.i("New song",song.getName());
            if(currentSong!=null)Log.i("Current song",currentSong.getName());

            if (player != null) {
                player.release();
                player = null;
                length = 0;
            }
            player = new MediaPlayer();
            player.setOnPreparedListener(preparedListener);
            player.setOnCompletionListener(completionListener);
            try {
                player.setDataSource(song.getSong().url);
            } catch (IOException ex) {
                Log.i("Invalid url", "Invalid url");
            }
            try {
                player.prepareAsync();
            } catch (IllegalStateException e) {
                Log.i("Incorrect data source", "Error");
            }
            currentSong = song;
            currentPosition = position;
        }
    }

    public static void Pause(){
        player.pause();
        length = player.getCurrentPosition();
        isPlaying = false;
    }
    public static void Continue(){
        player.seekTo(length);
        player.start();
        isPlaying = true;
    }
    public static void Prev() {
        if(currentPosition>0) {
            currentPosition--;
            Start(songs.get(currentPosition), currentPosition);
            isPlaying = true;
        }
    }
    public static void Next() {
        if(currentPosition<songs.size()-1) {
            currentPosition++;
            Start(songs.get(currentPosition), currentPosition);
            isPlaying = true;
        }
    }

    public static void populateMusicPlayer(ArrayList<Song> songs){
        MusicPlayer.songs = songs;
    }
    public static VKApiAudio getCurrentSong(){
        return currentSong.getSong();
    }
    public static Integer getCurrentPosition(){
        return currentPosition;
    }
    public static Integer getListLength(){
        return songs.size();
    }
    public static String getCurrentSongDuration(){
        Integer durationInt = getCurrentSong().duration;
        String duration = durationInt/60 + ":";
        duration += (durationInt%60>10) ? durationInt%60 : "0"+durationInt%60;
        return duration;
    }
    public static boolean isPlaying(){
        return isPlaying;
    }
    public static Integer getSeeking(){
        try {
            return player.getCurrentPosition();
        }catch(NullPointerException ex){
            Log.i("Seek is null",ex.getMessage());
        }
        return 0;
    }
    public static void clearCondition(){
        currentSong = null;
        length = 0;
    }

    public static void MouseMove(){
        stopSeeking = true;
    }
    public static void MouseUp(Integer progress){
        stopSeeking = false;
        player.seekTo(progress);
        length = progress;
    }

    public static void MouseUp(){
        stopSeeking = true;
    }

    public static boolean isStopSeeking(){
        return stopSeeking;
    }
}