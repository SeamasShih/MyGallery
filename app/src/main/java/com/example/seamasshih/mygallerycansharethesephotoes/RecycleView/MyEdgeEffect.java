package com.example.seamasshih.mygallerycansharethesephotoes.RecycleView;


import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.EdgeEffect;

public class MyEdgeEffect extends EdgeEffect {
    /**
     * Construct a new EdgeEffect with a theme appropriate for the provided context.
     *
     * @param context Context used to provide theming and resource information for the EdgeEffect
     */
    public MyEdgeEffect(Context context) {
        super(context);
    }

    @Override
    public void onPull(float deltaDistance, float displacement) {
    }

    @Override
    public void onAbsorb(int velocity) {

    }

    @Override
    public void onRelease() {

    }
}
