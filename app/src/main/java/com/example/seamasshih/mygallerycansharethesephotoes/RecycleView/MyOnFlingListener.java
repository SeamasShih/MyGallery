package com.example.seamasshih.mygallerycansharethesephotoes.RecycleView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MyOnFlingListener extends RecyclerView.OnFlingListener {
    @Override
    public boolean onFling(int velocityX, int velocityY) {
        Log.d("Seamas","onFling");
        return false;
    }
}
