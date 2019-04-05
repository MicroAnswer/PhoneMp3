package cn.microanswer.phonemp3.services;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.Task;

/**
 * 音乐播放服务
 */
public class CoreServices2 extends MediaBrowserServiceCompat {

    // 日志记录器
    private final static Logger logger = Logger.getLogger(CoreServices2.class);

    // 播放歌曲时会将服务设置为前台服务，此字段是通知的id.
    private final static int NOTIGY_ID = 10001;

    // 随机播放的时候，使用此变量产生随机数。
    private final Random RANDOM = new Random();

    // 播放器对象。
    private MediaPlayer mediaPlayer;

    // 播放器会话。 - 相当于前台界面和后台播放服务的通讯桥梁。
    private MediaSessionCompat mediaSession;

    // 服务创建时调用。
    @Override
    public void onCreate() {
        super.onCreate();

        // 实例化 mediaPlayer， 并设置监听。
        mediaPlayer = new MediaPlayer();
        MMediaPlayerListener mediaPlayerListener = new MMediaPlayerListener();
        mediaPlayer.setOnErrorListener(mediaPlayerListener);
        mediaPlayer.setOnCompletionListener(mediaPlayerListener);
        mediaPlayer.setOnPreparedListener(mediaPlayerListener);

        // 初始化会话
        mediaSession = new MediaSessionCompat(this, "CoreServices1");
        mediaSession.setCallback(new MMediaSessionCallBack());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE); // 按顺序列表播放，播放完不重复播放
        mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE); // 不随机播放

        setSessionToken(mediaSession.getSessionToken());

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("empty", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    private Task.TaskHelper taskHelper; // 异步任务执行器。

    private Task.TaskHelper getTaskHelper() {
        if (taskHelper == null) {
            taskHelper = Task.TaskHelper.newInstance(5);
        }
        return taskHelper;
    }

    private class MMediaSessionCallBack extends MediaSessionCompat.Callback {
        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }
    }

    /**
     * MediaPlayer 监听器。
     */
    private class MMediaPlayerListener implements
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {

        }
    }

}
