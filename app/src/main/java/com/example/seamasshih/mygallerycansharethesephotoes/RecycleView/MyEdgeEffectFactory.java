package com.example.seamasshih.mygallerycansharethesephotoes.RecycleView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.EdgeEffect;

public class MyEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {

    @NonNull
    @Override
    protected EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
        return new MyEdgeEffect(view.getContext());
    }
}
