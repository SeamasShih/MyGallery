package com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.seamasshih.mygallerycansharethesephotoes.MainActivity;

public class MyNestedScrollingView extends RelativeLayout implements NestedScrollingParent {

    RecyclerView recyclerView;
    View topView;
    View bottomView;
    int mDY = 0;
    ValueAnimator animator;
    boolean isAnimation = false;
    int toolbarY;

    public MyNestedScrollingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        topView = new View(context);
        topView.setVisibility(INVISIBLE);
        bottomView = new View(context);
        bottomView.setVisibility(INVISIBLE);
        animator = new ValueAnimator();
        animator.setInterpolator(new DecelerateInterpolator());
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

    public boolean getIsAnimation(){
        return isAnimation;
    }

    public void setToolbarY(int toolbarY) {
        this.toolbarY = toolbarY;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recyclerView =(RecyclerView) getChildAt(0);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return ((!child.canScrollVertically(-1)||!child.canScrollVertically(1)) && !isAnimation);
    }

    public void onFlingAnimation(){
        isAnimation = true;
        animator.setIntValues(0, -toolbarY*2/3 , 0);
        animator.setDuration(400);
        animator.start();
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!target.canScrollVertically(-1)) {
            mDY += dy;
            if (mDY > 0) {
                mDY = 0;
                scrollTo(0, 0);
                return;
            } else if (mDY < -toolbarY) {
                scrollTo(0, -toolbarY);
                mDY = -toolbarY;
                return;
            }
            scrollBy(0, dy);
            consumed[1] = dy;
        }else if (!target.canScrollVertically(1)){
            mDY += dy;
            Log.d("Seamas","dy = " + dy);
            if (mDY < 0) {
                mDY = 0;
                scrollTo(0, 0);
                return;
            } else if (mDY > toolbarY) {
                scrollTo(0, toolbarY);
                mDY = toolbarY;
                return;
            }
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
        if (mDY < 0)
            upToNormalStatus();
        if (!((MainActivity)getContext()).isToolbarVisible() && mDY > 0) {
            downToNormalStatus();
        }
    }

    public void upToNormalStatus(){
        isAnimation = true;
        float rate = (float) -mDY / (float)toolbarY;
        long time = (long) (400*rate);
        animator.setIntValues(mDY, 0);
        animator.setDuration(time);
        animator.start();
    }

    public void downToNormalStatus(){
        isAnimation = true;
        float rate = (float) mDY / (float)toolbarY;
        long time = (long) (400*rate);
        animator.setIntValues(mDY, 0);
        animator.setDuration(time);
        animator.start();
    }
}
