package com.sengled.media.player.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.SLSMediaPlayer;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sengled.media.player.R;
import com.sengled.media.player.common.Utils;
import com.sengled.media.player.entity.Lives;
import com.sengled.media.player.event.FullscreenEvent;
import com.sengled.media.player.event.ScreenshotEvent;
import com.sengled.media.player.widget.SengledMediaController;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by admin on 2017/3/27.
 */
public class LivesRecyclerAdapter extends RecyclerView.Adapter<LivesRecyclerAdapter.ViewHolder> {

    private List<Lives> mLives;
    private Context mContext;
    private LayoutInflater mInflater;

    public LivesRecyclerAdapter(Context context, List<Lives> lives) {
        mLives = lives;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.media_video_preview_list_item_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);

        vh.slsVideoTextureView.setMediaController(vh.controllerView);
        initVideoPlayer(vh.slsVideoTextureView);
        initPlayerEvent(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lives lives = mLives.get(position);
        holder.descView.setText(lives.getToken());

        ControllerClickListener clickListener = new ControllerClickListener(holder, position);
        holder.playBtn.setOnClickListener(clickListener);
        holder.controllerView.setmFullscreenListener(clickListener);
        holder.controllerView.setScreenshotListener(clickListener);

        Glide.with(mContext)
                .load(lives.getImage_path())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .placeholder(R.mipmap.play_background)
                .crossFade()
                .into(holder.coverImageView);
    }


    @Override
    public int getItemCount() {
        return mLives.size();
    }


    private void initVideoPlayer(SLSVideoTextureView videoView){
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        videoView.setAVOptions(options);
        videoView.setDisplayAspectRatio(SLSVideoTextureView.ASPECT_RATIO_16_9);
    }

    private void initPlayerEvent(ViewHolder holder){
        holder.slsVideoTextureView.setOnPreparedListener(new PlayerPreparedListener(holder));
        holder.slsVideoTextureView.setOnErrorListener(new PlayerErrorListener(holder));
        holder.slsVideoTextureView.setOnCompletionListener(new PlayerCompletionListener(holder));
        holder.slsVideoTextureView.setOnBufferingUpdateListener(new SLSMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(SLSMediaPlayer var1, int precent) {
                Log.d("hello", "onBufferingUpdate: " + precent);
            }
        });
    }

    /**
     * 预播放监听
     */
    class PlayerPreparedListener implements SLSMediaPlayer.OnPreparedListener{

        ViewHolder holder;

        public PlayerPreparedListener(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onPrepared(SLSMediaPlayer var1) {
            holder.slsVideoTextureView.start();

            new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    holder.errorMsgView.setVisibility(View.GONE);
                    holder.playBtn.setVisibility(View.GONE);
                    holder.descView.setVisibility(View.GONE);
                    holder.coverImageView.setVisibility(View.GONE);
                    holder.mLoadingView.setVisibility(View.GONE);
                }
            }.sendMessageDelayed(Message.obtain(),1000);

        }
    }

    /**
     * 播放完成
     */
    class PlayerCompletionListener implements SLSMediaPlayer.OnCompletionListener{
        private LivesRecyclerAdapter.ViewHolder holder;

        public PlayerCompletionListener(LivesRecyclerAdapter.ViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void onCompletion(SLSMediaPlayer var1) {
            if (Utils.getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            holder.playBtn.setBackgroundResource(R.mipmap.play_button);
            holder.playBtn.setVisibility(View.VISIBLE);
            holder.errorMsgView.setVisibility(View.VISIBLE);
            holder.errorMsgView.setText("Playback completed, click replay");
        }
    }

    /**
     * 播放错误监听
     */
    class  PlayerErrorListener implements SLSMediaPlayer.OnErrorListener{

        private LivesRecyclerAdapter.ViewHolder holder;

        public PlayerErrorListener(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onError(SLSMediaPlayer player, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case SLSMediaPlayer.ERROR_CODE_INVALID_URI:
                    showTips("Invalid URL !");
                    break;
                case SLSMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showTips("404 resource not found !");
                    break;
                case SLSMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showTips("Connection refused !");
                    break;
                case SLSMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case SLSMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showTips("Empty playlist !");
                    break;
                case SLSMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case SLSMediaPlayer.ERROR_CODE_IO_ERROR:
                    showTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case SLSMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showTips("Unauthorized Error !");
                    break;
                case SLSMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case SLSMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case SLSMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    isNeedReconnect = true;
                    break;
                default:
                    showTips("unknown error !");
                    break;
            }

            return true;
        }

        private void showTips(String msg){
            //viewHolder.ivPortrait.setImageResource();
            holder.playBtn.setBackgroundResource(R.mipmap.play_error);
            holder.playBtn.setVisibility(View.VISIBLE);
            holder.errorMsgView.setText(msg + " Click Retry");
            holder.errorMsgView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放按钮监听
     */
    private class ControllerClickListener implements View.OnClickListener {
        int position;
        ViewHolder holder;
        Lives lives;

        ControllerClickListener(ViewHolder holder,int position){
            this.holder  = holder;
            this.position = position;
            this.lives = mLives.get(position);
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_btn:
                    playClick(v);
                    break;
                case R.id.switch_fullscreen_btn:
                    fullscreenClick(v);
                    break;
                case R.id.screenshot_btn:
                    screenshotClick(v);
                    break;
                default:
                    // TODO: 2017/4/12
            }
        }

        //播放事件
        private void playClick(View v){
            holder.playBtn.setVisibility(View.GONE);
            holder.errorMsgView.setVisibility(View.GONE);
            holder.mLoadingView.setVisibility(View.VISIBLE);
            holder.slsVideoTextureView.setVideoPath(lives.getStream_addr());
            //holder.slsVideoTextureView.setVolume(0,0);
            //holder.slsVideoTextureView.start();
        }

        public void fullscreenClick(View v){
            Log.e("full", "full");
            EventBus.getDefault().post(new FullscreenEvent(holder.itemContainer, position));

            if (Utils.getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                v.setBackgroundResource(R.mipmap.ic_switch_screen);
            }else {
                ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                v.setBackgroundResource(R.mipmap.ic_switch_screen_collapse);
            }
        }

        public void screenshotClick(View v){
            Bitmap bitmap = holder.slsVideoTextureView.getTextureView().getBitmap();
            EventBus.getDefault().post(new ScreenshotEvent(bitmap));
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewGroup itemContainer;

        public SLSVideoTextureView slsVideoTextureView;
        public View mLoadingView;
        public View coverView;
        public SengledMediaController controllerView;

        public TextView descView;
        public ImageView coverImageView;
        public ImageButton playBtn;
        public TextView errorMsgView;

        public ViewHolder(View view) {
            super(view);
            itemContainer = (ViewGroup) view.findViewById(R.id.item_container);

            slsVideoTextureView = (SLSVideoTextureView) view.findViewById(R.id.list_item_video_view);
            mLoadingView = view.findViewById(R.id.item_loading_view);
            coverView = view.findViewById(R.id.media_video_item_cover_include_id);
            controllerView = (SengledMediaController)view.findViewById(R.id.item_media_controller);
            slsVideoTextureView.setBufferingIndicator(mLoadingView);

            descView = (TextView) coverView.findViewById(R.id.descText);
            coverImageView = (ImageView)coverView.findViewById(R.id.covertImage);
            playBtn = (ImageButton) coverView.findViewById(R.id.play_btn);
            errorMsgView = (TextView) coverView.findViewById(R.id.play_error_msg);
        }
    }
}
