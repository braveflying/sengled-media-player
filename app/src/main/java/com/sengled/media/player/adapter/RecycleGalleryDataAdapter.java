package com.sengled.media.player.adapter;

import android.content.Context;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sengled.media.player.R;

import java.io.File;
import java.util.List;

/**
 * Created by admin on 2017/4/14.
 */
public class RecycleGalleryDataAdapter extends RecyclerView.Adapter<RecycleGalleryDataAdapter.ViewHolder>{

    private ArraySet<String> uriSet;
    private Context mContext;
    private LayoutInflater mInflater;
    private View.OnClickListener itemClickListener;

    public RecycleGalleryDataAdapter(Context context, ArraySet<String> uriSet, View.OnClickListener itemClickListener) {
        this.uriSet = uriSet;
        this.mContext = context;
        this.itemClickListener = itemClickListener;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.media_image_recycle_item_layout, parent,false);
        ViewHolder vh = new ViewHolder(view);
        vh.imageView.setOnClickListener(itemClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(new File(uriSet.valueAt(position)))
                .thumbnail(0.1f )
                .into(holder.imageView);

        holder.imageView.setContentDescription(position+"");
    }

    @Override
    public int getItemCount() {
        return uriSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.recycle_item_image);
        }
    }
}
