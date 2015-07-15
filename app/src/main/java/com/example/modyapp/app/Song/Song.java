package com.example.modyapp.app.Song;

import com.vk.sdk.api.model.VKApiAudio;

public class Song {

    private VKApiAudio song;
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

    /**
     * Transforming duration to the UI displaying
     * 64 sec = 1:04
     * @param d - current time to be transformed into UI view
     * @return - transformed duration
     */
    public static String transformDuration(Integer d){
        String duration = d/60 + ":";
        duration += (d%60>=10) ? d%60 : "0"+d%60;
        return duration;
    }

    /**
     * Make value for search background in player
     * @param song - song to be searched
     * @return - search query for get request to find background image
     */
    public static String transformNameForBgSearch(VKApiAudio song){
        String searchValue = song.artist+" "+song.title;
        searchValue = searchValue.replaceAll("\\(.*?\\)", " ").trim()
                .replaceAll("\\s+","+");
        return searchValue;
    }

    public Integer getId(){
        return this.song.id;
    }

    /**
     * Compare two songs by id.
     * @param song - comparable object
     * @return :
     *      - true - if songs have same ids
     *      - false - if songs have different ids
     */
    @Override
    public boolean equals(Object song) {
        return (song!=null && this.getSong().getId()==((Song)song).getSong().getId());
    }
}
