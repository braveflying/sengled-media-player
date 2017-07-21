package com.sengled.media.player.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;
import android.view.ViewGroup.LayoutParams;

import com.sengled.media.player.R;
import com.sengled.media.player.adapter.RecycleGalleryDataAdapter;
import com.sengled.media.player.event.RecycleImageRefreshEvent;
import com.sengled.media.player.task.LoadLocalImageTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/13.
 */
public class ImageSwitchFragment extends Fragment{

    @BindView(R.id.media_image_switch)
    ImageSwitcher mImageSwitcher;

    @BindView(R.id.media_image_gallery_view)
    RecyclerView recyclerImageView;

    private LinearLayoutManager mLayoutManager;
    private RecycleGalleryDataAdapter imageDataAdapter;
    private ArraySet<String> imagePathSet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_image_switch_layout, container,false);
        ButterKnife.bind(this, view);

        initComponet();
        initData();
        return view;
    }

    private void initComponet(){
        mLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayout.HORIZONTAL,false);
        recyclerImageView.setLayoutManager(mLayoutManager);
        recyclerImageView.setItemAnimator(new DefaultItemAnimator());

        imagePathSet = new ArraySet<>();
        imageDataAdapter = new RecycleGalleryDataAdapter(this.getContext(), imagePathSet, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =Integer.parseInt(v.getContentDescription().toString());
                mImageSwitcher.setImageURI(Uri.fromFile(new File(imagePathSet.valueAt(position))));
            }
        });

        recyclerImageView.setAdapter(imageDataAdapter);

        mImageSwitcher.setInAnimation(getContext(), android.R.anim.fade_in);
        mImageSwitcher.setOutAnimation(getContext(), android.R.anim.fade_out);
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getContext());
                imageView.setBackgroundColor(0xFFFFFFFF);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);//居中显示
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));//定义组件
                return imageView;
            }
        });
    }

    private void initData(){
        String imageDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "SLS";
        new LoadLocalImageTask().execute(imageDir);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshRecycleImage(RecycleImageRefreshEvent event){
        imagePathSet.addAll(event.getImagePathList());
        imageDataAdapter.notifyDataSetChanged();

        //首次默认显示第一张图
        if (((ImageView)mImageSwitcher.getCurrentView()).getDrawable() == null){
            mImageSwitcher.setImageURI(Uri.fromFile(new File(imagePathSet.valueAt(0))));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            initData();
        }
        super.onHiddenChanged(hidden);
    }
}
