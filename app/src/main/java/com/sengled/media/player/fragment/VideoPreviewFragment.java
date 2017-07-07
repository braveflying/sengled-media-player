package com.sengled.media.player.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.adapter.LivesRecyclerAdapter;
import com.sengled.media.player.common.Utils;
import com.sengled.media.player.entity.Lives;
import com.sengled.media.player.event.FullscreenEvent;
import com.sengled.media.player.event.LivesDataRefreshEvent;
import com.sengled.media.player.event.ScreenshotEvent;
import com.sengled.media.player.task.RequestLivingTask;
import com.sengled.media.player.widget.ShareDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */
public class VideoPreviewFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView liveRecyclerView;
    private FrameLayout fullscreenView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Lives> liveDataList;
    private LivesRecyclerAdapter mAdapter;
    private FullscreenEvent fullscreenEvent;

    private ShareDialog screenshotDialog;

    private RecycleChildAttachStateChangeListener childAttachStateChangeListener=new RecycleChildAttachStateChangeListener();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_video_priview_layout, container, false);

        swipeRefreshLayout =(SwipeRefreshLayout)view.findViewById(R.id.media_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        liveRecyclerView = (RecyclerView) view.findViewById(R.id.media_recycler_live_list);
        liveRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayout.VERTICAL,false);
        liveRecyclerView.setLayoutManager(mLayoutManager);
        liveRecyclerView.addOnChildAttachStateChangeListener(childAttachStateChangeListener);

        fullscreenView = (FrameLayout)view.findViewById(R.id.media_fullscreen_view);
        screenshotDialog = new ShareDialog();
        screenshotDialog.setClickListener(new ScreenshotDialogClickListener());

        liveDataList = new ArrayList<>();
        mAdapter = new LivesRecyclerAdapter(this.getContext(), liveDataList);
        liveRecyclerView.setAdapter(mAdapter);

        new RequestLivingTask(this.getContext()).execute();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenshot(ScreenshotEvent event){
        screenshotDialog.setScreenshotPhoto(event.getBitmap());
        screenshotDialog.show(this.getFragmentManager(),"screenshot");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFullscreen(FullscreenEvent event){
        this.fullscreenEvent = event;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*LinearLayoutManager layoutManager =(LinearLayoutManager) mLayoutManager;
        ViewGroup recyclerItem = (ViewGroup) layoutManager.findViewByPosition(fullscreenEvent.getPosition());*/

        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){ //竖屏
            setFullScreen(false);
            liveRecyclerView.addOnChildAttachStateChangeListener(childAttachStateChangeListener);

            fullscreenView.removeView(fullscreenEvent.getItemContainer());
            //recyclerItem.addView(fullscreenEvent.getItemContainer());
            fullscreenEvent.getItemParentContainer().addView(fullscreenEvent.getItemContainer());

            fullscreenView.setVisibility(View.GONE);
            liveRecyclerView.setVisibility(View.VISIBLE);
        }else {
            setFullScreen(true);
            liveRecyclerView.removeOnChildAttachStateChangeListener(childAttachStateChangeListener);

            fullscreenEvent.getItemParentContainer().removeAllViews();
            //recyclerItem.removeView(fullscreenEvent.getItemContainer());
            fullscreenView.addView(fullscreenEvent.getItemContainer());
            fullscreenView.setVisibility(View.VISIBLE);
            liveRecyclerView.setVisibility(View.GONE);
        }
        SLSVideoTextureView videoPlay = (SLSVideoTextureView) fullscreenEvent.getItemContainer().findViewById(R.id.list_item_video_view);
        videoPlay.setEnableAspect(false);
        ViewGroup.LayoutParams layoutParams = videoPlay.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        Log.e("handler", "400");
        videoPlay.setLayoutParams(layoutParams);
        //videoPlay.requestLayout();
    }

    private void setFullScreen(boolean fullScreen) {
        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        if (fullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getActivity().getWindow().setAttributes(attrs);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setAttributes(attrs);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){
            stopMedia();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        stopMedia();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        stopMedia();
        super.onStop();
    }

    /**
     * 停止正在播放的媒体
     */
    private void stopMedia(){
        LinearLayoutManager layoutManager = (LinearLayoutManager) mLayoutManager;
        for (int i = layoutManager.findFirstVisibleItemPosition(); i<= layoutManager.findLastVisibleItemPosition();i++){
            View view = layoutManager.findViewByPosition(i);
            if (view == null){
                break;
            }
            SLSVideoTextureView videoTextureView = (SLSVideoTextureView) view.findViewById(R.id.list_item_video_view);
            if (videoTextureView.isPlaying()){
                videoTextureView.stopPlayback();
            }
        }
        mLayoutManager.removeAllViews();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLivesData(LivesDataRefreshEvent event){
        liveDataList.addAll(event.getLives());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 下拉刷新
     */
    class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            new RequestLivingTask(VideoPreviewFragment.this.getContext()).execute();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 停止刷新
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    }



    /**
     * Recycle item 移出，移入事件
     */
    class RecycleChildAttachStateChangeListener implements RecyclerView.OnChildAttachStateChangeListener{

        @Override
        public void onChildViewAttachedToWindow(View view) {
            SLSVideoTextureView videoTextureView = (SLSVideoTextureView)view.findViewById(R.id.list_item_video_view);
            if (videoTextureView == null){
                return;
            }
            else  {
                view.findViewById(R.id.play_error_msg).setVisibility(View.GONE);
                view.findViewById(R.id.play_btn).setBackgroundResource(R.mipmap.video_play_btn);
                view.findViewById(R.id.covertImage).setVisibility(View.VISIBLE);
                view.findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.descText).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            SLSVideoTextureView videoTextureView = (SLSVideoTextureView)view.findViewById(R.id.list_item_video_view);
            if (videoTextureView == null){
                return;
            }
            if (videoTextureView !=null && videoTextureView.isPlaying()) {
                ImageButton playBtn = (ImageButton) view.findViewById(R.id.play_btn);
                ImageView covertImage = (ImageView) view.findViewById(R.id.covertImage);
                View coverView = view.findViewById(R.id.media_video_item_cover_include_id);

                coverView.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.VISIBLE);
                covertImage.setVisibility(View.VISIBLE);
                videoTextureView.stopPlayback();
            }
        }
    }

    //截图事件
    class ScreenshotDialogClickListener implements ShareDialog.OnDialogClickListener{

        private String mSaveDir;
        @Override
        public void onShare(Bitmap bitmap, Uri uri) {
            if (mSaveDir== null){
                mSaveDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "SLS";
            }

            File imgDir = new File(mSaveDir);
            if (!imgDir.exists()){
                imgDir.mkdirs();
            }
            File file = new File(mSaveDir, System.currentTimeMillis() + ".jpg");
            Utils.verifyStoragePermissions(getActivity());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                Toast.makeText(getContext(), "保存成功，路径为:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "保存本地失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
