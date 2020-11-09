package com.llw.goodmusic.basic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.llw.goodmusic.service.MusicService;
import com.llw.goodmusic.utils.BLog;

import static com.llw.goodmusic.utils.Constant.CLOSE;
import static com.llw.goodmusic.utils.Constant.NEXT;
import static com.llw.goodmusic.utils.Constant.PLAY;
import static com.llw.goodmusic.utils.Constant.PREV;

/**
 * 基础Activity
 *
 * @author llw
 */
public abstract class BasicActivity extends AppCompatActivity implements UiCallBack {

    private static final String TAG = "BasicActivity";

    /**
     * 快速点击的时间间隔
     */
    private static final int FAST_CLICK_DELAY_TIME = 500;
    /**
     * 最后点击的时间
     */
    private static long lastClickTime;
    /**
     * 上下文参数
     */
    protected Activity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeView(savedInstanceState);
        this.context = this;

        //添加继承这个BaseActivity的Activity
        BasicApplication.getActivityManager().addActivity(this);
        //绑定布局id
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }
        //初始化数据
        initData(savedInstanceState);

    }

    @Override
    public void initBeforeView(Bundle savedInstanceState) {

    }

    /**
     * 返回
     *
     * @param toolbar
     */
    protected void Back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
                if (!isFastClick()) {
                    context.finish();
                }
            }
        });
    }

    /**
     * 两次点击间隔不能少于500ms  防止多次点击
     *
     * @return flag
     */
    protected static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;

        return flag;
    }

    /**
     * 消息提示
     *
     * @param llw
     */
    protected void show(CharSequence llw) {
        Toast.makeText(context, llw, Toast.LENGTH_SHORT).show();
    }

}
