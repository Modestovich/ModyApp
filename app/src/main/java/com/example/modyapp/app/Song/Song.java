package com.example.modyapp.app.Song;

import com.vk.sdk.api.model.VKApiAudio;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class Song{

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
    public String getTransformedDuration(){
        return transformDuration(this.song.duration);
    }
    private String getExtension(){
        String url = song.url;
        return url.substring(url.lastIndexOf("."),
                url.lastIndexOf("?")>-1?url.lastIndexOf("?"):url.length());
    }
    public VKApiAudio getAudio(){
        return this.song;
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
     * @param duration - current time to be transformed into UI view
     * @return - transformed duration
     */
    public static String transformDuration(Integer duration){
        String convertedDuration = duration/60 + ":";//calculate number of minutes
        convertedDuration += (duration%60>=10) ? duration%60 : "0"+duration%60;
        return convertedDuration;
    }

    /**
     * Make value for search background in player
     * @param song - song to be searched
     * @return - search query for get request to find background image
     */
    public static String transformNameForBgSearch(Song song){
        String searchValue = song.getArtist()+" "+song.getTitle();
        searchValue = searchValue.replaceAll("\\(.*?\\)", " ").trim()
                .replaceAll("\\s+","+");
        return searchValue;
    }

    public Integer getId(){
        return this.song.id;
    }
    public String getArtist(){
        return this.song.artist;
    }
    public String getTitle(){
        return this.song.title;
    }
    public String getURL(){
        return this.song.url;
    }
    public Integer getLyricsId(){
        return this.song.lyrics_id;
    }
    public Integer getDuration(){
        return this.song.duration;
    }
    public boolean hasLyrics(){
        return getLyricsId()>0;
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
        return (song!=null && this.getId().equals(((Song)song).getId()));
    }

    public static JSONObject collectionToJSON(Collection<Song> songs){
        JSONObject json = new JSONObject();
        try {
            /*json.put("id", this.getId());
            json.put("artist", this.getArtist());
            json.put("title", this.getTitle());
            json.put("url", this.getURL());
            json.put("duration", this.getDuration());
            json.put("lyrics_id", this.getLyricsId());
            if(extraData.length>0){
                json.put("lyrics",extraData[0]);
                json.put("image",extraData[1]);
            }*/
            json.put("listOfSongs",songs);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return json;
    }

    //public static Collection<Song> jsonToCollection(JSONObject json){
    public static String jsonToCollection(JSONObject json){
        Collection<Song> songsList = new ArrayList<Song>();
        String data = "";
        try{
            JSONArray jArray = json.getJSONArray("listOfSongs");
            for(int i=0;i>jArray.length();i++){
                //data+="\n"+jArray.get(i).toString();
                return jArray.get(i).toString();
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return data;
        //return songsList;
    }

}