package com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

public class MyToolbar extends Toolbar {
    public MyToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        animator = new ObjectAnimator();
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(animatorDuration);
        animator.setTarget(this);
        animator.setPropertyName("translationY");
    }


    protected long animatorDuration = 400;
    protected ObjectAnimator animator;


    protected void inVisibleAnimation(){
        animator.setFloatValues(0,getHeight());
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

    public void setVisible(boolean visible){
        if (visible) {
            visibleAnimation();
        }
        else {
            inVisibleAnimation();
        }
    }

    protected void visibleAnimation(){
        animator.setFloatValues(getHeight(),0);
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
