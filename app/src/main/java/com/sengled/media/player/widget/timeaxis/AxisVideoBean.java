package com.sengled.media.player.widget.timeaxis;

import java.util.Date;

/**
 * Created by admin on 2017/5/31.
 */
public class AxisVideoBean {

    private Date startTime;
    private Date endTime;
    private String videoPath;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
