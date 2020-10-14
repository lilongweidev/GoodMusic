package com.llw.goodmusic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 日期时间工具类
 * @author llw
 */
public class DateTimeUtils {

    /**
     * 获取当前完整的日期和时间
     * @return
     */
    public static String getNowDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getNowDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String getNowTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间不包含秒
     * @return
     */
    public static String getNowTimeM(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    /**
     * 转换当前时间不包含时
     * @param oldTime
     * @return
     */
    public static String parseTime(int oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }

    /**
     * 获取当前日期(精确到毫秒)
     * @return
     */
    public static String getNowTimeDetail(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        return sdf.format(new Date());
    }

    /**
     * 将时间戳转化为对应的时间(10位或者13位都可以)
     * @param time
     * @return
     */
    public static String formatTime(long time){
        String times = null;
        if(String.valueOf(time).length()>10){
            // 10位的秒级别的时间戳
            times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time * 1000));
        }else {
            // 13位的秒级别的时间戳
            times  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        }
        return times;
    }

    /**
     * 将时间字符串转为时间戳字符串
     * @param time
     * @return
     */
    public static String getStringTimestamp(String time) {
        String timestamp = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long longTime = sdf.parse(time).getTime()/1000;
            timestamp = Long.toString(longTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 将长整型时间转为为分秒
     * @param millionSeconds
     * @return
     */
    public static String time(long millionSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }


    public static StringBuilder mFormatBuilder = new StringBuilder();
    public static Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    /**
     * 将长度转换为时间
     * @param timeMs
     * @return
     */
    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }
}
