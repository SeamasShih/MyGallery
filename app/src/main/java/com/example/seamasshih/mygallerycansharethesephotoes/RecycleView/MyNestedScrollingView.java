package com.example.seamasshih.mygallerycansharethesephotoes.RecycleView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyNestedScrollingView extends LinearLayout implements NestedScrollingParent {
    public MyNestedScrollingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
