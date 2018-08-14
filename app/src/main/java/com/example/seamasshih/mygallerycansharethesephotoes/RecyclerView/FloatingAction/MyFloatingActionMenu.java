package com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.FloatingAction;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Label;

public class MyFloatingActionMenu extends FloatingActionMenu {
    public MyFloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = getChildAt(0);


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

        setMyMenuListener(view);
    }

    int HALF_WIDTH;
    int HALF_HEIGHT;
    private Resources resources = this.getResources();
    private DisplayMetrics dm = resources.getDisplayMetrics();
    private int screenWidth = dm.widthPixels;
    int margin = (int) (16 * dm.density);
    ValueAnimator animator;
    boolean drag = false;
    int mX;
    int mY;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        HALF_WIDTH = getWidth()/2;
        HALF_HEIGHT = getHeight()/2;
    }

    void setMyMenuListener(final View view){
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mX = (int) event.getRawX();
                        mY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        if (Math.sqrt((double)((mX - x)*(mX - x) + ((mY - y)*(mY - y)))) > 2 || drag) {
                            layout(x - HALF_WIDTH, y - HALF_HEIGHT, x + HALF_WIDTH, y + HALF_HEIGHT);
                            drag = true;
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (drag){
                            drag = false;
                            int x1 = (int) event.getRawX();
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
                return false;
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
