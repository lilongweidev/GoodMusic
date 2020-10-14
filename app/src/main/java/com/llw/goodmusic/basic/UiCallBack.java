package com.llw.goodmusic.basic;

import android.os.Bundle;

/**
 * UI回调接口
 *
 * @author llw
 */
public interface UiCallBack {

    /**
     * 初始化savedInstanceState
     *
     * @param savedInstanceState
     */
    void initBeforeView(Bundle savedInstanceState);

    /**
     * 初始化数据 相当于onCreate
     *
     * @param savedInstanceState
     */
    void initData(Bundle savedInstanceState);

    /**
     * 绑定布局
     *
     * @return
     */
    int getLayoutId();
}
