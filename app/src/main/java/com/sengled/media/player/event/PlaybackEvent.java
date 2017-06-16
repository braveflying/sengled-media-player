package com.sengled.media.player.event;

import com.sengled.media.player.widget.timeaxis.AxisMotion;
import com.sengled.media.player.widget.timeaxis.AxisMotionBean;
import com.sengled.media.player.widget.timeaxis.AxisVideo;

import java.util.List;

/**
 * Created by admin on 2017/5/26.
 */
public class PlaybackEvent {

    public static class UpdateMarkEvent{
        public List<String> dateList;
    }

    public static class FetchPlaybackListEvent{
        public List<AxisMotionBean> playbackList;
    }

    public static class UpdateAxisVideoEvent{
        public List<AxisVideo> axisVideos;

        public UpdateAxisVideoEvent(List<AxisVideo> axisVideos) {
            this.axisVideos = axisVideos;
        }
    }

    public static class UpdateAxisMotionEvent{
        public List<AxisMotion> axisMotions;

        public UpdateAxisMotionEvent(List<AxisMotion> axisMotions) {
            this.axisMotions = axisMotions;
        }
    }
}
