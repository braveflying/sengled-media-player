package com.sengled.media.player.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sengled.media.player.R;

/**
 * Created by admin on 2017/7/20.
 */
public class LightAdjustWin extends PopupWindow implements SeekBar.OnSeekBarChangeListener{

    private SeekBar lightSeeker;

    private TextView percentTextView;

    public LightAdjustWin(Context context, View anchor) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.media_light_adjust_layout, null);
        lightSeeker = (SeekBar) contentView.findViewById(R.id.media_light_seek_bar);
        percentTextView = (TextView) contentView.findViewById(R.id.media_light_seek_percent);
        lightSeeker.setOnSeekBarChangeListener(this);

        this.setContentView(contentView);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int width = ((View)anchor.getParent()).getWidth();
        this.setWidth(width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90ffaa76")));
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(android.R.style.Animation);
        // 刷新状态
        this.update();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percentTextView.setText(progress+"%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
