package com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        picture = new Picture();

        recording();
        initialAnimator();
    }

    private void initialAnimator() {
        animator = ObjectAnimator.ofFloat(this,"rotation",0,4,5,4,0,-4,-5,-4,0);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

    private void recording() {
        final int WIDTH = 500;
        final int HEIGHT = 500;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        Paint white = new Paint();
        white.setAntiAlias(true);
        white.setColor(Color.WHITE);
        white.setStrokeWidth(50);
        white.setStyle(Paint.Style.STROKE);
        white.setStrokeCap(Paint.Cap.ROUND);
        white.setStrokeJoin(Paint.Join.ROUND);


        Path check = new Path();
        check.moveTo(-100,0);
        check.lineTo(0,100);
        check.lineTo(125,-75);

        Canvas canvas = picture.beginRecording(WIDTH,HEIGHT);

        canvas.translate(WIDTH/2,HEIGHT/2);
        canvas.drawCircle(0,0,WIDTH/2,paint);
        canvas.drawPath(check,white);

        picture.endRecording();
    }

    boolean picked = false;
    Picture picture;
    ObjectAnimator animator;

    public boolean isPicked(){
        return picked;
    }

    public void setPicked(boolean picked){
        this.picked = picked;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (picked){
            if (!animator.isRunning())
                animator.start();
            int width = getWidth();
            int height = getHeight();
            @SuppressLint("DrawAllocation") Rect rect = new Rect(width*7/9,0,width,height*2/9);

            canvas.drawARGB(180,255,255,255);

            canvas.drawPicture(picture,rect);
        }
        else
            animator.end();
    }
}
