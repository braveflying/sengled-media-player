package com.sengled.media.player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.sengled.media.player.common.Utils;
import com.sengled.media.player.fragment.AboutFragment;
import com.sengled.media.player.fragment.ImageSwitchFragment;
import com.sengled.media.player.fragment.ImageTransitionFragment;
import com.sengled.media.player.fragment.SengledAboutFragment;
import com.sengled.media.player.fragment.StaggerImageFragment;
import com.sengled.media.player.fragment.VideoPreviewFragment;
import com.sengled.media.player.fragment.VideoRealtimeFragment;

public class MainActivity extends AppCompatActivity {

    // 抽屉菜单对象
    private ActionBarDrawerToggle drawerbar;
    public DrawerLayout drawerLayout;
    private RelativeLayout main_left_drawer_layout;
    private Toolbar toolbar;
    private ListView menuList;
    private Fragment videoPreviewFragment,aboutFragment,imageSwitchFragment,videoRealtimeFragment,staggerImageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        Utils.verifyStoragePermissions(this);  //请求访问SD 卡权限

        initLayout();
        initEvent();
        initData();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(IconicsContextWrapper.wrap(base));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("BackStackEntryCount"+getSupportFragmentManager() .getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount()>0){
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (videoPreviewFragment == null) {
            videoPreviewFragment = new VideoPreviewFragment();
            transaction.add(R.id.main_content_frame_parent, videoPreviewFragment);
        } else {
            transaction.show(videoPreviewFragment);
        }
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        //toolbar.setTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        menuList = (ListView) findViewById(R.id.media_drawer_left_menu_list);
        toolbar = (Toolbar) findViewById(R.id.media_drawer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //设置菜单内容之外其他区域的背景色
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        //左边菜单
        main_left_drawer_layout = (RelativeLayout) findViewById(R.id.main_left_drawer_layout);
    }

    public void initData(){
        String[] mMenuTitles = getResources().getStringArray(R.array.media_menu_array);
        ArrayAdapter menuArray = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mMenuTitles);
        menuList.setAdapter(menuArray);
    }

    //设置开关监听
    private void initEvent() {
        drawerbar = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            //菜单打开
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
            // 菜单关闭
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerbar.syncState();
        drawerLayout.addDrawerListener(drawerbar);
        menuList.setOnItemClickListener(new MenuListItemClickListener());
    }

    //左边菜单开关事件
    public void openLeftLayout(View view) {
        if (drawerLayout.isDrawerOpen(main_left_drawer_layout)) {
            drawerLayout.closeDrawer(main_left_drawer_layout);
        } else {
            drawerLayout.openDrawer(main_left_drawer_layout);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getSupportActionBar().isShowing()){
            getSupportActionBar().hide();
        }else {
            getSupportActionBar().show();
        }
        super.onConfigurationChanged(newConfig);
        drawerbar.onConfigurationChanged(newConfig);
    }

    /**
     * 菜单点击事件
     */
    class MenuListItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideFragment(transaction);
            switch (position){
                case 0:
                    //// TODO: 2017/4/12  视频预览
                    if (videoPreviewFragment == null) {
                        videoPreviewFragment = new VideoPreviewFragment();
                        transaction.add(R.id.main_content_frame_parent, videoPreviewFragment);
                    } else {
                        transaction.show(videoPreviewFragment);
                    }
                    break;
                case 1: //实时预览
                    if (videoRealtimeFragment == null) {
                        videoRealtimeFragment = new VideoRealtimeFragment();
                        transaction.add(R.id.main_content_frame_parent, videoRealtimeFragment);
                    } else {
                        transaction.show(videoRealtimeFragment);
                    }
                    break;
                case 2:
                    //// TODO: 2017/4/12  我的截图
                    if (imageSwitchFragment == null) {
                        imageSwitchFragment = new ImageSwitchFragment();
                        transaction.add(R.id.main_content_frame_parent, imageSwitchFragment);
                    } else {
                        transaction.show(imageSwitchFragment);
                    }
                    break;
                case 3:
                    if (staggerImageFragment == null) {
                        staggerImageFragment = new StaggerImageFragment();
                        transaction.add(R.id.main_content_frame_parent, staggerImageFragment);
                    } else {
                        transaction.show(staggerImageFragment);
                    }
                    break;
                case 4:
                    //// TODO: 2017/4/12  我的设备
                    break;
                case 5:
                    //// TODO: 2017/4/12  关于
                    if (aboutFragment == null) {
                        aboutFragment = new AboutFragment();
                        transaction.add(R.id.main_content_frame_parent, aboutFragment);
                    } else {
                        transaction.show(aboutFragment);
                    }
                    break;
                default:
                    //// TODO: 2017/4/12  默认
            }
            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();
            toolbar.setTitle(((TextView)view).getText());
            openLeftLayout(view);
        }

        private void hideFragment(FragmentTransaction transaction){
            if (videoPreviewFragment != null){
                transaction.hide(videoPreviewFragment);
            }

            if (videoRealtimeFragment != null){
                transaction.hide(videoRealtimeFragment);
            }

            if (aboutFragment != null){
                transaction.hide(aboutFragment);
            }

            if (imageSwitchFragment != null){
                transaction.hide(imageSwitchFragment);
            }

            if (staggerImageFragment != null){
                transaction.hide(staggerImageFragment);
            }

            Fragment imageTransitionFragment = getSupportFragmentManager().findFragmentByTag(ImageTransitionFragment.TAG);
            if (imageTransitionFragment != null){
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
    }

}
