package com.sengled.media.player.event;

import com.sengled.media.player.entity.Lives;

import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */
public class LivesDataRefreshEvent {

    private List<Lives> lives;

    public LivesDataRefreshEvent(List<Lives> lives) {
        this.lives = lives;
    }

    public List<Lives> getLives(){
        return this.lives;
    }
}
