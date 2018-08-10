package com.example.seamasshih.mygallerycansharethesephotoes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyAdapter;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyEdgeEffectFactory;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyImageView;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    MyAdapter adapter;
    GridLayoutManager manager;
    ScaleGestureDetector detector;
    SharedPreferences sharedPreferences;
    final String SHARED_PREFERENCE_NAME = "mSharePreference";
    final String SHARED_PREFERENCE_SPAN_COUNT = "mSpanCount";

    final int READ_REQUESTCODE = 5566;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},READ_REQUESTCODE);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        final int mSpanCount = sharedPreferences.getInt(SHARED_PREFERENCE_SPAN_COUNT,3);

        manager = new GridLayoutManager(this,mSpanCount, OrientationHelper.VERTICAL,false);
        adapter = new MyAdapter(this);

        recyclerView = findViewById(R.id.recyclerView);
        // 设置布局管理器
        recyclerView.setLayoutManager(manager);
        // 设置adapter
        recyclerView.setAdapter(adapter);

        recyclerView.setEdgeEffectFactory(new MyEdgeEffectFactory());

        recyclerView.addItemDecoration(new MyItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.addItemDecoration(new MyItemDecoration(this, LinearLayout.HORIZONTAL));


        detector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            boolean canScale = true;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                if (canScale && factor != 1) {
                    manager.setSpanCount(factor < 1 ? Math.min(manager.getSpanCount() + 1, 6) : Math.max(manager.getSpanCount() - 1, 1));
                    canScale = false;
                }
                return super.onScale(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                canScale = true;
                super.onScaleEnd(detector);
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    detector.onTouchEvent(event);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        adapter.updateData(getData());
        super.onResume();
    }

    @Override
    protected void onStop() {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFERENCE_SPAN_COUNT,manager.getSpanCount());
        editor.apply();
        super.onStop();
    }


    @Override
    protected void onStart() {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller;
        switch (manager.getSpanCount()){
            case 1:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_1);
                break;
            case 2:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_2);
                break;
            case 3:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_3);
                break;
            case 4:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_4);
                break;
            case 5:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_5);
                break;
            case 6:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_6);
                break;
            default:
                controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down_6);
                break;
        }
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_REQUESTCODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    finish();
        }
    }

    ArrayList<MyPhotoData> getData(){
        ArrayList<MyPhotoData> data = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projections = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        };
        String order = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        try {
            Cursor imageCursor = getContentResolver().query(
                    uri,
                    projections,
                    null,
                    null,
                    order
            );
            if (imageCursor != null) {
                while (imageCursor.moveToNext()) {
                    data.add(new MyPhotoData(imageCursor));
                }
                imageCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
