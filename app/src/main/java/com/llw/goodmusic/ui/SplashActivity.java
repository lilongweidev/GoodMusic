package com.llw.goodmusic.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.llw.goodmusic.R;
import com.llw.goodmusic.databinding.ActivityLocalMusicBinding;
import com.llw.goodmusic.databinding.ActivitySplashBinding;
import com.llw.goodmusic.utils.BLog;

/**
 * 启动页
 *
 * @author llw
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * 位移动画
     */
    private TranslateAnimation translateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        ActivitySplashBinding binding = DataBindingUtil.setContentView(SplashActivity.this, R.layout.activity_splash);
        TextView tvTranslate = binding.tvTranslate;

        tvTranslate.post(new Runnable() {

            @Override
            public void run() {
                //通过post拿到的tvTranslate.getWidth()不会为0。
                translateAnimation = new TranslateAnimation(0, tvTranslate.getWidth(), 0, 0);
                translateAnimation.setDuration(1000);
                translateAnimation.setFillAfter(true);
                tvTranslate.startAnimation(translateAnimation);

                //动画监听
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //动画结束时跳转到主页面
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

    }

    private class listener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
