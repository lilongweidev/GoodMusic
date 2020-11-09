package com.llw.goodmusic.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.llw.goodmusic.receiver.NotificationClickReceiver;
import com.llw.goodmusic.R;
import com.llw.goodmusic.bean.Song;
import com.llw.goodmusic.livedata.LiveDataBus;
import com.llw.goodmusic.utils.BLog;
import com.llw.goodmusic.utils.MusicUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.llw.goodmusic.utils.Constant.CLOSE;
import static com.llw.goodmusic.utils.Constant.NEXT;
import static com.llw.goodmusic.utils.Constant.PAUSE;
import static com.llw.goodmusic.utils.Constant.PLAY;
import static com.llw.goodmusic.utils.Constant.PREV;
import static com.llw.goodmusic.utils.Constant.PROGRESS;

/**
 * 音乐后台服务
 *
 * @author llw
 */
public class MusicService extends LifecycleService implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "MusicService";

    /**
     * 歌曲间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    /**
     * 歌曲列表
     */
    private static List<Song> mList = new ArrayList<>();

    /**
     * 音乐播放器
     */
    public MediaPlayer mediaPlayer;
    /**
     * 记录播放的位置
     */
    int playPosition = 0;

    /**
     * 通知
     */
    private static Notification notification;
    /**
     * 通知栏视图
     */
    private static RemoteViews remoteViews;
    /**
     * 通知ID
     */
    private int NOTIFICATION_ID = 1;
    /**
     * 通知管理器
     */
    private static NotificationManager manager;
    /**
     * 音乐广播接收器
     */
    private MusicReceiver musicReceiver;

    /**
     * 通知栏控制Activity页面UI
     */
    private LiveDataBus.BusMutableLiveData<String> activityLiveData;

    /**
     * Activity控制通知栏UI
     */
    private LiveDataBus.BusMutableLiveData<String> notificationLiveData;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mList = LitePal.findAll(Song.class);
        //初始化RemoteViews配置
        initRemoteViews();
        //初始化通知
        initNotification();

        //Activity的观察者
        activityObserver();

        //注册动态广播
        registerMusicReceiver();

        activityLiveData = LiveDataBus.getInstance().with("activity_control", String.class);
        BLog.d(TAG, "onCreate");

    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        String channelId = "play_control";
        String channelName = "播放控制";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);

        //点击整个通知时发送广播
        Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //初始化通知
        notification = new NotificationCompat.Builder(this, "play_control")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon_big_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_big_logo))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }


    /**
     * 播放
     */
    public void play(int position) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            //监听音乐播放完毕事件，自动下一曲
            mediaPlayer.setOnCompletionListener(this);
        }

        try {
            //切歌前先重置，释放掉之前的资源
            mediaPlayer.reset();
            playPosition = position;
            //设置播放音频的资源路径
            mediaPlayer.setDataSource(mList.get(position).path);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //显示通知
            updateNotificationShow(position);

            //更新到Activity
            activityLiveData.postValue(PLAY);

            //更新播放进度
            updateProgress();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停/继续 音乐
     */
    public void pauseOrContinueMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            activityLiveData.postValue(PAUSE);
        } else {
            mediaPlayer.start();
            activityLiveData.postValue(PLAY);
        }
        //更改通知栏播放状态
        updateNotificationShow(playPosition);
    }

    /**
     * 关闭音乐
     */
    public void closeMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            //重置
            mediaPlayer.reset();
            //释放
            mediaPlayer.release();
        }
    }

    /**
     * 关闭音乐通知栏
     */
    public void closeNotification() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        manager.cancel(NOTIFICATION_ID);

        activityLiveData.postValue(CLOSE);
    }

    /**
     * 下一首
     */
    public void nextMusic() {
        if (playPosition >= mList.size() - 1) {
            playPosition = 0;
        } else {
            playPosition += 1;
        }
        activityLiveData.postValue(NEXT);
        play(playPosition);
    }

    /**
     * 上一首
     */
    public void previousMusic() {
        if (playPosition <= 0) {
            playPosition = mList.size() - 1;
        } else {
            playPosition -= 1;
        }
        activityLiveData.postValue(PREV);
        play(playPosition);
    }


    /**
     * 获取当前播放位置
     * @return
     */
    public int getPlayPosition() {
        return playPosition;
    }


    /**
     * 初始化自定义通知栏 的按钮点击事件
     */
    private void initRemoteViews() {
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);

        //通知栏控制器上一首按钮广播操作
        Intent intentPrev = new Intent(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, 0);
        //为prev控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
        //为play控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        //为next控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, 0);
        //为close控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent);

    }

    /**
     * 更改通知的信息和UI
     * @param position
     */
    public void updateNotificationShow(int position) {
        //播放状态判断
        if (mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.play_black);
        }
        //封面专辑
        remoteViews.setImageViewBitmap(R.id.iv_album_cover, MusicUtils.getAlbumPicture(this, mList.get(position).getPath(), 0));
        //歌曲名
        remoteViews.setTextViewText(R.id.tv_notification_song_name, mList.get(position).getSong());
        //歌手名
        remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getSinger());
        //发送通知
        manager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 创建通知渠道
     *
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道重要性
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        manager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }


    /**
     * 当前音乐播放完成监听
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        //下一曲
        nextMusic();

    }

    /**
     * 注册动态广播
     */
    private void registerMusicReceiver() {
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(CLOSE);
        registerReceiver(musicReceiver, intentFilter);
    }

    /**
     * 广播接收器 （内部类）
     */
    public class MusicReceiver extends BroadcastReceiver {

        public static final String TAG = "MusicReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            //UI控制
            UIControl(intent.getAction(), TAG);
        }
    }

    /**
     * Activity的观察者
     */
    private void activityObserver() {
        notificationLiveData = LiveDataBus.getInstance().with("notification_control", String.class);
        notificationLiveData.observe(MusicService.this, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                //UI控制
                UIControl(state, TAG);
            }
        });
    }

    /**
     * 页面的UI 控制 ，通过服务来控制页面和通知栏的UI
     *
     * @param state 状态码
     * @param tag
     */
    private void UIControl(String state, String tag) {
        switch (state) {
            case PLAY:
                //暂停或继续
                pauseOrContinueMusic();
                BLog.d(tag,PLAY+" or "+PAUSE);
                break;
            case PREV:

                previousMusic();
                BLog.d(tag,PREV);
                break;
            case NEXT:

                nextMusic();
                BLog.d(tag,NEXT);
                break;
            case CLOSE:

                closeNotification();
                BLog.d(tag,CLOSE);
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //进度发生改变时，
            activityLiveData.postValue(PROGRESS);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicReceiver != null) {
            //解除动态注册的广播
            unregisterReceiver(musicReceiver);
        }
    }


}
