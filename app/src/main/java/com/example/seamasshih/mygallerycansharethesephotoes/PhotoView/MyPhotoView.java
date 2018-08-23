package com.example.seamasshih.mygallerycansharethesephotoes.PhotoView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
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
                Log.wtf("Seamas","x = " + translatingX);
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
    Matrix matrix = new Matrix();

    float scaling = 1;
    float translatingX = 0;
    float translatingY = 0;
    float scaleCenterX;
    float scaleCenterY;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initialScaleCenter();
    }


    private void initialScaleCenter(){
        scaleCenterX = getWidth()/2;
        scaleCenterY = getHeight()/2;
    }


    public boolean isBackingToEdge(){
        return backToEdge.isRunning();
    }


    public void setScaling(float scaling , float px , float py){
        this.scaling = scaling;

        Matrix invert = new Matrix();

        matrix.invert(invert);
        float[] floats = new float[]{px,py,1};
        invert.mapPoints(floats);
        translatingX = px - floats[0];
        translatingY = py - floats[1];
        scaleCenterX = floats[0];
        scaleCenterY = floats[1];
        invalidate();
    }

    public void setScaling(float scaling){
        this.scaling = scaling;
        invalidate();
    }

    public boolean isScaling(){
        return scaling != 1;
    }

    public float getScaling() {
        return scaling;
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

        float pl = getDrawable().getBounds().left + getWidth()/2 - getDrawable().getBounds().centerX();
        float pt = getDrawable().getBounds().top + getHeight()/2 - getDrawable().getBounds().centerY();
        float pr = getDrawable().getBounds().right + getWidth()/2 - getDrawable().getBounds().centerX();
        float pb = getDrawable().getBounds().bottom + getHeight()/2 - getDrawable().getBounds().centerY();

        float[] pointLT = new float[]{
                pl,
                pt,
                1
        };
        matrix.mapPoints(pointLT);

        float[] pointRB = new float[]{
                pr,
                pb,
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
            float left = getLeft();
            left += (scaling-1)*scaleCenterX;
            left -= scaling*pl;
            edgeX.setFloatValues(translatingX, left);
            isAnimation = true;
        }
        else if (r < getWidth() && (r-l) > getWidth()) {
            float right = getRight();
            right += (scaling-1)*scaleCenterX;
            right -= scaling*pr;
            edgeX.setFloatValues(translatingX, right);
            isAnimation = true;
        }
        if (t > 0 && (b-t) > getHeight()) {
            float top = getTop();
            top += (scaling-1)*scaleCenterY;
            top -= scaling*pt;
            edgeY.setFloatValues(translatingY, top);
            isAnimation = true;
        }
        else if (b < getHeight() && (b-t) > getHeight()) {
            float bottom = getBottom();
            bottom += (scaling-1)*scaleCenterY;
            bottom -= scaling*pb;
            edgeY.setFloatValues(translatingY, bottom);
            isAnimation = true;
        }

        if (isAnimation)
            backToEdge.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        matrix.setTranslate(translatingX,translatingY);
        matrix.preScale(scaling,scaling,scaleCenterX,scaleCenterY);
        canvas.concat(matrix);
        canvas.setMatrix(matrix);
        super.onDraw(canvas);
    }
}
