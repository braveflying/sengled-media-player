package com.sengled.media.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sengled.media.player.R;

/**
 * Created by zhuguohui on 2016/8/21 0021.
 */
public class PageIndicator extends LinearLayout{
    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void InitIndicatorItems(int itemsNumber) {
        removeAllViews();
        for (int i = 0; i < itemsNumber; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.dot_unselected);
            imageView.setPadding(10, 0, 10, 0);
            addView(imageView);
        }
        onPageSelected(0);
    }

    public void onPageSelected(int pageIndex) {
        ImageView imageView = (ImageView) getChildAt(pageIndex);
        if(imageView!=null) {
            unSelectedAll();
            imageView.setImageResource(R.drawable.dot_selected);
        }
    }

    public void unSelectedAll(){
        ImageView imageView = null;
        for (int i = 0; i < getChildCount(); i++) {
            imageView = (ImageView)getChildAt(i);
            if (imageView !=null)
                imageView.setImageResource(R.drawable.dot_unselected);
        }
    }

    public void onPageUnSelected(int pageIndex) {
        ImageView imageView = (ImageView) getChildAt(pageIndex);
        if(imageView!=null) {
            imageView.setBackgroundResource(R.drawable.dot_unselected);
        }
    }
}
