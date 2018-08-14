package com.example.seamasshih.mygallerycansharethesephotoes.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.PhotoActivity;
import com.example.seamasshih.mygallerycansharethesephotoes.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<MyPhotoData> mData;
    private Context context;
    private ArrayList<Integer> pick;

    public MyAdapter(Context context){
        this.context = context;
        mData = new ArrayList<>();
        pick = new ArrayList<>();
    }

    public void updateData(ArrayList<MyPhotoData> data){
        mData.clear();
        mData = data;
        pick.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_rv_item, parent, false);
        // 实例化viewholder
        return new ViewHolder(v);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MyImageView item = holder.itemView;
        item.setPicked(false);

        if (pick.contains(position)) {
            item.setPicked(true);
        }

        RequestOptions requestOptions = new RequestOptions().centerCrop();
        Glide.with(context)
                .load(mData.get(position).getData())
                .apply(requestOptions)
                .into(item);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPickCount() == 0){
                    Intent intent = new Intent();
                    intent.setClass(context, PhotoActivity.class);
                    intent.putExtra("photoOrder",position+1);
                    intent.putExtra("photoAmount",getItemCount());
                    intent.putExtra("photoData",mData.get(position));
                    context.startActivity(intent);
                }
                else {
                    setImagePick((MyImageView)v, position);
                }
            }
        });

        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setImagePick((MyImageView) v,position);
                return true;
            }
        });
        item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        ((MyImageView)v).setColorFilter(Color.argb(80,255,0,0));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    default:
                        ((MyImageView)v).clearColorFilter();
                        break;
                }
                return false;
            }
        });
    }

    private void setImagePick(MyImageView view, int pos){
        view.clearColorFilter();
        if (pick.contains(pos)){
            view.setPicked(false);
            pick.remove(pick.indexOf(pos));
            view.invalidate();
        }
        else {
            view.setPicked(true);
            pick.add(pos);
            view.invalidate();
        }
    }

    public void cancelImagePick(){
        pick.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public int getPickCount(){
        return pick == null ? 0 : pick.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView itemView;

        public ViewHolder(View view){
            super(view);
            itemView = view.findViewById(R.id.item_iv);
        }
    }

}
