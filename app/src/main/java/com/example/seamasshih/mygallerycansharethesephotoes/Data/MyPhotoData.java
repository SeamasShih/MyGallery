package com.example.seamasshih.mygallerycansharethesephotoes.Data;

import android.database.Cursor;

public class MyPhotoData {
    public MyPhotoData(Cursor cursor){
        data = cursor.getString(0);
        displayName = cursor.getString(1);
        width = cursor.getString(2);
        height = cursor.getString(3);
    }

    private String data;
    private String displayName;
    private String width;
    private String height;

    public String getData(){
        return data;
    }
    public String getDisplayName(){
        return displayName;
    }
    public String getWidth(){
        return width;
    }
    public String getHeight(){
        return height;
    }
}
