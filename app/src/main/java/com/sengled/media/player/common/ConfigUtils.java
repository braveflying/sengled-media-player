package com.sengled.media.player.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2017/6/30.
 */
public class ConfigUtils {

    private static ConfigUtils configUtils;
    private SharedPreferences preferences;

    private ConfigUtils(Context context){
        preferences = context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static ConfigUtils getInstance(Context context){
        if (configUtils == null){
            synchronized (ConfigUtils.class){
                if (configUtils == null){
                    configUtils = new ConfigUtils(context);
                }
            }
        }
        return configUtils;
    }

    public  boolean saveConfig(String key, String value){
        return preferences.edit().putString(key, value).commit();
    }

    public String getConfig(String key){
        return preferences.getString(key, null);
    }
}
