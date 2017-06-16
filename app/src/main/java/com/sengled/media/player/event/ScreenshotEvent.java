package com.sengled.media.player.event;

import android.graphics.Bitmap;

/**
 * Created by admin on 2017/4/13.
 */
public class ScreenshotEvent {

    private Bitmap bitmap;

    public ScreenshotEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
