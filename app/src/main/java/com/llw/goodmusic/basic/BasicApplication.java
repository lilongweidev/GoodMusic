package com.llw.goodmusic.basic;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/**
 * 工程管理
 *
 * @author llw
 */
public class BasicApplication extends Application {
    private static ActivityManager activityManager;
    private static BasicApplication application;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //声明Activity管理
        activityManager = new ActivityManager();
        context = getApplicationContext();
        application = this;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public static ActivityManager getActivityManager() {
        return activityManager;
    }

    /**
     * 内容提供器
     * @return
     */
    public static Context getContext() {
        return context;
    }

    public static BasicApplication getApplication() {
        return application;
    }
}

