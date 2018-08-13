package com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class MyFloatingActionButton extends FloatingActionButton {
    public MyFloatingActionButton(Context context,AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.rgb(240,240,240));
        paint.setAntiAlias(true);

        animator = new ValueAnimator();
        animator.setDuration(700);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int r = (int)animation.getAnimatedValue();
                layout(r-HALF_WIDTH,getTop(),r+HALF_WIDTH,getBottom());
            }
        });
    }

    int HALF_WIDTH;
    int HALF_HEIGHT;
    private Resources resources = this.getResources();
    private DisplayMetrics dm = resources.getDisplayMetrics();
    private int screenWidth = dm.widthPixels;
    int margin = (int) (16 * dm.density);
    ValueAnimator animator;
    Paint paint;
    boolean drag = false;
    int mX;
    int mY;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        HALF_WIDTH = getWidth()/2;
        HALF_HEIGHT = getHeight()/2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mX = (int) ev.getRawX();
                mY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();
                if (Math.sqrt((double)((mX - x)*(mX - x) + ((mY - y)*(mY - y)))) > 2 || drag) {
                    layout(x - HALF_WIDTH, y - HALF_HEIGHT, x + HALF_WIDTH, y + HALF_HEIGHT);
                    drag = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (drag){
                    drag = false;
                    int x1 = (int) ev.getRawX();
                    if (x1 < screenWidth/2){
                        animator.setIntValues(x1,HALF_WIDTH+margin);
                        animator.start();
                    }
                    else {
                        animator.setIntValues(x1,screenWidth-HALF_WIDTH-margin);
                        animator.start();
                    }
                    return true;
                }
                break;
            default:
                if (drag){
                    drag = false;
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int WIDTH = getWidth();
        int HEIGHT = getHeight();
        canvas.drawCircle(WIDTH/4,HEIGHT/2,WIDTH/13,paint);
        canvas.drawCircle(WIDTH/2,HEIGHT/2,WIDTH/13,paint);
        canvas.drawCircle(WIDTH*3/4,HEIGHT/2,WIDTH/13,paint);
    }
}
