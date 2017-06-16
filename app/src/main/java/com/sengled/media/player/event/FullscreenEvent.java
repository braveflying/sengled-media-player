package com.sengled.media.player.event;

import android.view.ViewGroup;

/**
 * Created by admin on 2017/4/13.
 */
public class FullscreenEvent {

    private ViewGroup itemContainer;
    private int position;

    public FullscreenEvent(ViewGroup itemContainer,int position) {
        this.itemContainer = itemContainer;
        this.position = position;
    }

    public ViewGroup getItemContainer() {
        return itemContainer;
    }

    public int getPosition() {
        return position;
    }
}
