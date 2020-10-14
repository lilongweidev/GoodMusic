package com.llw.goodmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * sharepref工具类
 *
 * @author llw
 */
public class SPUtils {
    private static final String NAME = "config";

    public static void putBoolean(String key, boolean value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(String key, String value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue, Context context) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(NAME,
                    Context.MODE_PRIVATE);
            return sp.getString(key, defValue);
        }
        return "";

    }

    public static void putInt(String key, int value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }


    public static int getInt(String key, int defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void remove(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }


}
