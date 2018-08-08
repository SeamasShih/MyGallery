package com.example.seamasshih.mygallerycansharethesephotoes;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.RecycleView.MyAdapter;
import com.example.seamasshih.mygallerycansharethesephotoes.RecycleView.MyEdgeEffect;
import com.example.seamasshih.mygallerycansharethesephotoes.RecycleView.MyEdgeEffectFactory;
import com.example.seamasshih.mygallerycansharethesephotoes.RecycleView.MyItemDecoration;
import com.example.seamasshih.mygallerycansharethesephotoes.RecycleView.MyOnFlingListener;
import com.example.seamasshih.mygallerycansharethesephotoes.RecycleView.MyOnScrollListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    MyAdapter adapter;
    GridLayoutManager manager;
    GestureDetectorCompat gestureDetector;

    final int READ_REQUESTCODE = 5566;
    boolean inSrollDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},READ_REQUESTCODE);

//        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
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


//        int height = getMaxHeight();
//        lp = (LinearLayout.LayoutParams) view.getLayoutParams();
//        lp.setMargins(0,(int)(deltaDistance*height),0,0);
//        view.setLayoutParams(lp);

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
