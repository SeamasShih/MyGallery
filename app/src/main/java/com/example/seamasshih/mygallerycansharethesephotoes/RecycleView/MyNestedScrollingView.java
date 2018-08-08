package com.example.seamasshih.mygallerycansharethesephotoes.RecycleView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public class MyNestedScrollingView extends LinearLayout implements NestedScrollingParent {

    RecyclerView recyclerView;
    View topView;
    View bottomView;
    int mDY = 0;
    ValueAnimator animator;
    boolean isAnimation = false;

    public MyNestedScrollingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        topView = new View(context);
        topView.setVisibility(INVISIBLE);
        bottomView = new View(context);
        bottomView.setVisibility(INVISIBLE);
        animator = new ValueAnimator();
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int r = (int)animation.getAnimatedValue();
                scrollTo(0,r);
                if (r ==0){
                    mDY = 0;
                    isAnimation = false;
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recyclerView =(RecyclerView) getChildAt(0);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (!child.canScrollVertically(-1) && !isAnimation);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!target.canScrollVertically(-1)) {
            mDY += dy;
            if (mDY > 0) {
                mDY = 0;
                scrollTo(0, 0);
                return;
            } else if (mDY < -150) {
                scrollTo(0, -150);
                mDY = -150;
                return;
            }
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
        isAnimation = true;
        animator.setIntValues(mDY,0);
        animator.start();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (!target.canScrollVertically(-1)) {
        }
        return false;
    }
}
