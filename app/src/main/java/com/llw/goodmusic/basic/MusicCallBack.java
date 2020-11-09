package com.llw.goodmusic.basic;

/**
 * 音乐状态回调
 *
 * @author llw
 */
public interface MusicCallBack {

    void notificationPlayListener();

    void notificationPrevListener();

    void notificationNextListener();

    void notificationPauseListener();
}
