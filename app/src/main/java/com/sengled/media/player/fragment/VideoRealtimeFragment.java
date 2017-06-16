package com.sengled.media.player.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.adapter.MyTestAdapter;
import com.sengled.media.player.adapter.VideoRealtimeAdapter;
import com.sengled.media.player.common.PagingScrollHelper;
import com.sengled.media.player.entity.Lives;
import com.sengled.media.player.event.LivesDataRefreshEvent;
import com.sengled.media.player.task.RequestLivingTask;
import com.sengled.media.player.widget.HorizontalPageLayoutManager;
import com.sengled.media.player.widget.PageIndicator;
import com.sengled.media.player.widget.RecyclerViewWithEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/17.
 */
public class VideoRealtimeFragment extends Fragment implements PagingScrollHelper.onPageChangeListener,View.OnClickListener {

    private static final String SP_VID_KEY = "vIds";

    private RecyclerViewWithEmpty recyclerView;
    private VideoRealtimeAdapter realtimeAdapter;
    PageIndicator pageIndicator;

    @BindView(R.id.empty_view)
    View  emptyView;

    private HorizontalPageLayoutManager horizontalPageLayoutManager = null;
    private TextView oneGridBtn,fourGridBtn,nineGridBtn,sixteenGridBtn;
    private PagingScrollHelper helper = new PagingScrollHelper();

    private List<Lives> allPlaylist;
    private String[] tokens;

    public List<Lives> selectPlaylist = new ArrayList<>();
    public List<Lives> cleanPlaylist = new ArrayList<>();
    private boolean[] isCheckeds;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Set<String> vIds = new HashSet<String>();
        for (Lives lives : selectPlaylist) {
            vIds.add(lives.getStream_addr());
        }
        SharedPreferences sp =getContext().getSharedPreferences("Videos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(SP_VID_KEY, vIds);
        editor.apply();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_video_realtime_layout, container, false);
        ButterKnife.bind(this, view);
        recyclerView = (RecyclerViewWithEmpty) view.findViewById(R.id.media_video_realtime_view);
        recyclerView.setEmptyView(emptyView);

        realtimeAdapter = new VideoRealtimeAdapter(getContext(),selectPlaylist);

        recyclerView.setAdapter(realtimeAdapter);
        horizontalPageLayoutManager = new HorizontalPageLayoutManager(2, 2);
        recyclerView.setLayoutManager(horizontalPageLayoutManager);

        helper.setOnPageChangeListener(this);
        helper.setUpRecycleView(recyclerView);

        initView(view);
        initEvent();
        initData();
        refreshButton(fourGridBtn);
        return view;
    }

    private void initView(View view){
        oneGridBtn= (TextView) view.findViewById(R.id.one_grid_button);
        fourGridBtn= (TextView) view.findViewById(R.id.four_grid_button);
        nineGridBtn = (TextView) view.findViewById(R.id.nine_grid_button);
        sixteenGridBtn = (TextView) view.findViewById(R.id.sixteen_grid_button);
        pageIndicator= (PageIndicator) view.findViewById(R.id.pageindicator);
    }

    private void initEvent(){
        oneGridBtn.setOnClickListener(this);
        fourGridBtn.setOnClickListener(this);
        nineGridBtn.setOnClickListener(this);
        sixteenGridBtn.setOnClickListener(this);
        recyclerView.addOnChildAttachStateChangeListener(new RecycleChildAttachStateChangeListener());

        recyclerView.setRemovedListener(new RecyclerViewWithEmpty.ItemRemovedListener() {
            @Override
            public void onRemoved(int positionStart, int itemCount) {
                for (int i = positionStart; i < (itemCount+positionStart); i++) {
                    View view = horizontalPageLayoutManager.findViewByPosition(i);
                    if (view != null) {
                        SLSVideoTextureView videoView = (SLSVideoTextureView) view.findViewById(R.id.list_item_video_view);
                        videoView.stopPlayback();
                    }
                }
            }
        });
    }

    private void initData(){
        new RequestLivingTask(this.getContext()).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.media_video_bar_menu ,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.media_video_select_bar){
            //选中的新的数据集合
            final List<Lives> newDataSet = new ArrayList<>();

            new  AlertDialog.Builder(this.getContext())
                    .setTitle(getString(R.string.select_realtime_video))
                    .setIcon(R.mipmap.sengled_default_photo)
                    .setMultiChoiceItems(tokens, isCheckeds, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            Lives lives = allPlaylist.get(which);
                            if (isChecked){
                                if (!selectPlaylist.contains(lives)){
                                    newDataSet.add(lives);
                                    isCheckeds[which] = true;
                                }
                            }else {
                                if (selectPlaylist.contains(lives)){
                                    cleanPlaylist.add(lives);
                                    isCheckeds[which] =false;
                                }else {
                                    newDataSet.remove(lives);
                                }
                            }
                        }
                    })
                    .setPositiveButton(getString(R.string.start_preview), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startPreview(newDataSet);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newDataSet.removeAll(selectPlaylist);
                            for (Object o: newDataSet){
                                isCheckeds[allPlaylist.indexOf(o)]=false;
                            }
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 开始预览
     * @param playlist
     */
    private void startPreview(List<Lives> playlist){

        List<Lives> old = new ArrayList<>(selectPlaylist);

        selectPlaylist.removeAll(cleanPlaylist);
        selectPlaylist.addAll(playlist);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(old, selectPlaylist));
        diffResult.dispatchUpdatesTo(realtimeAdapter);

        pageIndicator.InitIndicatorItems(calPages(selectPlaylist.size(), horizontalPageLayoutManager.getOnePageSize()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLivesData(LivesDataRefreshEvent event){
        if (allPlaylist == null){
            allPlaylist = new ArrayList<>();
        }else {
            allPlaylist.clear();
        }
        allPlaylist.addAll(event.getLives());

        tokens = new String[allPlaylist.size()];
        isCheckeds = new boolean[allPlaylist.size()];
        for (int i = 0; i < allPlaylist.size(); i++) {
            tokens[i] = allPlaylist.get(i).getToken();
        }

        //显示上次预览的数据
        SharedPreferences sp =getContext().getSharedPreferences("Videos", Context.MODE_PRIVATE);
        Set<String> vIdSet = sp.getStringSet(SP_VID_KEY, null);
        List<Lives> historyData = new ArrayList<>();

        for (int i = 0; i < allPlaylist.size(); i++) {
            if (vIdSet.contains(allPlaylist.get(i).getStream_addr())){
                historyData.add(allPlaylist.get(i));
                isCheckeds[i] =true;
            }
        }

        if (!historyData.isEmpty()){
            startPreview(historyData);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){
            stopMedia();
        }
        super.onHiddenChanged(hidden);
    }

    /**
     * 停止正在播放的媒体
     */
    private void stopMedia(){
        for (int i = 0; i < horizontalPageLayoutManager.getItemCount(); i++) {
            View view = horizontalPageLayoutManager.findViewByPosition(i);
            if (view == null){
                break;
            }
            SLSVideoTextureView videoTextureView = (SLSVideoTextureView) view.findViewById(R.id.list_item_video_view);
            if (videoTextureView!=null && videoTextureView.isPlaying()){
                videoTextureView.stopPlayback();
            }
        }
        recyclerView.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one_grid_button:
                horizontalPageLayoutManager = new HorizontalPageLayoutManager(1,1);
                break;
            case R.id.four_grid_button:
                horizontalPageLayoutManager = new HorizontalPageLayoutManager(2,2);
                break;
            case R.id.nine_grid_button:
                horizontalPageLayoutManager = new HorizontalPageLayoutManager(3,3);
                break;
            case R.id.sixteen_grid_button:
                horizontalPageLayoutManager = new HorizontalPageLayoutManager(4,4);
                break;
            default:

        }

        int pageSize = calPages(selectPlaylist.size(), horizontalPageLayoutManager.getOnePageSize());
        pageIndicator.InitIndicatorItems(pageSize);
        recyclerView.setLayoutManager(horizontalPageLayoutManager);
        helper.updateLayoutManger();

        refreshButton(v);
    }

    private void refreshButton(View btnView){
        oneGridBtn.setEnabled(true);
        fourGridBtn.setEnabled(true);
        nineGridBtn.setEnabled(true);
        sixteenGridBtn.setEnabled(true);
        btnView.setEnabled(false);
    }

    private int calPages(int total, int pageSize){
        int result = total/ pageSize + (total % pageSize == 0 ? 0 : 1);
        return result;
    }

    @Override
    public void onPageChange(int index) {
        pageIndicator.onPageSelected(index);
    }


    class  DiffCallBack extends DiffUtil.Callback{

        private List<Lives> mOldDatas, mNewDatas;

        public DiffCallBack(List<Lives> mOldDatas, List<Lives> mNewDatas) {
            this.mOldDatas = mOldDatas;
            this.mNewDatas = mNewDatas;
        }

        @Override
        public int getOldListSize() {
            return mOldDatas.size();
        }

        @Override
        public int getNewListSize() {
            return mNewDatas.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (mOldDatas.get(oldItemPosition) == mNewDatas.get(newItemPosition)){
                return true;
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (mOldDatas.get(oldItemPosition).getStream_addr()
                    .equals(mNewDatas.get(newItemPosition).getStream_addr())){
                return true;
            }
            return false;
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
            if (!videoTextureView.isPlaying()){
                videoTextureView.start();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            SLSVideoTextureView videoTextureView = (SLSVideoTextureView)view.findViewById(R.id.list_item_video_view);
            if (videoTextureView == null){
                return;
            }
            if (videoTextureView.isPlaying()) {
                view.findViewById(R.id.media_video_realtime_item_play_btn).setVisibility(View.VISIBLE);
                videoTextureView.stopPlayback();
            }
        }
    }
}
