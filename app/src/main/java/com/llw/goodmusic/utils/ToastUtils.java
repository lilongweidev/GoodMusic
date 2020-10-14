package com.llw.goodmusic.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    /**
     * 长消息
     *
     * @param context 上下文参数
     * @param llw     内容
     */
    public static void longToast(Context context, CharSequence llw) {
        Toast.makeText(context.getApplicationContext(), llw, Toast.LENGTH_LONG).show();
    }

    /**
     * 短消息
     *
     * @param context 上下文参数
     * @param llw     内容
     */
    public static void shortToast(Context context, CharSequence llw) {
        Toast.makeText(context.getApplicationContext(), llw, Toast.LENGTH_SHORT).show();
    }
}
