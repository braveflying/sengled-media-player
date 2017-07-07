package com.sengled.media.player.entity;

/**
 * Created by admin on 2017/6/30.
 */
public class PlaybackDto {
    private String uri;

    private String time;

    private int duration;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
