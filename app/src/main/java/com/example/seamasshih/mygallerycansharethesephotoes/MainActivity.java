package com.example.seamasshih.mygallerycansharethesephotoes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;

import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyAdapter;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyEdgeEffectFactory;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    MyAdapter adapter;
    GridLayoutManager manager;
    ScaleGestureDetector detector;

    final int READ_REQUESTCODE = 5566;
    boolean inSrollDown = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},READ_REQUESTCODE);

        manager = new GridLayoutManager(this,2, OrientationHelper.VERTICAL,false);
        adapter = new MyAdapter(this,getData());

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
                    manager.setSpanCount(factor > 1 ? Math.min(manager.getSpanCount() + 1, 6) : Math.max(manager.getSpanCount() - 1, 1));
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

        adapter.setOnItemListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Seamas","onItemClick");
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("Seamas","onItemLongClick");
            }
        });
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
