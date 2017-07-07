package com.sengled.media.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sengled.media.player.R;
import com.sengled.media.player.widget.timeaxis.AxisVideo;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/7/5.
 */
public class PlaybackControlAdapter extends RecyclerView.Adapter<PlaybackControlAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<AxisVideo> dataList;

    public PlaybackControlAdapter(Context mContext, List<AxisVideo> dataList) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.dataList = dataList;

    }

    @Override
    public PlaybackControlAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.media_video_playback_new_item_layout, parent, false);
        PlaybackControlAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaybackControlAdapter.ViewHolder holder, int position) {

        AxisVideo axisVideo = dataList.get(position);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(axisVideo.getStartTime());
        holder.timeDot.setText(String.format("%d:00",startCalendar.get(Calendar.HOUR_OF_DAY)));
    }

    @Override
    public int getItemCount() {

        Set<Integer> timePoint = new HashSet<>();
        //计算有视频的时间点数
        Calendar vStartCalendar = Calendar.getInstance();
        Calendar vEndCalendar = Calendar.getInstance();
        for (AxisVideo AxisVideo : dataList) {
            vStartCalendar.setTime(AxisVideo.getStartTime());
            vStartCalendar.set(Calendar.MINUTE,0);
            vStartCalendar.set(Calendar.SECOND,0);
            vEndCalendar.setTime(AxisVideo.getEndTime());

            do{
                timePoint.add(vStartCalendar.get(Calendar.HOUR_OF_DAY));
                vStartCalendar.add(Calendar.HOUR_OF_DAY,1);
            }while (vStartCalendar.before(vEndCalendar));
        }

        AxisVideo videoData = null;
        for (int i = 0; i <= 23; i++) {
            if (!timePoint.contains(i)){
                vStartCalendar.set(Calendar.HOUR_OF_DAY, i);
                videoData = new AxisVideo();
                videoData.setStartTime(vStartCalendar.getTime());
                dataList.add(videoData);
            }
        }

        Collections.sort(dataList, new Comparator<AxisVideo>() {
            @Override
            public int compare(AxisVideo o1, AxisVideo o2) {
                return o1.getStartTime().before(o2.getStartTime())?1:-1;
            }
        });

        return dataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View timeLine;
        public TextView timeDot;
        public ViewHolder(View itemView) {
            super(itemView);
            timeLine = itemView.findViewById(R.id.media_playback_timeline);
            timeDot = (TextView) itemView.findViewById(R.id.time_dot);
        }
    }
}
