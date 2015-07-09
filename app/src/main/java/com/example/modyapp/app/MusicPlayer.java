package com.example.modyapp.app;

import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.modyapp.app.Song.Song;
import com.vk.sdk.api.model.VKApiAudio;

import java.io.IOException;
import java.util.*;

/**
 * Created by Mody on 03.06.2015.
 */
public final class MusicPlayer {

    public enum Repeat{
        REPEAT_NOREPEAT(0), REPEAT_ALL(1) ,REPEAT_SINGLE(2);
        private Integer value;
        private static Integer currentValue = 0;
        private static Map<Integer, Repeat> map = new HashMap<Integer, Repeat>();

        static {
            for (Repeat repeat : Repeat.values()) {
                map.put(repeat.value, repeat);
            }
        }
        Repeat(final int value) { this.value = value; }

        public static void setNextRepeat(){
            if(currentValue==0 || currentValue==1) currentValue++;
            else currentValue = 0;
        }
        public static void setRepeat(Integer flag){
            currentValue = flag;
        }

        public static Integer getIntValue(){
            return currentValue;
        }
        public static Repeat getValue(Integer repeatInt){
            switch (repeatInt) {
                case 1:
                    return REPEAT_ALL;
                case 2:
                    return REPEAT_SINGLE;
                default:return REPEAT_NOREPEAT;
            }
        }
        public static Repeat getValue(){
            switch (currentValue) {
                case 1:
                    return REPEAT_ALL;
                case 2:
                    return REPEAT_SINGLE;
                default:return REPEAT_NOREPEAT;
            }
        }
    }


    private static MediaPlayer player = null;
    private static ArrayList<Song> songs;
    private static int length;//milliseconds
    private static Song currentSong;
    private static Integer currentPosition;//position of current song in list
    private static boolean isPlaying;
    private static boolean stopSeeking;
    private static boolean randomFlag = false;
    private static ArrayList<Integer> sequence = new ArrayList<Integer>();
    private static Integer positionInList=0;

    private static Random random = new Random();

    private static MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            isPlaying = true;
            stopSeeking = false;
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
        if(Repeat.getValue()==Repeat.REPEAT_SINGLE ||
                (songs.size()==1 && getRepeat()!=Repeat.REPEAT_NOREPEAT )) {
            length = 0;
            player.seekTo(length);
            Start(songs.get(currentPosition), currentPosition);
            return;
        }
        if(randomFlag && songs.size()>1){
            try{
                if(positionInList!=0){
                    currentPosition = sequence.get(--positionInList);
                }
                Start(songs.get(currentPosition), currentPosition);
                isPlaying = true;
            }catch(ConcurrentModificationException ex){
                Log.i("ConcurrentEx","Switch while processing current");
            }
        }
        else if(currentPosition>0) {
            currentPosition--;
            Start(songs.get(currentPosition), currentPosition);
            isPlaying = true;
        }
    }
    public static void Next() {
        if(Repeat.getValue()==Repeat.REPEAT_SINGLE ||
                (songs.size()==1 && getRepeat()!=Repeat.REPEAT_NOREPEAT )) {
            length = 0;
            player.seekTo(length);
            player.start();
            return;
        }
        if(randomFlag && songs.size()>1){
            try {
                if(positionInList==sequence.size()-1 || sequence.size()==0){
                    currentPosition = getRandom();
                    sequence.add(currentPosition);
                    positionInList++;
                }
                else {
                    currentPosition = sequence.get(++positionInList);
                }
                isPlaying = true;
                Start(songs.get(currentPosition), currentPosition);
            }catch(ConcurrentModificationException ex){
                Log.i("ConcurrentEx","Switch while processing current");
            }
        }else if(currentPosition<songs.size()-1) {
            currentPosition++;
            Start(songs.get(currentPosition), currentPosition);
            isPlaying = true;
        }
    }
    private static Integer getRandom(){
        Integer next = random.nextInt(songs.size());
        if(Repeat.getValue()==Repeat.REPEAT_NOREPEAT) {
            while(sequence.contains(next)){
                next = random.nextInt(songs.size());
            }
        }
        return next;
    }

    public static void populateMusicPlayer(ArrayList<Song> songs){
        MusicPlayer.songs = songs;
    }
    public static VKApiAudio getCurrentSong(){
        return currentSong==null? null : currentSong.getSong();
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
            Log.i("Seek is null",ex.getMessage()+"!");
        }
        return 0;
    }
    public static void clearCondition(){
        currentSong = null;
        length = 0;
    }
    public static void clearSequence(Song song, Integer position){
        clearCondition();
        sequence.clear();
        positionInList = 0;
        if(getRandomState()) sequence.add(position);
        Start(song,position);
        isPlaying = true;
    }

    //seeking position of song
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

    //setting/getting repeat
    public static void setRepeat(){
        Repeat.setNextRepeat();
    }
    /*public static void setRepeat(Integer repeat) { Repeat.setRepeat(repeat); }*/
    public static Repeat getRepeat(){
        return Repeat.getValue();
    }
/*    public static Integer getIntRepeat(){
        return Repeat.getIntValue();
    }*/
    //setting random
    public static boolean getRandomState(){
        return randomFlag;
    }
    public static boolean setRandom(){
        if(randomFlag){
            randomFlag = false;
            sequence.clear();
        }else {
            randomFlag = true;
            positionInList = 0;
            if(currentSong!=null)
                sequence.add(currentPosition);
        }
        return randomFlag;
    }
  /*  public static void setRandom(boolean flag){
        randomFlag = flag;
        if(flag && sequence.size()==0) sequence.add(currentPosition);
    }*/

}