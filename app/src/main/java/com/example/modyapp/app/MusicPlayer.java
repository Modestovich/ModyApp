package com.example.modyapp.app;

import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
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
    public static Song currentSong;
    private static Integer currentPosition;//position of current song in list
    private static View musicPlayerView;
    private static boolean isPlaying;

    private static MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            isPlaying = true;
        }
    };

    private MusicPlayer(){
    }

    public static void Start(Song song, Integer position){
        if(currentSong==null || !song.equals(currentSong)){
            isPlaying = false;
            if (player != null) {
                player.release();
                player = null;
            }
            player = new MediaPlayer();
            player.setOnPreparedListener(preparedListener);
            try {
                player.setDataSource(song.getSong().url);
            } catch (IOException ex) {
                Log.i("Invalid url", ex.getMessage());
            }
            try {
                player.prepareAsync();
            } catch (IllegalStateException e) {
                Log.i("Incorrect data source", "Error");
            }
            updateSongNumber(song);
            updateHeader(song);
            currentSong = song;
            currentPosition = position;
        }
    }
    public static void Pause(){
        length = player.getCurrentPosition();
        player.pause();
    }
    public static void Continue(){
        player.seekTo(length);
        player.start();
    }
    public static void Prev() {
        if(currentPosition>0) {
            currentPosition--;
            Start(songs.get(currentPosition), currentPosition);
        }
    }
    public static void Next() {
        if(currentPosition<songs.size()-1) {
            currentPosition++;
            Start(songs.get(currentPosition), currentPosition);
        }
    }

    public static void populateMusicPlayer(ArrayList<Song> songs){
        MusicPlayer.songs = songs;
    }

    public static void setView(View view) {
        musicPlayerView = view;
    }
    private static void updateHeader(Song song) {
        if((currentSong==null || !song.equals(currentSong)) && musicPlayerView!=null){
            ((TextView) musicPlayerView.findViewById(R.id.player_artist)).setText(song.getSong().artist);
            ((TextView) musicPlayerView.findViewById(R.id.player_title)).setText(song.getSong().title);
        }
    }
    private static void updateSongNumber(Song song){
        if((currentSong==null || !song.equals(currentSong)) && musicPlayerView!=null){
            ((TextView) musicPlayerView.findViewById(R.id.player_song_number))
                    .setText((currentPosition+1)+" of "
                            + MusicPlayer.getListLength());
        }
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
    public static boolean isPlaying(){
        return isPlaying;
    }
}
