package com.sengled.media.player.widget.timeaxis;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sengled.media.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/5/25.
 */
public class SengledTimeAxis extends RelativeLayout {
    private ListView axisListView;

    private SengledTimeAxisAdapter dataAdapter;

    private List<AxisMotionBean> dataList = new ArrayList<>();

    public SengledTimeAxis(Context context) {
        super(context);
        initView(context);
    }

    public SengledTimeAxis(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.media_timeaxis_layout, this, true);
        axisListView = (ListView) view.findViewById(R.id.media_axis_content);

        dataAdapter = new SengledTimeAxisAdapter(context, dataList);
        axisListView.setAdapter(dataAdapter);
    }

    public void updateMotions(List<AxisMotionBean> dataList) {
        this.dataList .addAll(dataList);
        dataAdapter.notifyDataSetChanged();
    }

    public void updateVideoDates(List<AxisVideoBean> dataList){

        View bgView = new View(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100);
        bgView.setLayoutParams(params);
        bgView.requestLayout();
        bgView.setBackgroundColor(Color.parseColor("#fce0cd"));
        addView(bgView);
    }
}
