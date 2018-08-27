package com.example.seamasshih.mygallerycansharethesephotoes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.PhotoView.MyDrawable;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyAdapter;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyEdgeEffectFactory;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyItemDecoration;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyNestedScrollingView;
import com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView.MyToolbar;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    MyToolbar toolbar;
    RecyclerView recyclerView;
    MyNestedScrollingView scrollingView;
    MyAdapter adapter;
    GridLayoutManager manager;
    ScaleGestureDetector detector;
    GestureDetector gestureDetector;
    SharedPreferences sharedPreferences;
    MyContentObserver contentObserver;
    final String SHARED_PREFERENCE_NAME = "mSharePreference";
    final String SHARED_PREFERENCE_SPAN_COUNT = "mSpanCount";
    boolean isFling = false;

    final int READ_REQUESTCODE = 5566;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.MyTheme);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_REQUESTCODE);

        findMyView();

        setToolbar();

        setRecyclerView();

        setListener();

        registerContentObserver();
    }

    private void registerContentObserver() {
        contentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,contentObserver);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        scrollingView.setToolbarY(toolbar.getHeight());
    }

    void findMyView(){
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.tool);
        scrollingView = findViewById(R.id.scrollingLayout);
    }


    private void setToolbar() {
        toolbar.inflateMenu(R.menu.widget);
        toolbar.setVisibility(View.INVISIBLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setNavigationIcon(MyDrawable.createCloseDownIcon());
        toolbar.setOnMenuItemClickListener(new MyMenuItemClickListener(this));
    }

    public boolean isToolbarVisible(){
        return toolbar.isShown();
    }

    public void showToolbar(boolean show){
        if (!(show && toolbar.getVisibility() == View.VISIBLE))
            toolbar.setVisible(show);
        if (!show){
            scrollingView.onStopNestedScroll(recyclerView);
        }
    }


    void setRecyclerView(){
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        final int mSpanCount = sharedPreferences.getInt(SHARED_PREFERENCE_SPAN_COUNT,3);

        manager = new GridLayoutManager(this,mSpanCount, OrientationHelper.VERTICAL,false);
        adapter = new MyAdapter(this,getData());

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        recyclerView.setEdgeEffectFactory(new MyEdgeEffectFactory());

        recyclerView.addItemDecoration(new MyItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.addItemDecoration(new MyItemDecoration(this, LinearLayout.HORIZONTAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(-1)
                        && !scrollingView.getIsAnimation() && dy < 0 && isFling){
                    scrollingView.onFlingAnimation();
                    isFling = false;
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }



    @SuppressLint("ClickableViewAccessibility")
    void setListener(){
        detector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            boolean canScale = true;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                if (canScale && factor != 1) {
                    if (factor < 1 && manager.getSpanCount() != 6) {
                        manager.setSpanCount(manager.getSpanCount() + 1);
                        adapter.notifyItemChanged(0); // It can perform an animation but I don't know why...
                    }
                    else if (factor > 1 && manager.getSpanCount() != 1){
                        manager.setSpanCount(manager.getSpanCount() - 1);
                        adapter.notifyItemChanged(0); // It can perform an animation but I don't know why...
                    }
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

        gestureDetector = new GestureDetector(this, new MyGestureDetectorListener());

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    detector.onTouchEvent(event);
                    return true;
                }
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (adapter.getPickCount() == 0)
            super.onBackPressed();
        else {
            adapter.cancelImagePick();
            showToolbar(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFERENCE_SPAN_COUNT,manager.getSpanCount());
        editor.apply();
    }


    @Override
    protected void onStart() {
        super.onStart();
        showToolbar(false);
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_REQUESTCODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    finish();
                else
                    recreate();
        }
    }

    ArrayList<MyPhotoData> getData(){
        ArrayList<MyPhotoData> data = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projections = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
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


    class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            isFling = true;
            return super.onFling(e1, e2, velocityX, velocityY);
        }
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
                    Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    ArrayList<MyPhotoData> shareData = adapter.getPhotoData();
                    ArrayList<Integer> sharePick = adapter.getPick();
                    ArrayList<Uri> sendUriList = new ArrayList<>();

                    File sharePath;
                    for (int p : sharePick){
                        sharePath = new File(shareData.get(p).getData());
                        Uri uri = FileProvider.getUriForFile(
                                context,BuildConfig.APPLICATION_ID + ".your_name",sharePath);
                        sendUriList.add(uri);
                    }

                    String mimeType = shareData.get(0).getMimeType();
                    shareIntent.setType(mimeType);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,sendUriList);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent,"Share "+mimeType));
                    break;
                case R.id.tool_delete:
                    ArrayList<MyPhotoData> deleteData = adapter.getPhotoData();
                    ArrayList<Integer> deletePick = adapter.getPick();

                    for (int p : deletePick){
                        File file = new File(deleteData.get(p).getData());
                        String[] strings =new String[]{file.getPath()};
                        context.getContentResolver().delete(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "=?", strings
                        );
                    }

                    showToolbar(false);
                    break;
            }
            return true;
        }
    }

    class MyContentObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            adapter.updateData(getData());
        }
    }

}
