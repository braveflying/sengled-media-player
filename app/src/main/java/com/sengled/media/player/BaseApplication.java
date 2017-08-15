package com.sengled.media.player;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.google.gson.Gson;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.sengled.media.player.common.ConfigUtils;
import com.sengled.media.player.common.Const;
import com.sengled.media.player.common.CustomFont;
import com.sengled.media.player.entity.UserInfoDto;
import com.sengled.media.player.service.LogService;

/**
 * Created by admin on 2017/5/11.
 */
public class BaseApplication extends Application{
    CrashHandler handler = null;
    private UserInfoDto userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new CustomFont());

        handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());

        loadUserInfo();
        startService(new Intent(this, LogService.class));
    }

    private void loadUserInfo(){
        this.userInfo = new Gson().fromJson(ConfigUtils.getInstance(this).getConfig(Const.USER_INFO),UserInfoDto.class);
    }

    public UserInfoDto getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoDto userInfo) {
        this.userInfo = userInfo;
    }
}
