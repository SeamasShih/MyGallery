package com.example.seamasshih.mygallerycansharethesephotoes.PhotoView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class MyPhotoView extends android.support.v7.widget.AppCompatImageView {
    public MyPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialAnimation();
    }

    private void initialAnimation() {
        backX = new ValueAnimator();
        backX.setInterpolator(new LinearInterpolator());
        backX.setDuration(500);
        backX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                setTranslatingX(t);
                postInvalidate();
            }
        });

        backY = new ValueAnimator();
        backY.setInterpolator(new LinearInterpolator());
        backY.setDuration(500);
        backY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                setTranslatingY(t);
                postInvalidate();
            }
        });

        backToNormal = new AnimatorSet();
        backToNormal.playTogether(backX,backY);


        edgeX = new ValueAnimator();
        edgeX.setInterpolator(new DecelerateInterpolator());
        edgeX.setDuration(500);
        edgeX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                setTranslatingX(t);
                translatingX = t;
                postInvalidate();
            }
        });

        edgeY = new ValueAnimator();
        edgeY.setInterpolator(new DecelerateInterpolator());
        edgeY.setDuration(500);
        edgeY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                setTranslatingY(t);
                translatingY = t;
                postInvalidate();
            }
        });

        backToEdge = new AnimatorSet();
        backToEdge.playTogether(edgeX,edgeY);

    }

    AnimatorSet backToNormal;
    ValueAnimator backX;
    ValueAnimator backY;
    AnimatorSet backToEdge;
    ValueAnimator edgeX;
    ValueAnimator edgeY;

    float scalingX = 1;
    float scalingY = 1;
    float translatingX = 0;
    float translatingY = 0;

    public void setScalingX(float scalingX){
        this.scalingX = scalingX;
        invalidate();
    }

    public boolean isScaling(){
        return scalingX != 1;
    }

    public float getScalingX() {
        return scalingX;
    }

    public void setScalingY(float scalingY){
        this.scalingY = scalingY;
        invalidate();
    }


    public void setTranslatingX(float translatingX){
        this.translatingX = translatingX;
        invalidate();
    }

    public void setAccumulateTranslatingX(float translatingX){
        this.translatingX += translatingX;
        invalidate();
    }


    public void setTranslatingY(float translatingY){
        this.translatingY = translatingY;
        invalidate();
    }

    public void setAccumulateTranslatingY(float translatingY){
        this.translatingY += translatingY;
        invalidate();
    }

    public float getTranslatingX(){
        return translatingX;
    }

    public float getTranslatingY(){
        return translatingY;
    }

    public boolean isTranslating(){
        return translatingX != 0 || translatingY != 0;
    }

    public void backToNormalSite(){
        backX.setFloatValues(translatingX,0);
        backY.setFloatValues(translatingY,0);
        backToNormal.start();
    }

    public void animationToEdge(){
        if(!isTranslating()) return;

        Matrix matrix = new Matrix();
        matrix.postScale(scalingX,scalingY,getWidth()/2,getHeight()/2);
        matrix.postTranslate(translatingX,translatingY);

        float[] pointLT = new float[]{
                getDrawable().getBounds().left + getWidth()/2 - getDrawable().getBounds().centerX(),
                getDrawable().getBounds().top + getHeight()/2 - getDrawable().getBounds().centerY() ,
                1
        };
        matrix.mapPoints(pointLT);

        float[] pointRB = new float[]{
                getDrawable().getBounds().right + getWidth()/2 - getDrawable().getBounds().centerX(),
                getDrawable().getBounds().bottom + getHeight()/2 - getDrawable().getBounds().centerY() ,
                1
        };
        matrix.mapPoints(pointRB);

        float l = (int)pointLT[0];
        float t = (int)pointLT[1];
        float r = (int)pointRB[0];
        float b = (int)pointRB[1];
        edgeX.setFloatValues(translatingX,translatingX);
        edgeY.setFloatValues(translatingY,translatingY);
        boolean isAnimation = false;
        if (l > 0 && (r-l) > getWidth()) {
            edgeX.setFloatValues(translatingX, translatingX - l);
            isAnimation = true;
        }
        else if (r < getWidth() && (r-l) > getWidth()) {
            edgeX.setFloatValues(translatingX, translatingX + getWidth() - r);
            isAnimation = true;
        }
        if (t > 0 && (b-t) > getHeight()) {
            edgeY.setFloatValues(translatingY, translatingY - t);
            isAnimation = true;
        }
        else if (b < getHeight() && (b-t) > getHeight()) {
            edgeY.setFloatValues(translatingY, translatingY + getHeight() - b);
            isAnimation = true;
        }

        if (isAnimation)
            backToEdge.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(translatingX,translatingY);
        canvas.scale(scalingX,scalingY,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
