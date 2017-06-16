package com.sengled.media.player.widget.timeaxis;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/5/25.
 */
public class AxisMotion {

    private long deviceId;
    private Date timePoint;
    private Date endPoint;
    private String motionType;
    private ZoneType zone;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public Date getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(Date timePoint) {
        this.timePoint = timePoint;
    }

    public Date getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Date endPoint) {
        this.endPoint = endPoint;
    }

    public String getMotionType() {
        return motionType;
    }

    public void setMotionType(String motionType) {
        this.motionType = motionType;
    }

    public ZoneType getZone() {
        return zone;
    }

    public void setZone(ZoneType zone) {
        this.zone = zone;
    }

    public static enum ZoneType{
        @SerializedName("1")
        zone1(1),

        @SerializedName("2")
        zone2(2),

        @SerializedName("3")
        zone3(3);

        private final int value;

        ZoneType(int type) {
            this.value = type;
        }
    }
}

