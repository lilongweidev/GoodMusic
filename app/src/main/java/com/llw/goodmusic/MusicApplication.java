package com.llw.goodmusic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;

import com.llw.goodmusic.basic.ActivityManager;
import com.llw.goodmusic.basic.BasicApplication;

/**
 * 项目管理
 *
 * @author llw
 */
public class MusicApplication extends BasicApplication {

    /**
     * 应用实例
     */
    public static MusicApplication weatherApplication;
    private static Context context;
    private static ActivityManager activityManager;

    private static Activity sActivity;

    public static Context getMyContext() {
        return weatherApplication == null ? null : weatherApplication.getApplicationContext();
    }

    private Handler myHandler;

    public Handler getMyHandler() {
        return myHandler;
    }

    public void setMyHandler(Handler handler) {
        myHandler = handler;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        activityManager = new ActivityManager();
        context = getApplicationContext();
        weatherApplication = this;
    }


    public static ActivityManager getActivityManager() {
        return activityManager;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
