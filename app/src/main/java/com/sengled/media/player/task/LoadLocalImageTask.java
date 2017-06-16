package com.sengled.media.player.task;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import com.sengled.media.player.event.RecycleImageRefreshEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/14.
 */
public class LoadLocalImageTask extends AsyncTask<String, Void,List<String>> {

    @Override
    protected List<String> doInBackground(String... params) {

        String imageDir = params[0];
        List<String> imagePathList = new ArrayList<>();
        File file = new File(imageDir);
        if (file.isDirectory()){
            for (File item :file.listFiles()){
                if (item.getName().endsWith("jpg")){
                    imagePathList.add(item.getAbsolutePath());
                }
            }
        }
        return imagePathList;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        EventBus.getDefault().post(new RecycleImageRefreshEvent(strings));
    }
}
