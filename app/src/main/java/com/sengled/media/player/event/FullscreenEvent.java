package com.sengled.media.player.event;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 2017/4/13.
 */
public class FullscreenEvent {

    private ViewGroup itemParentContainer;
    private ViewGroup itemContainer;

    public FullscreenEvent(ViewGroup itemParentContainer,ViewGroup itemContainer) {
        this.itemContainer = itemContainer;
        this.itemParentContainer = itemParentContainer;
    }

    public ViewGroup getItemContainer() {
        return itemContainer;
    }

    public ViewGroup getItemParentContainer() {
        return itemParentContainer;
    }
}
