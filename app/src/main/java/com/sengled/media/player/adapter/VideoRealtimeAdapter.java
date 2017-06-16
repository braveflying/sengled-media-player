package com.sengled.media.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.SLSMediaPlayer;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.entity.Lives;

import java.util.List;

/**
 * Created by zhuguohui on 2016/11/8.
 */

public class VideoRealtimeAdapter extends RecyclerView.Adapter<VideoRealtimeAdapter.ViewHolder> {

    private List<Lives> playlist;
    private Context mContext;
    public VideoRealtimeAdapter(Context context, List<Lives> playlist) {
        this.playlist = playlist;
        this.mContext = context;
    }

    public void setData(List<Lives> playlist){
        this.playlist = playlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.media_video_realtime_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        initVideoPlayer(holder);
        initEvent(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String title=playlist.get(position).getToken();
        holder.videoDesc.setText(title);

        if (holder.videoView.isPlaying()) {
            holder.videoView.stopPlayback();
        }

        holder.playButton.setVisibility(View.GONE);
        holder.loadingView.setVisibility(View.VISIBLE);
        holder.videoView.setVideoPath(playlist.get(position).getStream_addr());
        //holder.slsVideoTextureView.setVolume(0,0);

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.coverView.setVisibility(View.GONE);
                holder.playButton.setVisibility(View.GONE);
                holder.loadingView.setVisibility(View.VISIBLE);
                holder.videoView.setVideoPath(playlist.get(position).getStream_addr());
                //Toast.makeText(v.getContext(),"item"+ title+" 被点击了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    private void initEvent(final ViewHolder holder){

        holder.videoView.setOnPreparedListener(new SLSMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(SLSMediaPlayer slsMediaPlayer) {
                holder.coverView.setVisibility(View.GONE);
                holder.playButton.setVisibility(View.GONE);
                slsMediaPlayer.start();
            }
        });
    }


    private void initVideoPlayer(final ViewHolder holder){
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        holder.videoView.setAVOptions(options);
        holder.videoView.setDisplayAspectRatio(SLSVideoTextureView.ASPECT_RATIO_16_9);

        holder.videoView.setBufferingIndicator(holder.loadingView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView videoDesc;
        ImageView coverView;
        SLSVideoTextureView videoView;
        ImageView playButton;
        View loadingView;

        public ViewHolder(View itemView) {
            super(itemView);
            videoView = (SLSVideoTextureView)itemView.findViewById(R.id.list_item_video_view);
            videoDesc = (TextView) itemView.findViewById(R.id.media_video_realtime_item_desc);
            coverView = (ImageView) itemView.findViewById(R.id.media_video_realtime_item_cover);
            playButton = (ImageView) itemView.findViewById(R.id.media_video_realtime_item_play_btn);
            loadingView = itemView.findViewById(R.id.item_loading_view);
        }
    }
}
