package com.sengled.media.player.widget.timeaxis;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/5/25.
 */
public class AxisMotionBean {

    private String timeMark;

    private long deviceId;

    private List<ItemMotionInfo> motionInfoList;

    public String getTimeMark() {
        return timeMark;
    }

    public void setTimeMark(String timeMark) {
        this.timeMark = timeMark;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public List<ItemMotionInfo> getMotionInfoList() {
        return motionInfoList;
    }

    public void setMotionInfoList(List<ItemMotionInfo> motionInfoList) {
        this.motionInfoList = motionInfoList;
    }

    public static class ItemMotionInfo{
        public Date timePoint;
        public Date endPoint;
        public String motionType;
        public ZoneType zone;
    }

    public static enum ZoneType{
        zone1(1),zone2(2),zone3(3);

        private final int value;

        ZoneType(int type) {
            this.value = type;
        }
    }
}
