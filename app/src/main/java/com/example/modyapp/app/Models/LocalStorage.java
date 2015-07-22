package com.example.modyapp.app.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collection;

public class LocalStorage {
    public static final String VK_SONG_LIST = "vk_song_list";
    public static final String DOWNLOAD_SONG_LIST = "download_song_list";
    private static Collection container;
    private static SharedPreferences localStorage;
    private static SharedPreferences.Editor editor;
    public LocalStorage(Context context){
        //localStorage = PreferenceManager.getDefaultSharedPreferences(context);
        localStorage = context.getSharedPreferences(VK_SONG_LIST,0);
        editor = localStorage.edit();
    }
    public static void setValue(String key,Object value){
        editor.putString(key, value.toString());
        editor.apply();
    }
    public static String getValue(String key){
        return localStorage.getString(key,"");
    }

    /**
     * Temporary container to store data
     */
    public static void putToContainer(Object object){
        if(container==null)container = new ArrayList<Object>();
        container.add(object);
    }

    /**
     * @return pushed to container data and clearing it at once
     */
    public static Collection retrieveFromContainer(){
        Collection temp = container;
        container.clear();
        return temp;
    }

    public static void clearContainer(){
        container.clear();
    }

    public static void clear(){
        editor.remove(LocalStorage.VK_SONG_LIST);
        editor.apply();
    }
}