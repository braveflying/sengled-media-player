package com.sengled.media.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sengled.media.player.R;
import com.sengled.media.player.entity.Lives;

import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by admin on 2017/4/18.
 */
public class SampleVideoDataAdapter extends BaseAdapter{

    private List<Lives> mLives;
    private Context mContext;
    private LayoutInflater mInflater;

    public SampleVideoDataAdapter(Context context, List<Lives> lives) {
        mLives = lives;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mLives.size();
    }

    @Override
    public Object getItem(int position) {
        return mLives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.media_video_realtime_sample_adapter_item_layout, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deviceName.setText(mLives.get(position).getToken());
        return convertView;
    }

    private class ViewHolder {
        TextView deviceName;
        ImageView deviceIcon;
        public ViewHolder(View convertView) {
            deviceName = (TextView) convertView.findViewById(R.id.media_video_device_name);
            deviceIcon = (ImageView) convertView.findViewById(R.id.media_video_device_icon);
        }
    }
}
