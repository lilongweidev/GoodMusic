package com.llw.goodmusic.basic;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class MyActivityManager {


    private static WeakReference<Activity> sCurrentActivityWeakRef;

    private static Object activityUpdateLock = new Object();

    public static Activity getCurrentActivity() {
        Activity currentActivity = null;
        synchronized (activityUpdateLock){
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef.get();
            }
        }
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
        synchronized (activityUpdateLock){
            sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
        }

    }

}
