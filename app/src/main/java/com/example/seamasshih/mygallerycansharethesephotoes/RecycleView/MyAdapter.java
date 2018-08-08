package com.example.seamasshih.mygallerycansharethesephotoes.RecycleView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.seamasshih.mygallerycansharethesephotoes.Data.MyPhotoData;
import com.example.seamasshih.mygallerycansharethesephotoes.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<MyPhotoData> mData;
    private MyAdapter.OnItemClickListener listener;
    private Context context;

    public MyAdapter(Context context , ArrayList<MyPhotoData> data){
        this.context = context;
        mData = data;
    }

    public void upDateData(ArrayList<MyPhotoData> data){
        mData = data;
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

    public void setOnItemListener(MyAdapter.OnItemClickListener listener){
        this.listener = listener;
    }


    public void onBindViewHolder(final ViewHolder holder, int position) {
        MyImageView item = holder.itemView;

        Glide.with(context)
                .load(mData.get(position).getData())
                .into(item);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    int pos = holder.getLayoutPosition();
                    listener.onItemClick(holder.itemView,pos);
                }
            }
        });

        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null){
                    int pos = holder.getLayoutPosition();
                    listener.onItemLongClick(holder.itemView,pos);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView itemView;

        public ViewHolder(View view){
            super(view);
            itemView = view.findViewById(R.id.item_iv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
