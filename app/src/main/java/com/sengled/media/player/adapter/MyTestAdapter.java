package com.sengled.media.player.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/4.
 */
public class MyTestAdapter extends RecyclerView.Adapter<MyTestAdapter.TestViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    public static ArrayList<String> dataList = new ArrayList<>();
    /*static {
        for (int i = 0; i < 10; i++) {
            dataList.add("name: "+ i);
        }
    }
*/
    public MyTestAdapter(Context mContext, LayoutInflater mInflater) {
        this.mContext = mContext;
        this.mInflater = mInflater;
    }
    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.media_video_realtime_sample_adapter_item_layout, parent,false);
        TestViewHolder holder = new TestViewHolder(v);
        initVideoPlayer(holder.player);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TestViewHolder holder, final int position) {

        holder.deviceName.setText(dataList.get(position));
        new Thread(new Runnable() {
            @Override
            public void run() {
                holder.player.setVideoPath(dataList.get(position));
                holder.player.start();
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void initVideoPlayer(SLSVideoTextureView videoView){
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);
        videoView.setAVOptions(options);
        videoView.setDisplayAspectRatio(SLSVideoTextureView.ASPECT_RATIO_16_9);
    }

    public class TestViewHolder  extends RecyclerView.ViewHolder{
        TextView deviceName;
        SLSVideoTextureView player;
        public TestViewHolder(View convertView) {
            super(convertView);
            deviceName = (TextView) convertView.findViewById(R.id.media_video_device_name);
            //player = (SLSVideoTextureView)convertView.findViewById(R.id.list_item_video_view_test);
        }
    }
}
