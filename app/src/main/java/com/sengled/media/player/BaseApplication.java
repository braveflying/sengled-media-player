package com.sengled.media.player;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.sengled.media.player.common.CustomFont;

/**
 * Created by admin on 2017/5/11.
 */
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new CustomFont());
    }
}
