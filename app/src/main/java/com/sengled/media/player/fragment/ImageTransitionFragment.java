package com.sengled.media.player.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sengled.media.player.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/4/20.
 */
public class ImageTransitionFragment extends Fragment {

    public static final String TAG = StaggerImageFragment.class.getSimpleName();

    @BindView(R.id.media_image_transition_view)
    ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(
                    TransitionInflater.from(getContext())
                            .inflateTransition(android.R.transition.move));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_image_stagger_transition_layout, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(getContext())
                .load(getArguments().get("path"))
                .into(imageView);
    }

    @OnClick(R.id.media_image_transition_view)
    public void onImageClick(View view){
        getFragmentManager().popBackStackImmediate();
    }
}
