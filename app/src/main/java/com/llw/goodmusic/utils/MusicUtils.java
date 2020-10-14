package com.llw.goodmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.llw.goodmusic.bean.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐扫描工具
 *
 * @author llw
 */
public class MusicUtils {
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                //歌曲名称
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌手
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //专辑名
                song.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲路径
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲时长
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲大小
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.song.contains("-")) {
                        String[] str = song.song.split("-");
                        song.singer = str[0];
                        song.song = str[1];
                    }
                    list.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }
        return list;
    }

    /**
     * 专辑图片
     * @param context
     * @return
     */
    private static String imgUrl(Context context) {
        String album_art = null;
        String[] mediaColumns1 = new String[]{MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ALBUM};

        Cursor cursor1 = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, mediaColumns1, null, null,
                null);

        if (cursor1 != null) {
            cursor1.moveToFirst();
            do {
                album_art = cursor1.getString(0);
                if (album_art != null) {
                    BLog.d("ALBUM_ART", album_art);
                }

                String album = cursor1.getString(1);
                if (album != null) {
                    BLog.d("ALBUM_ART", album);
                }

            } while (cursor1.moveToNext());

            cursor1.close();
        }
        return album_art;
    }

}