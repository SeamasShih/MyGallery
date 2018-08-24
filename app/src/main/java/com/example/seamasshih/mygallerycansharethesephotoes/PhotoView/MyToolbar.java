package com.example.seamasshih.mygallerycansharethesephotoes.PhotoView;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class MyToolbar extends com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyToolbar {
    public MyToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        animatorDuration = 250;
        animator.setDuration(animatorDuration);
    }


    protected void inVisibleAnimation(){
        animator.setFloatValues(0,-getHeight());
        animator.removeAllListeners();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.pause();
        animator.start();
    }


    protected void visibleAnimation(){
        animator.setFloatValues(-getHeight(),0);
        animator.removeAllListeners();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.pause();
        animator.start();
    }
}
