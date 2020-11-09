package com.llw.goodmusic.basic;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理所有的Activity
 *
 * @author llw
 */
public class ActivityManager {
    /**
     * 保存所有创建的Activity
     */
    private List<Activity> allActivities = new ArrayList<>();


    /**
     * 弱引用
     */
    private static WeakReference<Activity> activityWeakReference;

    private static Object activityUpdateLock = new Object();


    /**
     * 添加Activity到管理器
     *
     * @param activity activity
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            allActivities.add(activity);
        }
    }


    /**
     * 从管理器移除Activity
     *
     * @param activity activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            allActivities.remove(activity);
        }
    }

    /**
     * 关闭所有Activity
     */
    public void finishAll() {
        for (Activity activity : allActivities) {
            activity.finish();
        }

    }


    /**
     * 得到当前Activity
     * @return
     */
    public static Activity getCurrentActivity() {
        Activity currentActivity = null;
        synchronized (activityUpdateLock){
            if (activityWeakReference != null) {
                currentActivity = activityWeakReference.get();
            }
        }
        return currentActivity;
    }

    /**
     * 设置当前Activity
     * @return
     */
    public static void setCurrentActivity(Activity activity) {
        synchronized (activityUpdateLock){
            activityWeakReference = new WeakReference<Activity>(activity);
        }

    }
}
