package com.llw.goodmusic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.llw.goodmusic.R;
import com.llw.goodmusic.basic.BasicActivity;

/**
 * 主页面
 *
 * @author llw
 */
public class MainActivity extends BasicActivity {


    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    public void onClick(View view) {
        startActivity(new Intent(context,LocalMusicActivity.class));
    }
}
