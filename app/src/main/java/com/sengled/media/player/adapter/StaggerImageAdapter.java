package com.sengled.media.player.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sengled.media.player.R;
import com.sengled.media.player.common.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/20.
 */
public class StaggerImageAdapter extends RecyclerView.Adapter<StaggerImageAdapter.ViewHolder>{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> dataList = new ArrayList<>();
    private View.OnClickListener itemOnClickListener;

    public StaggerImageAdapter(Context mContext, LayoutInflater mInflater) {
        this.mContext = mContext;
        this.mInflater = mInflater;
    }

    public void setItemOnClickListener(View.OnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void updateData(List<String> datas){
        dataList.clear();
        dataList.addAll(datas);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.media_image_recycle_item_layout, parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (itemOnClickListener != null){
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(itemOnClickListener);
        }
        ViewCompat.setTransitionName(holder.imageView, String.valueOf(position) + "_image");

        Glide.with(mContext)
                .load(new File(dataList.get(position)))
                .crossFade()
                .thumbnail(0.1f )
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.recycle_item_image);

            Display display = Utils.getActivityFromView(imageView).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int width = size.x/3;
            int height = (int) (200 + Math.random() * 400);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.setMargins(0,15,0,0);
            //设置图片的相对于屏幕的宽高比
            imageView.setLayoutParams(params);
        }
    }
}
