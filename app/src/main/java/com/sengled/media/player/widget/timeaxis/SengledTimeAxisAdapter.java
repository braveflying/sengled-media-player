package com.sengled.media.player.widget.timeaxis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sengled.media.player.R;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2017/5/25.
 */
public class SengledTimeAxisAdapter extends BaseAdapter {

    private Context context;
    private List<AxisMotionBean> dataList;
    private LayoutInflater inflater;

    private static final Long MOTION_INTERVAL_SECOND = 15L;

    public SengledTimeAxisAdapter(Context context, List<AxisMotionBean> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.media_timeaxis_item_layout, parent, false);
        viewHolder.axisDotText = (TextView) convertView.findViewById(R.id.media_timeaxis_num);
        viewHolder.axisMarkRed = (Button) convertView.findViewById(R.id.media_axis_mark_red);
        viewHolder.axisMarkRed = (Button) convertView.findViewById(R.id.media_axis_mark_red);
        viewHolder.axisMarkBlue = (Button) convertView.findViewById(R.id.media_axis_mark_blue);
        viewHolder.axisMarkGreen = (Button) convertView.findViewById(R.id.media_axis_mark_green);
        viewHolder.axisDot = (ImageView) convertView.findViewById(R.id.media_timeaxis_dot);
        viewHolder.convertView = convertView;

        drawContent(viewHolder, position);
        return convertView;
    }

    private void drawContent(ViewHolder holder,int position){
        AxisMotionBean bean = dataList.get(position);
        holder.axisDotText.setText(bean.getTimeMark());

        measureSize(holder, bean);
    }

    private void measureSize(ViewHolder holder, AxisMotionBean bean){
        List<AxisMotionBean.ItemMotionInfo> motionDatas = bean.getMotionInfoList();
        int itemViewHeight = holder.convertView.getLayoutParams().height;
        float secondHeight = (float) itemViewHeight/(float) 3600; //每一秒显示的高度

        int dotHeight =holder.axisDot.getLayoutParams().height; //时间标记dot 的高度，为了跟时间标记对齐

        LinkedList<AxisMotionBean.ItemMotionInfo> motionPoint = new LinkedList<>();
        for (AxisMotionBean.ItemMotionInfo motionData : motionDatas) {
            if (motionPoint.isEmpty()){
                motionData.endPoint = motionData.timePoint;
                motionPoint.add(motionData);
            }else {
                AxisMotionBean.ItemMotionInfo lastMotionPoint = motionPoint.getLast();
                if ((motionData.timePoint.getTime() -lastMotionPoint.endPoint.getTime()) <= (MOTION_INTERVAL_SECOND*1000)
                        && lastMotionPoint.zone == motionData.zone){
                    lastMotionPoint.endPoint = motionData.timePoint;
                }else {
                    motionData.endPoint = motionData.timePoint;
                    motionPoint.add(motionData);
                }
            }
        }

        for (AxisMotionBean.ItemMotionInfo itemMotionInfo : motionPoint) {

            long diffTime = itemMotionInfo.endPoint.getTime() -itemMotionInfo.timePoint.getTime();
            float diffSecond = (float) diffTime/(float) 1000;
            int height = new BigDecimal(diffSecond * secondHeight).intValue();

            int marginTop = measureMarginTop(itemMotionInfo, itemViewHeight);
            int marginBottom = measureMarginBottom(height, itemViewHeight, marginTop);

            Button buttonTpl = null;
            switch (itemMotionInfo.zone){
                case zone1:
                    buttonTpl = holder.axisMarkRed;
                    break;
                case zone2:
                    buttonTpl = holder.axisMarkGreen;
                    break;
                case zone3:
                    buttonTpl = holder.axisMarkBlue;
                    break;
            }

            RelativeLayout.LayoutParams  paramsTpl = (RelativeLayout.LayoutParams) buttonTpl.getLayoutParams();
            Button showBtn = new Button(context);
            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(buttonTpl.getLayoutParams());

            params.addRule(RelativeLayout.RIGHT_OF, paramsTpl.getRule(RelativeLayout.RIGHT_OF));
            params.setMargins(params.leftMargin, (marginTop+dotHeight/2), params.rightMargin, marginBottom==0?params.bottomMargin:marginBottom);
            params.height=height;
            params.leftMargin=paramsTpl.leftMargin;
            showBtn.setLayoutParams(params);

            showBtn.setBackground(buttonTpl.getBackground());
            ((ViewGroup)buttonTpl.getParent()).addView(showBtn);
            ((ViewGroup)buttonTpl.getParent()).bringChildToFront(showBtn);
        }
    };

    private int measureMarginTop(AxisMotionBean.ItemMotionInfo motion, float itemHeight){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(motion.timePoint);

        int minute = calendar.get(Calendar.MINUTE);

        float oneMinuteHeight =  itemHeight/(float) 60;
        int marginTop = new BigDecimal(minute* oneMinuteHeight).intValue();
        return marginTop;
    }

    private int measureMarginBottom(float markHeight, float itemHeight, float marginTop){
        if ((marginTop + markHeight)>itemHeight){
            return (int) (marginTop+markHeight - itemHeight);
        }
        return 0;
    }

    private static class ViewHolder{
        public TextView axisDotText;
        public Button axisMarkRed;
        public Button axisMarkBlue;
        public Button axisMarkGreen;
        public View convertView;
        public ImageView axisDot;
        private View bgView;
    }
}
