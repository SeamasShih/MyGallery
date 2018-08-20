package com.example.seamasshih.mygallerycansharethesephotoes;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.PhotoView.MyPhotoView;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    ScaleGestureDetector scaleGestureDetector;
    RelativeLayout relativeLayout;
    MyPhotoView photo;
    Toolbar toolbar;
    int photoOrder;
    int photoAmount;
    MyPhotoData data;
    String uri;
    String mimeType;
    float scale;
    ValueAnimator scaleAnimator;
    GestureDetector gestureDetector;
    boolean readyFinish = false;
    boolean scaling = false;
    boolean scrolling = false;
    ObjectAnimator photoAnimatorX;
    ObjectAnimator photoAnimatorY;
    AnimatorSet photoAnimatorSet;
    final long CANCEL_TIME = 800;
    final int WRITE_REQUESTCODE = 6655;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);
        setTheme(R.style.MyTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findMyView();

        setToolbar();

        setOnGestureDetector();

        initialPhotoAnimator();

    }

    private void findMyView() {
        toolbar = findViewById(R.id.toolbar);
        relativeLayout = findViewById(R.id.myPhotoLayout);
        photo = findViewById(R.id.myPhoto);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.alpha = 1f;
        window.setAttributes(windowParams);
    }


    void setToolbar(){
        Intent intent = getIntent();
        data = intent.getParcelableExtra("photoData");
        uri = data.getData();
        photoOrder = intent.getIntExtra("photoOrder",-1);
        photoAmount = intent.getIntExtra("photoAmount",-1);

        Glide.with(this)
                .load(uri)
                .into(photo);

        if (photoOrder != -1 && photoAmount != -1)
            toolbar.setTitle(photoOrder + "/" + photoAmount);

        toolbar.inflateMenu(R.menu.widget);
        toolbar.setVisibility(View.INVISIBLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new MyMenuItemClickListener(this));
    }




    @SuppressLint("ClickableViewAccessibility")
    void setOnGestureDetector(){
        gestureDetector = new GestureDetector(this , new MyGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());

        relativeLayout.setOnTouchListener(new View.OnTouchListener() { // It can't be set on image view and I don't know why

            float dX;
            float dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        dX = event.getX();
                        dY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (readyFinish){  // Move by finger
                            float x = event.getX();
                            float y = event.getY();
                            photo.setTranslationX(x-dX);
                            photo.setTranslationY(y-dY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (readyFinish)
                            doPhotoBackAnimation(0);
                        else if (scrolling) {
                            photo.animationToEdge();
                            if (photo.getScalingX() < .5f) onBackPressed();
                            else if (photo.getScalingX() < 1f) doPhotoBackAnimation(1);

                            scrolling = false;
                        }
                        break;
                }
                return false;
            }
        });

        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (scaling || scrolling) return false;
                photo.setBackgroundColor(Color.TRANSPARENT);
                doPhotoBackAnimation(.5f);
                readyFinish = true;
                photoAnimatorSet.start(); // Start photoRotationAnimation
                return false;
            }
        });
    }


    void doPhotoBackAnimation(float target){
        float now = photo.getScalingX();
        scaleAnimator.setFloatValues(now , target);
        scaleAnimator.start();
        photo.backToNormalSite();
    }


    void initialPhotoAnimator(){
        photoAnimatorX = ObjectAnimator.ofFloat(photo,"rotationX",0,7.07f,10,7.07f,0,-7.07f,-10,-7.07f,0).setDuration(CANCEL_TIME);
        photoAnimatorX.setRepeatCount(ValueAnimator.INFINITE);
        photoAnimatorY = ObjectAnimator.ofFloat(photo,"rotationY",10,7.07f,0,-7.07f,-10,-7.07f,0,7.07f,10).setDuration(CANCEL_TIME);
        photoAnimatorY.setRepeatCount(ValueAnimator.INFINITE);

        photoAnimatorSet = new AnimatorSet();
        photoAnimatorSet.setInterpolator(new LinearInterpolator());
        photoAnimatorSet.playTogether(photoAnimatorX,photoAnimatorY);

        scaleAnimator = new ValueAnimator();
        scaleAnimator.setDuration(500);
        scaleAnimator.setInterpolator(new DecelerateInterpolator());
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                photo.setScalingX(t);
                photo.setScalingY(t);
                if (t == 0)
                    finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        photo.setBackgroundColor(Color.TRANSPARENT);
        doPhotoBackAnimation(0);
    }

    class MyMenuItemClickListener implements Toolbar.OnMenuItemClickListener{

        Context context;

        public MyMenuItemClickListener(Context context){
            this.context = context;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.tool_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    mimeType = data.getMimeType();
                    File sharePath = new File(uri);
                    Uri mUri = FileProvider.getUriForFile(
                            context,BuildConfig.APPLICATION_ID + ".your_name",sharePath);
                    shareIntent.setType(mimeType);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,mUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent,"Share "+mimeType));
                    break;
                case R.id.tool_delete:
                    File file = new File(uri);
                    String[] strings = new String[]{file.getPath()};
                    context.getContentResolver().delete(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "=?", strings
                    );
                    photo.setBackgroundColor(Color.TRANSPARENT);
                    doPhotoBackAnimation(0);
                    finish();
                    break;
            }
            return true;
        }
    }

    class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (scaleAnimator.isRunning()) return false;
            scale = photo.getScalingX();
            scaling = true;
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = Math.abs(detector.getScaleFactor());

            photo.setScalingX(factor * scale);
            photo.setScalingY(factor * scale);

            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            float factor = Math.abs(detector.getScaleFactor());
            scaling = false;

            if (factor < .5f) onBackPressed();
            else if (factor < 1f) doPhotoBackAnimation(1);
        }
    }

    class MyGestureListener implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (toolbar.getVisibility() == View.INVISIBLE)
                toolbar.setVisibility(View.VISIBLE);
            else
                toolbar.setVisibility(View.INVISIBLE);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (photo.getScalingX() == 1 && !scaleAnimator.isRunning()) {
                doPhotoBackAnimation(2);
            }
            else if (photo.getScalingX() > 1 && !scaleAnimator.isRunning()) {
                doPhotoBackAnimation(1);
                photo.backToNormalSite();
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (photo.isScaling()) {
                photo.setAccumulateTranslatingX(-distanceX);
                photo.setAccumulateTranslatingY(-distanceY);
                scrolling = true;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

}
