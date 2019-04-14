package cn.microanswer.phonemp3.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.ACTION;
import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Config;
import cn.microanswer.phonemp3.entity.Config_Table;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.Music_Table;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.entity.PlayList_Table;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

/**
 * 音乐播放服务
 */
public class CoreServices2 extends MediaBrowserServiceCompat {
    public static boolean isPlaying = false; // 标记是否正在播放歌曲，此字段可信度高。

    // 日志记录器
    private final static Logger logger = Logger.getLogger(CoreServices2.class);

    // 播放歌曲时会将服务设置为前台服务，此字段是通知的id.
    private final static int NOTIGY_ID = 10001;

    // 随机播放的时候，使用此变量产生随机数。
    private final Random RANDOM = new Random();

    // 播放器对象。
    private MediaPlayer mediaPlayer;

    // 播放器会话。 - 相当于前台界面和后台播放服务的通讯桥梁。
    private MediaSessionCompat mediaSessionCompat;

    // 播放器监听器。
    private MMediaPlayerListener mediaPlayerListener;

    // 播放列表。每次在资源开始播放时检查此字段是否被初始化
    // 如果没有：则根据当前播放的歌曲所在的列表进行初始化。
    // 如果有：则不做初始化处理。
    private PlayList playList;

    // 标记当前正在播放的歌曲。
    private Music currentMusic;

    // 调用play方法播放歌曲时候，允许设定播放起始位置。
    // 使用时段：1、此段在开始播放时被设定值，在资源准备完成时使用，并撤销值。
    private int playAtPosition = -1;

    // 用户设定的下一曲要播放的歌曲。此字段只有用户显示的设置了才会有值，程序不会自动赋值。
    private Music nextMusic;

    // 当用户删除某一首歌曲时，同时这首歌曲又是正在播放的歌曲，那么此字段将被赋值，待下一首歌曲成功播放时，会删除
    // 此字段，并将列表中该歌曲移除。
    private Music shouldDeleteMusic;

    // 标记歌曲是否准备完毕。没有准备完毕，需要重新准备
    private boolean isDataPeared;

    // 服务创建时调用。
    @Override
    public void onCreate() {
        super.onCreate();

        // 实例化 mediaPlayer， 并设置监听。
        mediaPlayer = new MediaPlayer();
        mediaPlayerListener = new MMediaPlayerListener();
        mediaPlayer.setOnErrorListener(mediaPlayerListener);
        mediaPlayer.setOnCompletionListener(mediaPlayerListener);
        mediaPlayer.setOnPreparedListener(mediaPlayerListener);

        // 初始化会话
        mediaSessionCompat = new MediaSessionCompat(this, "CoreServices1");
        mediaSessionCompat.setCallback(new MMediaSessionCallBack());
        // 初始化默认状态是NONE的暂停状态。
        mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_NONE, 0, 1).build());
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE); // 列表不重复播放
        mediaSessionCompat.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE); // 不随机播放

        setSessionToken(mediaSessionCompat.getSessionToken());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION.NOTIFICATION.LOVE.equals(action)) {
                // 点击通知栏里面的收藏按钮
                doLoveMusic();
            } else if (ACTION.NOTIFICATION.NEXT.equals(action)) {
                // 点击通知栏里面的下一曲
                onSkipToNext();
            } else if (ACTION.NOTIFICATION.PREVIOUS.equals(action)) {
                // 点击通知栏里面你的上一曲
                onSkipToPrevious();
            } else if (ACTION.NOTIFICATION.TOGGLE.equals(action)) {
                // 点击通知栏里面的播放暂停
                int state = mediaSessionCompat.getController().getPlaybackState().getState();
                if (state == PlaybackStateCompat.STATE_PAUSED) {
                    start();
                } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                    pause();
                } else {
                    start();
                }
            }
        }

        // logger.i("服务onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDataPeared = false;
        isPlaying = false;
        this.mediaPlayer.release();
        mediaSessionCompat.release();
    }

    // 播放指定歌曲。从指定位置播放
    public void play(Music music, int position) throws Exception {
        this.isDataPeared = false;
        this.playAtPosition = position;
        this.currentMusic = music;
        this.mediaPlayer.reset();
        this.mediaPlayer.setDataSource(music.get_data());
        this.mediaPlayer.prepareAsync();
    }

    // 暂停
    public void pause() {
        if (currentMusic == null) return;

        this.mediaPlayer.pause();
        dispachStateChange(PlaybackStateCompat.STATE_PAUSED);
        isPlaying = false;
    }

    // 恢复播放
    public void start() {
        if (currentMusic == null) return;
        if (!this.isDataPeared) {
            try{play(currentMusic, 0);}catch (Exception e) {onError(e);}
            return;
        }
        this.mediaPlayer.start();
        dispachStateChange(PlaybackStateCompat.STATE_PLAYING);
        isPlaying = true;
    }

    public void dispachStateChange(int state) {
        dispachStateChange(state, "no_action");
    }

    public void dispachStateChange(int state, String action) {
        PlaybackStateCompat stateCompat = new PlaybackStateCompat.Builder()
                .setState(state, mediaPlayer.getCurrentPosition(), 1)
                .setExtras(Utils.APP.music2Bundle(currentMusic))
                .addCustomAction(action, action, 1)
                .build();
        dispachStateChange(stateCompat);
    }

    public void dispachStateChange(PlaybackStateCompat stateCompat) {
        mediaSessionCompat.setMetadata(Utils.APP.music2MetaData(currentMusic));
        mediaSessionCompat.setPlaybackState(stateCompat);
        if (stateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            sendNotify(currentMusic, true);
        } else {
            sendNotify(currentMusic, false);
        }
    }

    // 下一曲
    public void onSkipToNext() {
        if (currentMusic == null) return;

        if (mediaPlayer != null) {
            mediaPlayerListener.onCompletion(mediaPlayer);
        }
    }

    // 上一曲
    public void onSkipToPrevious() {
        if (currentMusic == null) return;

        // 播放上一曲。
        // 如果是随机模式，那就上一曲下一曲都一样效果。
        if (mediaSessionCompat.getController().getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
            onSkipToNext();
        } else {
            try {
                // 不是随机模式，那就找到前一曲播放
                List<Music> musics = playList.getMusics();
                int i = musics.indexOf(currentMusic);
                if (i - 1 < 0) {
                    // 已经播放到第一首了。
                    // 如果是列表循环，则又从最后一首歌曲开始播放
                    // 如果没有开启列表循环，则不播放了。
                    if (mediaSessionCompat.getController().getRepeatMode() == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                        play(musics.get(musics.size() - 1), 0);
                    } else {
                        Toast.makeText(CoreServices2.this, "上一曲没有了", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    play(musics.get(i - 1), 0);
                }
            } catch (Exception e) {
                onError(e);
            }
        }
    }


    // 发送前台通知。
    private void sendNotify(final Music music, final boolean forground) {
        taskHelper = getTaskHelper();

        // 因为要解析数据可能耗时，所以，将解析数据放在异步中
        taskHelper.run(new Task.ITask<Music, Notification>() {
            @Override
            public Music getParam() {
                return music;
            }

            @Override
            public Notification run(Music param) throws Exception {
                if (param == null) {
                    return null;
                }
                boolean isPlaying = mediaSessionCompat.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
                String title = param.getTitle();
                String desc = String.format("%s-%s", param.getArtist(), param.getAlbum());
                String coverPath = param.getCoverPath();
                boolean isLoved = Music.isLove(param); // 是否收藏的

                Bitmap largeIcon = null;

                if (TextUtils.isEmpty(coverPath)) {
                    largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
                } else {
                    largeIcon = BitmapFactory.decodeFile(coverPath);
                }

                // MediaControllerCompat controller = mediaSession.getController();

                // ComponentName serviceName = new ComponentName(CoreServices1.this, PhoneMp3Activity.class);
                Intent intent = new Intent(CoreServices2.this, PhoneMp3Activity.class);
                intent.putExtra("skipIndex", "true");
                // intent.setComponent(serviceName);

                PendingIntent activity = PendingIntent.getActivity(CoreServices2.this, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(CoreServices2.this, "musicPlay");
                builder.setSmallIcon(R.drawable.notify_);
                builder.setLargeIcon(largeIcon);
                builder.setContentTitle(title);
                builder.setContentText(desc);
                builder.setOngoing(isPlaying);
                builder.setContentIntent(activity); // 点击通知的时候，打开软件
                // .setDeleteIntent() // 移除通知的时候
                builder.setColor(SettingHolder.getSettingHolder().getColorPrimary());
                builder.setWhen(System.currentTimeMillis());
                builder.addAction(R.drawable.icon_previous, "上一曲", _PendingIntent(ACTION.NOTIFICATION.PREVIOUS));
                builder.addAction(isPlaying ? R.drawable.icon_pause : R.drawable.icon_play, isPlaying ? "暂停" : "播放", _PendingIntent(ACTION.NOTIFICATION.TOGGLE));
                builder.addAction(R.drawable.icon_next, "下一曲", _PendingIntent(ACTION.NOTIFICATION.NEXT));
                builder.addAction(isLoved ? R.drawable.icon_love : R.drawable.icon_dislove, isLoved ? "取消收藏" : "收藏", _PendingIntent(ACTION.NOTIFICATION.LOVE));
                builder.setShowWhen(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                    androidx.media.app.NotificationCompat.MediaStyle style = new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2, 3);
                    builder.setStyle(style);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        String channelId = "musicPlay";
                        String channelName = "音乐怎么了";
                        int importance = NotificationManager.IMPORTANCE_LOW;
                        notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
                    }
                }
                Notification n2 = builder.build();
                n2.sound = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n2.category = Notification.CATEGORY_TRANSPORT;
                }
                n2.flags = Notification.FLAG_FOREGROUND_SERVICE;
                return n2;
            }

            @Override
            public void afterRun(Notification notify) {
                super.afterRun(notify);
                if (notify == null) return;
                startForeground(NOTIGY_ID, notify);
                if (!forground) {
                    stopForeground(false);
                }
            }
        });
    }

    // 收藏\取消收藏当前正在播放的歌曲。
    private void doLoveMusic() {
        if (currentMusic == null) return;

        getTaskHelper().run(new Task.ITask<Object, Integer>() {
            @Override
            public Integer run(Object param) throws Exception {
                return Music.toggleLove(currentMusic);
            }

            @Override
            public void afterRun(Integer value) {
                super.afterRun(value);
                if (-1 == value) {
                    Toast.makeText(CoreServices2.this, "操作失败", Toast.LENGTH_SHORT).show();
                    return;
                } else if (1 == value) {
                    // Toast.makeText(CoreServices2.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CoreServices2.this, "收藏成功", Toast.LENGTH_SHORT).show();
                }
                dispachStateChange(
                        mediaSessionCompat.getController().getPlaybackState().getState(),
                        value == 1 ? ACTION.DIS_LOVE : ACTION.LOVE
                );
            }
        });
    }

    // 保存播放记录。
    private void logPlayedMusic() {

        // 记录当前播放的歌曲
        getTaskHelper().run(new Task.ITask<Music, Object>() {
            @Override
            public Music getParam() {
                return currentMusic;
            }

            @Override
            public Object run(Music param) throws Exception {

                // 记录当前播放的歌曲
                Config config = SQLite.select().from(Config.class)
                        .where(Config_Table._Key.eq(Database.CONFIG_LASTPLAYMUSIC_KEY)).querySingle();
                if (config == null) {
                    config = new Config();
                    config.setKey(Database.CONFIG_LASTPLAYMUSIC_KEY);
                    config.setValue(JSON.toJSONString(param));
                    config.setDesc("最后一次记录到的播放的歌曲。");
                    config.insert();
                } else {
                    config.setValue(JSON.toJSONString(param));
                    config.update();
                }

                // 加入播放历史记录
                // 先查询播放记录中是否有这条数据， 如果有了这条数据，那么修改一下这条数据的更新时间。
                PlayList historyPlayList = SQLite.select().from(PlayList.class)
                        .where(PlayList_Table._id.eq(Database.PLAYLIST_ID_HISTORY))
                        .querySingle();
                if (historyPlayList == null) {
                    // 还没有历史播放记录列表。现在创建
                    historyPlayList = new PlayList();
                    historyPlayList.setId(Database.PLAYLIST_ID_HISTORY);
                    historyPlayList.setName("最近播放");
                    historyPlayList.setRamark("这个列表保存了最近播放的歌曲");
                    historyPlayList.save();
                }

                // 查询是否在历史播放记录里。
                Music music = SQLite.select().from(Music.class)
                        .where(Music_Table.list_id.eq(historyPlayList.getId()))
                        .and(Music_Table._data.eq(param.get_data()))
                        .querySingle();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                if (music == null) {
                    // 最近播放列表中，没有这首歌曲。
                    music = Music.from(param);
                    music.setId(0L);
                    music.setListId(historyPlayList.getId());
                    music.setUpdateAt(simpleDateFormat.format(new Date()));
                    music.save();
                } else {
                    // 最近播放列表中，有这首歌曲，更新播放时间
                    music.setUpdateAt(simpleDateFormat.format(new Date()));
                    music.update();
                }

                return music;
            }

            @Override
            public void afterRun(Object value) {
                super.afterRun(value);
                // Music music = (Music) value;
                // mediaSession.setMetadata(Utils.APP.music2MetaData(music));
            }
        });
    }

    // 歌曲被删除
    private void onMusicDeleted(Music music) {

        if (mediaSessionCompat.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            // 删除的歌曲正在播放。
            // 则播放下一曲
            this.shouldDeleteMusic = music;
            onSkipToNext();
        } else {
            if (currentMusic != null && currentMusic.equals(music)) {
                currentMusic = null;
            }
            // 删除的歌曲是当前歌曲，但是当前没有在播放，则发出状态改变事件。
            dispachStateChange(PlaybackStateCompat.STATE_PAUSED);
            // 移除通知
            stopForeground(true);
            // 将歌曲从列表移除
            if (playList != null) {
                playList.getMusics().remove(music);
            }
        }
    }

    // 初始化播放列表
    private void initPlayListIfNeed() {
        // 当前未初始化播放列表， 或者当前的播放列表与播放的歌曲不在同一个列表。
        // 则进行播放列表的初始化。
        if (playList == null || !playList.getId().equals(currentMusic.getListId())) {
            // 初始化播放列表。

            // 先获取当前歌曲所在列表里面的所有歌曲。
            List<Music> musics = SQLite.select().from(Music.class)
                    .where(Music_Table.list_id.eq(currentMusic.getListId()))
                    .queryList();

            // 根据这些歌曲构建当前正在播放的列表
            playList = new PlayList();
            playList.setId(Database.PLAYLIST_ID_CURRENT);
            playList.setName("playing_list");
            playList.setRamark(currentMusic.getListId());
            // 保存正在播放列表信息。
            playList.save();

            // 将列表歌曲设定到列表中，
            playList.setMusics(musics);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("empty", null);
    }

    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    private PendingIntent _PendingIntent(String action) {
        final ComponentName serviceName = new ComponentName(this, CoreServices2.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(this, 0, intent, 0);
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
            getTaskHelper().run(new Task.ITask<String, Music>() {
                @Override
                public String getParam() {
                    return mediaId;
                }

                @Override
                public Music run(String param) {
                    if (param.equals(ACTION.NONE_ID)) {
                        if (extras != null) {
                            return Utils.APP.bundle2Music(extras);
                        }
                        return null;
                    }
                    return SQLite.select().from(Music.class)
                            .where(Music_Table._id.eq(Long.valueOf(param))).querySingle();
                }

                @Override
                public void afterRun(Music value) {
                    super.afterRun(value);
                    if (value == null) return;
                    try {
                        play(value, extras.getInt("position", 0));
                    } catch (Exception e) {
                        CoreServices2.this.onError(e);
                    }
                }
            });
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            CoreServices2.this.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            CoreServices2.this.onSkipToPrevious();
        }

        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            start();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            if (ACTION.NOTIFICATION.LOVE.equals(action)) {
                doLoveMusic();
            } else if (ACTION.THEME_COLOR_CHANGE.equals(action)) {
                sendNotify(currentMusic, mediaSessionCompat.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING);

            } else if (ACTION.SET_CURRENT_MUSIC.equals(action)) {
                // 设置当前播放歌曲的action
                Music music = Utils.APP.bundle2Music(extras);
                try {
                    playAtPosition = 0;
                    currentMusic = music;
                    initPlayListIfNeed();
                } catch (Exception e) {
                    onError(e);
                }
            } else if (ACTION.SET_NEXT_MUSIC.equals(action)) {
                getTaskHelper().run(new Task.ITask<Bundle, Music>() {
                    @Override
                    public Bundle getParam() {
                        return extras;
                    }

                    @Override
                    public Music run(Bundle param) throws Exception {
                        return Utils.APP.bundle2Music(param);
                    }

                    @Override
                    public void afterRun(Music value) {
                        super.afterRun(value);
                        if (value != null) {
                            nextMusic = value;
                        }
                    }
                });
            } else if (ACTION.MUSIC_DELETED.equals(action)) {
                onMusicDeleted(Utils.APP.bundle2Music(extras));
            } else if (ACTION.EXIT.equals(action)) {
                isDataPeared = false;
                stopForeground(true);
            }
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
            isPlaying = false;
            // 一首歌曲播放完成的回调。 在这里找到下一曲歌曲然后进行播放。
            try {
                // 先检查是否用户显示设置了下一曲要播放的歌曲，如果设置了，那么播放该歌曲。
                if (nextMusic != null) {
                    play(nextMusic, 0);

                    // 删除下一曲要播放的标记。
                    nextMusic = null;
                    return;
                }

                // 根据循环方式拿到下一曲。
                int repeatMode = mediaSessionCompat.getController().getRepeatMode();
                int shuffleMode = mediaSessionCompat.getController().getShuffleMode();
                List<Music> musics = playList.getMusics();
                if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                    // 开启了随机播放.
                    // 在播放列表中随机寻找1首歌曲.
                    Music tempMusic = null;
                    while (tempMusic == null || tempMusic.equals(currentMusic)) {
                        tempMusic = musics.get(RANDOM.nextInt(musics.size()));
                    }
                    play(tempMusic, 0);
                    return;
                }
                // 未开启随机播放。直接下一曲。
                int i = musics.indexOf(currentMusic);
                if (i + 1 >= musics.size()) {
                    // 已经播放到最后一首了。
                    // 如果是列表循环，则又从第一首歌曲开始播放
                    // 如果没有开启列表循环，则不播放了。
                    if (repeatMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                        play(musics.get(0), 0);
                    } else {
                        Toast.makeText(CoreServices2.this, "下一曲没有了", Toast.LENGTH_SHORT).show();
                        dispachStateChange(PlaybackStateCompat.STATE_PAUSED);
                    }
                } else {
                    play(musics.get(i + 1), 0);
                }

            } catch (Exception e) {
                CoreServices2.this.onError(e);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            try {
                // 出错，通常是准备歌曲资源会出错，此处又进行播放操作，直到不出错。
                play(currentMusic, 0);
            } catch (Exception e) {
                CoreServices2.this.onError(e);
            }
            return true;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            isDataPeared = true;

            initPlayListIfNeed();

            // 如果指定了播放位置，则从指定位置开始播放。
            if (playAtPosition >= 0) {
                mp.seekTo(playAtPosition);
                playAtPosition = -1;
            }

            // 记录到历史播放记录。
            logPlayedMusic();

            // 如果有待移除的歌曲，则将其移除。
            if (shouldDeleteMusic != null) {
                // 将歌曲从列表移除
                if (playList != null) {
                    playList.getMusics().remove(shouldDeleteMusic);
                }
            }

            // 资源准备完成，进行播放。
            start();
        }
    }

    private void onError(Exception e) {
        e.printStackTrace();
        logger.e(e.getMessage());
    }

}
