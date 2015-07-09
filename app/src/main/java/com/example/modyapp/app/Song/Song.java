package com.example.modyapp.app.Song;

import android.support.annotation.Nullable;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.model.VKApiAudio;

import java.util.ArrayList;

/**
 * Created by Mody on 31.05.2015.
 */
public class Song {
    private VKApiAudio song;
    //public Boolean play = false;
    public Song(VKApiAudio song){
        this.song = song;
    }

    public VKApiAudio getSong(){
        return this.song;
    }
    public String getName(){
        return this.song.title;
    }

    public String getDuration(){
        return transformDuration(this.song.duration);
    }
    private String getExtension(){
        String url = song.url;
        return url.substring(url.lastIndexOf("."),
                url.lastIndexOf("?")>-1?url.lastIndexOf("?"):url.length());
    }
    public String getFileNameToSave(){
        return song.artist+"-"+song.title+this.getExtension();
    }
    public String getNameToFilter(){
        return song.artist+" "+song.title+this.getExtension();
    }

    public static String transformDuration(Integer d){
        String duration = d/60 + ":";
        duration += (d%60>=10) ? d%60 : "0"+d%60;
        return duration;
    }
    public static String transformNameForBgSearch(VKApiAudio song){
        String searchValue = song.artist+" "+song.title;
        //searchValue = searchValue.replaceAll("([()]+)", "$1 ").trim()
        searchValue = searchValue.replaceAll("\\(.*?\\)", " ").trim()
                .replaceAll("\\s+","+");
        return searchValue;
    }

    @Override
    public boolean equals(Object song) {
        return (this.getSong().getId()==((Song)song).getSong().getId());
    }
}
