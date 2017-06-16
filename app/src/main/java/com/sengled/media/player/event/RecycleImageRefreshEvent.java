package com.sengled.media.player.event;

import java.util.List;

/**
 * Created by admin on 2017/4/14.
 */
public class RecycleImageRefreshEvent {

    private List<String> imagePathList;

    public RecycleImageRefreshEvent(List<String> imagePathList) {
        this.imagePathList = imagePathList;
    }

    public List<String> getImagePathList() {
        return imagePathList;
    }
}
