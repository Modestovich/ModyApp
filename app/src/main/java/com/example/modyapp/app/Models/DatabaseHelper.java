package com.example.modyapp.app.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final Integer db_version = 1;
    private static String DATABASE_NAME = "Music_player.db";
    private static String CONFIG_TABLE_NAME = "player_config";
    private static String CONFIG_APP_ID_ROW_NAME = "application_id";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+CONFIG_TABLE_NAME+" ( "+CONFIG_APP_ID_ROW_NAME+" text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+CONFIG_TABLE_NAME);
        onCreate(db);
    }

    public long InsertValues(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONFIG_APP_ID_ROW_NAME,"4935615");
        return db.insert(CONFIG_TABLE_NAME, null, values);
    }

    public String GetApplicationId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * from "+CONFIG_TABLE_NAME,null);
        StringBuilder response = new StringBuilder();
        if(result.getCount()>0){
            while(result.moveToNext()){
                response.append(result.getString(result.getColumnIndex(CONFIG_APP_ID_ROW_NAME)));
            }
        }
        result.close();
        return response.toString();
    }
}