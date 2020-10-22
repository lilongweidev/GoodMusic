package com.llw.goodmusic.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.llw.goodmusic.R;
import com.llw.goodmusic.adapter.MusicListAdapter;
import com.llw.goodmusic.basic.BasicActivity;
import com.llw.goodmusic.bean.Song;
import com.llw.goodmusic.databinding.ActivityLocalMusicBinding;
import com.llw.goodmusic.utils.BLog;
import com.llw.goodmusic.utils.Constant;
import com.llw.goodmusic.utils.MusicUtils;
import com.llw.goodmusic.utils.SPUtils;
import com.llw.goodmusic.view.MusicRoundProgressView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地音乐
 *
 * @author llw
 */
public class LocalMusicActivity extends BasicActivity implements MediaPlayer.OnCompletionListener {


    private static final String TAG = "LocalMusic";

    private Toolbar toolbar;
    /**
     * 歌曲列表
     */
    private RecyclerView rvMusic;
    /**
     * 扫描歌曲布局
     */
    private LinearLayout layScanMusic;
    /**
     * 歌曲适配器
     */
    private MusicListAdapter mAdapter;
    /**
     * 歌曲列表
     */
    private List<Song> mList = new ArrayList<>();

    /**
     * 上一次点击的位置
     */
    private int oldPosition = -1;

    /**
     * 定位当前音乐按钮
     */
    private MaterialButton btnLocationPlayMusic;
    /**
     * 底部logo图标，点击之后弹出当前播放歌曲详情页
     */
    private ShapeableImageView ivLogo;
    /**
     * 底部当前播放歌名
     */
    private MaterialTextView tvSongName;
    /**
     * 底部当前歌曲控制按钮, 播放和暂停
     */
    private MaterialButton btnPlay;
    /**
     * 音频播放器
     */
    private MediaPlayer mediaPlayer;
    /**
     * 记录当前播放歌曲的位置
     */
    public int mCurrentPosition = -1;

    /**
     * 自定义进度条
     */
    private MusicRoundProgressView musicProgress;

    /**
     * 音乐进度间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    /**
     * 图片动画
     */
    private ObjectAnimator logoAnimation;


    @Override
    public void initData(Bundle savedInstanceState) {
        initView();
    }

    /**
     * 初始化控件级页面的业务逻辑
     */
    private void initView() {
        ActivityLocalMusicBinding binding = DataBindingUtil.setContentView(context, R.layout.activity_local_music);
        toolbar = binding.toolbar;
        rvMusic = binding.rvMusic;
        layScanMusic = binding.layScanMusic;
        btnLocationPlayMusic = binding.btnLocationPlayMusic;
        //新增 第三篇文章
        ivLogo = binding.ivLogo;
        tvSongName = binding.tvSongName;
        btnPlay = binding.btnPlay;
        musicProgress = binding.musicProgress;

        Back(toolbar);

        //当进入页面时发现有缓存数据时，则隐藏扫描布局，直接获取本地数据。
        if (SPUtils.getBoolean(Constant.LOCAL_MUSIC_DATA, false, context)) {
            //省去一个点击扫描的步骤
            layScanMusic.setVisibility(View.GONE);
            permissionsRequest();
        }

        rvMusic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //暂停
                    showLocationMusic(false);
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //滑动
                    showLocationMusic(true);
                }
            }
        });


        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        logoAnimation = ObjectAnimator.ofFloat(ivLogo, "rotation", 0.0f, 360.0f);
        logoAnimation.setDuration(3000);
        logoAnimation.setInterpolator(new LinearInterpolator());
        logoAnimation.setRepeatCount(-1);
        logoAnimation.setRepeatMode(ObjectAnimator.RESTART);
    }

    /**
     * 显示定位当前音乐图标
     */
    private void showLocationMusic(boolean isScroll) {
        //先判断是否存在播放音乐
        if (oldPosition != -1) {
            if (isScroll) {
                //滑动
                btnLocationPlayMusic.setVisibility(View.VISIBLE);
            } else {
                //延时隐藏  Android 11（即API 30:Android R）弃用了Handler默认的无参构造方法,所以传入了Looper.myLooper()
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnLocationPlayMusic.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_local_music;
    }

    /**
     * 动态权限请求
     */
    private void permissionsRequest() {

        PermissionX.init(this).permissions(
                //写入文件
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                        scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白");
                    }
                })
                .setDialogTintColor(R.color.white, R.color.app_color)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {
                            //获取本地音乐列表
                            getMusicList();
                        } else {
                            show("您拒绝了如下权限：" + deniedList);
                        }
                    }
                });
    }

    /**
     * 页面点击事件
     *
     * @param view 控件
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_local_music:
                //扫描本地音乐
                permissionsRequest();
                break;
            case R.id.btn_location_play_music:
                //定位当前播放歌曲
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvMusic.getLayoutManager();
                linearLayoutManager.scrollToPositionWithOffset(oldPosition, 0);
                break;
            case R.id.btn_play:
                //控制音乐 播放和暂停
                if (mediaPlayer == null) {
                    //没有播放过音乐 ,点击之后播放第一首
                    oldPosition = 0;
                    mCurrentPosition = 0;
                    mList.get(mCurrentPosition).setCheck(true);
                    mAdapter.changeState();
                    changeSong(mCurrentPosition);
                } else {
                    //播放过音乐  暂停或者播放
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        btnPlay.setIcon(getDrawable(R.mipmap.icon_play));
                        btnPlay.setIconTint(getColorStateList(R.color.white));
                        logoAnimation.pause();
                    } else {
                        mediaPlayer.start();
                        btnPlay.setIcon(getDrawable(R.mipmap.icon_pause));
                        btnPlay.setIconTint(getColorStateList(R.color.gold_color));
                        logoAnimation.resume();
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * 获取音乐列表
     */
    private void getMusicList() {

        //清除列表数据
        mList.clear();
        //将扫描到的音乐赋值给音乐列表
        mList = MusicUtils.getMusicData(this);

        if (mList != null && mList.size() > 0) {
            //是否有缓存歌曲
            SPUtils.putBoolean(Constant.LOCAL_MUSIC_DATA, true, context);
            layScanMusic.setVisibility(View.GONE);
            //显示本地音乐
            showLocalMusicData();
        } else {
            show("兄嘚，你是一无所有啊~");
        }

    }

    /**
     * 显示本地音乐数据
     */
    private void showLocalMusicData() {
        //指定适配器的布局和数据源
        mAdapter = new MusicListAdapter(R.layout.item_music_rv_list, mList);
        //线性布局管理器，可以设置横向还是纵向，RecyclerView默认是纵向的，所以不用处理,如果不需要设置方向，代码还可以更加的精简如下
        rvMusic.setLayoutManager(new LinearLayoutManager(this));
        //设置适配器
        rvMusic.setAdapter(mAdapter);

        //item的点击事件
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.item_music) {

                    //控制当前播放位置
                    playPositionControl(position);

                    mCurrentPosition = position;
                    changeSong(mCurrentPosition);

                }
            }
        });
    }

    /**
     * 控制播放位置
     *
     * @param position
     */
    private void playPositionControl(int position) {
        if (oldPosition == -1) {
            //未点击过 第一次点击
            oldPosition = position;
            mList.get(position).setCheck(true);
        } else {
            //大于 1次
            if (oldPosition != position) {
                mList.get(oldPosition).setCheck(false);
                mList.get(position).setCheck(true);
                //重新设置位置，当下一次点击时position又会和oldPosition不一样
                oldPosition = position;
            }
        }
        //刷新数据
        mAdapter.changeState();

    }

    /**
     * 切换歌曲
     */
    private void changeSong(int position) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            //监听音乐播放完毕事件，自动下一曲
            mediaPlayer.setOnCompletionListener(this);
        }

        try {
            //切歌前先重置，释放掉之前的资源
            mediaPlayer.reset();
            BLog.i(TAG, mList.get(position).path);
            //设置播放音频的资源路径
            mediaPlayer.setDataSource(mList.get(position).path);
            //设置播放的歌名和歌手
            tvSongName.setText(mList.get(position).song + " - " + mList.get(position).singer);
            //如果内容超过控件，则启用跑马灯效果
            tvSongName.setSelected(true);
            //开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();
            //开始播放音频
            mediaPlayer.start();

            musicProgress.setProgress(0, mediaPlayer.getDuration());
            //更新进度
            updateProgress();

            //播放按钮控制
            if (mediaPlayer.isPlaying()) {
                btnPlay.setIcon(getDrawable(R.mipmap.icon_pause));
                btnPlay.setIconTint(getColorStateList(R.color.gold_color));
                logoAnimation.resume();
            } else {

                btnPlay.setIcon(getDrawable(R.mipmap.icon_play));
                btnPlay.setIconTint(getColorStateList(R.color.white));
                logoAnimation.pause();
            }

            //图片旋转动画
            logoAnimation.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 播放完成之后自动下一曲
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {

        //停止旋转并重置
        logoAnimation.end();
        int position = -1;
        if (mList != null) {
            if (mCurrentPosition == mList.size() - 1) {
                //当前为最后一首歌时,则切换到列表的第一首歌
                position = mCurrentPosition = 0;
            } else {
                position = ++mCurrentPosition;
            }
        }

        //移动播放位置
        playPositionControl(position);
        //切歌
        changeSong(position);

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            int progress = mediaPlayer.getCurrentPosition();
            musicProgress.setProgress(progress, mediaPlayer.getDuration());

            //更新进度
            updateProgress();
            return true;
        }
    });

    /**
     * 更新进度
     */
    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        // 获取一个现成的消息
        Message msg = Message.obtain();
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = mediaPlayer.getCurrentPosition();
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }
}
