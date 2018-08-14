package com.example.seamasshih.mygallerycansharethesephotoes.Data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class MyPhotoData implements Parcelable{
    public MyPhotoData(Cursor cursor){
        data = cursor.getString(0);
        displayName = cursor.getString(1);
        mimeType = cursor.getString(2);
        width = cursor.getString(3);
        height = cursor.getString(4);
    }

    private String data;
    private String displayName;
    private String mimeType;
    private String width;
    private String height;


    protected MyPhotoData(Parcel in) {
        data = in.readString();
        displayName = in.readString();
        mimeType = in.readString();
        width = in.readString();
        height = in.readString();
    }

    public static final Creator<MyPhotoData> CREATOR = new Creator<MyPhotoData>() {
        @Override
        public MyPhotoData createFromParcel(Parcel in) {
            return new MyPhotoData(in);
        }

        @Override
        public MyPhotoData[] newArray(int size) {
            return new MyPhotoData[size];
        }
    };

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

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(displayName);
        dest.writeString(mimeType);
        dest.writeString(width);
        dest.writeString(height);
    }
}
