package com.sengled.media.player.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sengled.media.player.R;
import com.sengled.media.player.adapter.StaggerImageAdapter;
import com.sengled.media.player.entity.Lives;
import com.sengled.media.player.event.RecycleImageRefreshEvent;
import com.sengled.media.player.task.LoadLocalImageTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/20.
 */
public class StaggerImageFragment extends Fragment {

    public static final String TAG = StaggerImageFragment.class.getSimpleName();

    @BindView(R.id.media_image_stagger_view)
    RecyclerView recyclerImageView;

    private StaggerImageAdapter adapter;
    private Fragment transitionFragment;
    private List<String> dataList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transitionFragment = new ImageTransitionFragment();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_image_stagger_layout,container, false);
        ButterKnife.bind(this, view);

        //recyclerImageView = (RecyclerView)view.findViewById(R.id.media_image_stagger_view);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerImageView.setLayoutManager(layoutManager);

        adapter = new StaggerImageAdapter(this.getContext(),inflater);
        recyclerImageView.setAdapter(adapter);

        initEvent();
        return view;
    }

    private void initEvent(){
        adapter.setItemOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v.findViewById(R.id.recycle_item_image);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(StaggerImageFragment.this);

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addSharedElement(imageView, "simpleImage").addToBackStack(TAG);
                if (getFragmentManager().findFragmentByTag(ImageTransitionFragment.TAG)== null){
                    transaction .add(R.id.main_content_frame_parent, transitionFragment,ImageTransitionFragment.TAG);
                }else {
                    transaction.show(transitionFragment);
                };

                Bundle bundle = new Bundle();
                bundle.putString("path", dataList.get(Integer.parseInt(v.getTag().toString())));
                transitionFragment.setArguments(bundle);
                transaction.commit();

                /*if (transitionFragment == null){
                    transitionFragment = new ImageTransitionFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putString("path", dataList.get((int)v.getTag()));
                transitionFragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                        .replace(R.id.main_content_frame_parent, transitionFragment)
                        .addToBackStack(null)
                        .commit();*/

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String imageDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "SLS";
        new LoadLocalImageTask().execute(imageDir);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshRecycleImage(RecycleImageRefreshEvent event){
        dataList = event.getImagePathList();
        adapter.updateData(event.getImagePathList());
    }

}
