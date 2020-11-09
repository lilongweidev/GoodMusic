package com.llw.goodmusic;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import com.llw.goodmusic.basic.ActivityManager;
import com.llw.goodmusic.basic.BasicApplication;
import org.litepal.LitePal;

/**
 * 项目管理
 *
 * @author llw
 */
public class MusicApplication extends BasicApplication {

    /**
     * 应用实例
     */
    public static MusicApplication musicApplication;
    private static Context context;
    private static ActivityManager activityManager;

    public static Context getMyContext() {
        return musicApplication == null ? null : musicApplication.getApplicationContext();
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
        musicApplication = this;
        //初始化
        LitePal.initialize(this);
    }


    public static ActivityManager getActivityManager() {
        return activityManager;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
