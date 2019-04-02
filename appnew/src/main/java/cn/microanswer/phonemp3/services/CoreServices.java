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
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
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
public class CoreServices extends MediaBrowserServiceCompat implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    // 日志记录器
    private final static Logger logger = Logger.getLogger(CoreServices.class);

    // 播放歌曲时会将服务设置为前台服务，此字段是通知的id.
    private final static int NOTIGY_ID = 10001;

    /**
     * <pre>
     *     随机播放的时候，使用此变量产生随机数。
     * </pre>
     */
    private final Random RANDOM = new Random();

    /**
     * <pre>
     *     播放器对象。
     * </pre>
     */
    private MediaPlayer player;

    /**
     * <pre>
     * 当前正在播放的歌曲。
     * 当前正在播放的歌曲，不论是在播放还是暂停，这个字段都有值。但是如果从来没有播放过歌曲，这个字段是null的
     * </pre>
     */
    private Music currentMusic;

    /**
     * <pre>
     *     下一曲要播放的歌曲，通常是有值的， 如果为null，那么接下来将停止播放(播放列表播放完了)。
     *
     *     每次播放歌曲的时候，会同时将此字段赋值，确定下一首要播放的歌曲。如果播放列表播放完了，在为
     *     该字段赋值的时候即会赋值null。即使该字段已经被赋值了，任然可以外接任意的修改这个值，达到可以设定
     *     下一首歌曲要播放的内容的功能。
     * </pre>
     */
    private Music nextMusic;

    /**
     * <pre>
     *     当前正在播放的音乐列表。
     * </pre>
     */
    private PlayList currentPlayList;

    /**
     * <pre>
     *     当前正在播放的音乐文件。
     *     当前播放的文件。
     *     (直接调取 play(String path) 方法，就会为该字段赋值，
     *     playMusic(String musicJson) 方法不会为该字段赋值的。)
     * </pre>
     */
    private String currentFile;

    /**
     * <pre>
     * 当服务刚打开时，是没有播放歌曲的，此时可以通过 setCurrentMusic 方法设置一首待播放的歌曲.
     * setCurrentMusic 方法中会将此字段设定为 true， 当播放按钮点击时，首先会检查此字段是否
     * 为 true， 如果是，则会播放通过 setCurrentMusic 方法设置的歌曲，并将 此字段再次设定为false。
     * </pre>
     */
    private boolean currentMusicSeted = false;

    /**
     * 媒体会话
     */
    private MediaSessionCompat mediaSession;

    /**
     * 音乐控制器发出对应音乐控制时，此对象中对应的方法会得到回调。
     */
    private MediaSessionCompat.Callback mediaSessionCallBack = new MediaSessionCompat.Callback() {

        /**
         * <pre>控制器调用transportControls.play();方法</pre>
         * 从暂停中恢复过来会调用此方法
         */
        @Override
        public void onPlay() {
            super.onPlay();
            CoreServices.this.start();
        }

        /**
         * <pre>
         *     控制器调用 transportControls.pause(); 方法时，此方法会被调用。
         * </pre>
         */
        @Override
        public void onPause() {
            super.onPause();
            CoreServices.this.pause();
        }

        /**
         * <pre>
         *     下一曲
         *     控制器调用 transportControls.skipToNext(); 方法时，此方法调用。
         * </pre>
         */
        @Override
        public void onSkipToNext() {
            playNext(true);
        }

        /**
         * <pre>
         *    mediaControllerCompat.sendCommand()
         *    控制器发送自定义命令时，此方法会吊起。
         * </pre>
         * @param command 命令
         * @param extras 参数
         * @param cb 回调
         */
        @Override
        public void onCommand(String command, Bundle extras, final ResultReceiver cb) {
            super.onCommand(command, extras, cb);
        }

        /**
         * 控制器发送自定义action时，此方法调用。
         * @param action 自定义action
         * @param extras 参数
         */
        @Override
        public void onCustomAction(String action, final Bundle extras) {
            super.onCustomAction(action, extras);

            // 收藏歌曲的action
            if (ACTION.NOTIFICATION.LOVE.equals(action)) {
                if (currentMusic != null) {
                    toggleLove(currentMusic);
                }

                // 主题颜色改变的action
            } else if (ACTION.THEME_COLOR_CHANGE.equals(action)) {
                sendNotify(currentMusic, mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING);

                // 设置当前播放歌曲的action
            } else if (ACTION.SET_CURRENT_MUSIC.equals(action)) {
                getTaskHelper().run(new Task.ITask<String, Music>() {
                    @Override
                    public String getParam() {
                        return extras.getString("music");
                    }

                    @Override
                    public Music run(String param) throws Exception {
                        if (TextUtils.isEmpty(param)) {
                            return null;
                        }
                        return JSON.parseObject(param, Music.class);
                    }

                    @Override
                    public void afterRun(Music value) {
                        super.afterRun(value);
                        if (value != null) {
                            setCurrentMusic(value);
                        }
                    }
                });
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
                            nextPlay(value);
                        }
                    }
                });
            }

        }

        /**
         * 播放制定歌曲
         * @param mediaId 歌曲id
         * @param extras 参数
         */
        @Override
        public void onPlayFromMediaId(final String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);

            // 从数据库查询获取这首歌曲，获取到后，开始播放。
            getTaskHelper().run(new Task.ITask<String, Music>() {
                @Override
                public String getParam() {
                    return mediaId;
                }

                @Override
                public Music run(String mediaId) throws Exception {
                    return SQLite.select().from(Music.class).where(Music_Table._id.eq(Long.valueOf(mediaId))).querySingle();
                }

                @Override
                public void afterRun(Music value) {
                    super.afterRun(value);
                    playMusic(value);
                }
            });

        }
    };

    /**
     * 服务不提供音乐资源。 请勿使用此方法获取歌曲列表数据
     *
     * @param clientPackageName ·
     * @param clientUid         ·
     * @param rootHints         ·
     * @return ·
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("empty", null);
    }

    /**
     * 服务不提供音乐资源。请勿使用此方法获取歌曲列表数据
     *
     * @param parentId ·
     * @param result   ·
     */
    @Override
    public void onLoadChildren(@NonNull final String parentId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    /**
     * 服务创建时此方法调用
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化播放器。
        player = new MediaPlayer();
        player.setOnPreparedListener(this); // 资源准备完成的监听
        player.setOnCompletionListener(this); // 播放完成的监听
        player.setOnErrorListener(this); // 出错监听

        // 初始化会话
        mediaSession = new MediaSessionCompat(this, "CoreServices");
        mediaSession.setCallback(mediaSessionCallBack);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(_newState(PlaybackStateCompat.STATE_NONE));
        mediaSession.setMetadata(null);
        mediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE); // 按顺序列表播放，播放完不重复播放
        mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE); // 不随机播放

        setSessionToken(mediaSession.getSessionToken());

        logger.i("服务启动");
    }

    /**
     * 发送到服务的命令
     *
     * @param intent  命令
     * @param flags   ·
     * @param startId ·
     * @return ·
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION.NOTIFICATION.LOVE.equals(action)) {
                // 点击通知栏里面的收藏按钮
                toggleLove(currentMusic);
            } else if (ACTION.NOTIFICATION.NEXT.equals(action)) {
                // 点击通知栏里面的下一曲
                playNext(true);
            } else if (ACTION.NOTIFICATION.PREVIOUS.equals(action)) {
                // 点击通知栏里面你的上一曲
                playPrevious();
            } else if (ACTION.NOTIFICATION.TOGGLE.equals(action)) {
                // 点击通知栏里面的播放暂停
                int state = mediaSession.getController().getPlaybackState().getState();
                if (state == PlaybackStateCompat.STATE_PAUSED) {
                    start();
                } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                    pause();
                } else {
                    start();
                }
            }
        }

        logger.i("服务onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    // 收藏或者取消收藏歌曲
    private void toggleLove(final Music currentMusic) {
        getTaskHelper().run(new Task.ITask<Music, Integer>() {
            @Override
            public Music getParam() {
                return currentMusic;
            }

            @Override
            public Integer run(Music param) throws Exception {
                return Music.toggleLove(currentMusic);
            }

            @Override
            public void afterRun(Integer value) {
                super.afterRun(value);
                if (value == 1) {
                    // 取消收藏成功
                    sendNotify(currentMusic, mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING);
                    // 更新媒体信息
                    mediaSession.setMetadata(Utils.APP.music2MetaData(currentMusic));
                } else if (value == 2) {
                    // 收藏成功
                    sendNotify(currentMusic, mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING);
                    // 更新媒体信息
                    mediaSession.setMetadata(Utils.APP.music2MetaData(currentMusic));
                } else {
                    // 操作失败
                    Toast.makeText(CoreServices.this, "操作失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.reset();
        player.release();
        player = null;
        logger.i("服务终止。");
    }

    /**
     * 播放某一首歌曲
     *
     * @param music
     */
    private void playMusic(final Music music) {
        this.currentMusic = music;
        play(this.currentMusic.get_data());
    }

    /**
     * 将某一首歌曲设置为待播放状态，但是不播放，等待点击播放才进行播放，
     *
     * @param music
     */
    private void setCurrentMusic(final Music music) {
        this.currentMusic = music;
        this.currentMusicSeted = true;
        initPlayListIfNeeded(); // 初始化一下这首歌对应的播放列表
        mediaSession.setMetadata(Utils.APP.music2MetaData(music));
    }

    private void initPlayListIfNeeded() {
        // 如果当前播放列表未初始化，根据当前歌曲，进行初始化。
        if (currentPlayList == null) {
            currentPlayList = new PlayList();
            currentPlayList.setId(Database.PLAYLIST_ID_CURRENT);

            getTaskHelper().run(new Task.ITask<Object, List<Music>>() {
                @Override
                public List<Music> run(Object param) throws Exception {

                    return SQLite.select().from(Music.class)
                            .where(Music_Table.list_id.eq(currentMusic.getListId()))
                            .queryList();
                }

                @Override
                public void afterRun(List<Music> value) {
                    super.afterRun(value);
                    currentPlayList.setMusics(value);
                }
            });
        } else {
            if (!currentMusic.getListId().equals(currentPlayList.getId())) {
                // 播放的歌曲列表和当前列表不在同一个列表。
                // 将当前歌曲的列表替换为当前列表

                getTaskHelper().run(new Task.ITask<Music, List<Music>>() {
                    @Override
                    public Music getParam() {
                        return currentMusic;
                    }

                    @Override
                    public List<Music> run(Music param) throws Exception {
                        PlayList p = new PlayList();
                        p.setId(param.getListId());
                        if (p.getId().equals(Database.PLAYLIST_ID_HISTORY)) {
                            return p.getMusics(false);
                        } else {
                            return p.getMusics();
                        }
                    }

                    @Override
                    public void afterRun(List<Music> value) {
                        super.afterRun(value);
                        currentPlayList.setMusics(value);
                    }
                });

            }
        }
    }

    // 播放下一曲
    private void playNext(final boolean fromUser) {

        if (currentMusic == null) {
            logger.i("在播放下一曲时，发现当前播放的音乐为 null");
            return;
        }

        getTaskHelper().run(new Task.ITask<Void, Music>() {
            @Override
            public Music run(Void param) throws Exception {
                return getNextMusic(currentMusic, fromUser, mediaSession);
            }

            @Override
            public void afterRun(Music value) {
                super.afterRun(value);
                if (value == null) { // 下一曲没有歌曲可播放了。
                    logger.i("在将要播放下一曲时，发现下一曲没有了");

                    // 关闭服务前台
                    sendNotify(currentMusic, false);

                    // 改变session状态
                    mediaSession.setPlaybackState(_newState(PlaybackStateCompat.STATE_PAUSED));

                } else {
                    logger.i("在将要播放下一曲时，正常播放下一曲");
                    playMusic(value);
                }
            }
        });
    }

    // 播放上一曲
    private void playPrevious() {
        getTaskHelper().run(new Task.ITask<Void, Music>() {
            @Override
            public Music run(Void param) throws Exception {
                return getPreviousMusic(currentMusic, mediaSession);
            }

            @Override
            public void afterRun(Music value) {
                super.afterRun(value);
                if (value == null) {
                    return;
                }
                playMusic(value);
            }
        });
    }

    // 设置下一曲要播放的歌曲
    private void nextPlay(Music nextMusic) {
        this.nextMusic = nextMusic;
    }

    /**
     * 播放某一个文件
     *
     * @param filePath
     */
    private void play(String filePath) {
        try {
            currentFile = filePath;

            // 初始化一下播放列表
            initPlayListIfNeeded();

            // 准备播放
            player.reset();
            player.setDataSource(filePath);
            player.prepareAsync();

        } catch (Exception e) {
            logger.e(String.format("在播放 %s 的时候出错：", filePath));
            e.printStackTrace();
        }
    }

    // 暂停播放，多次调用会等同于1次调用
    private void pause() {

        if (this.player == null) {
            return;
        }
        if (this.currentMusic == null) {
            return;
        }

        this.player.pause();

        // 发送暂停广播
        Utils.APP.sendBroadcast(this, ACTION.PLAY_2_PAUSE);
        // 更新通知 // 关闭前台状态
        if (currentMusic != null) sendNotify(this.currentMusic, false);
        // seesion状态改变
        mediaSession.setPlaybackState(_newState(PlaybackStateCompat.STATE_PAUSED));
    }

    /**
     * 开始播放。
     * 此方法通常是用于暂停过的恢复播放。
     * <p>
     * 若从未播放过歌曲，直接调用此方法，不会有任何效果。
     * 若当前已经在播放了，此方法调用也不会有任何效果
     */
    private void start() {
        if (this.player == null) {
            return;
        }
        if (this.currentMusic == null) {
            return;
        }
        if (this.player.isPlaying()) {
            return;
        } else {

            if (currentMusicSeted) {

                play(currentMusic.get_data());
                return;
            }

        }

        this.player.start();

        // 发送播放广播
        Utils.APP.sendBroadcast(this, ACTION.PAUSE_2_PLAY);
        // 更新通知
        if (currentMusic != null) sendNotify(this.currentMusic, true);
        // seesion状态改变
        mediaSession.setPlaybackState(_newState(PlaybackStateCompat.STATE_PLAYING));
    }

    private PlaybackStateCompat.Builder __playBackStateCompatBuilder;

    private PlaybackStateCompat _newState(int state) {
        if (__playBackStateCompatBuilder == null) {
            __playBackStateCompatBuilder = new PlaybackStateCompat.Builder();
            __playBackStateCompatBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        }
        return __playBackStateCompatBuilder.setState(state, 0, 1f).build();
    }

    private PendingIntent _PendingIntent(String action) {
        final ComponentName serviceName = new ComponentName(this, CoreServices.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
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
                boolean isPlaying = mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
                String title = param.getTitle();
                String desc = String.format("%s - %s", param.getArtist(), param.getAlbum());
                String coverPath = param.getCoverPath();
                boolean isLoved = false; // 是否收藏的

                Bitmap largeIcon = null;

                if (TextUtils.isEmpty(coverPath)) {
                    largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
                } else {
                    largeIcon = BitmapFactory.decodeFile(coverPath);
                }

                Music music = SQLite.select().from(Music.class)
                        .where(Music_Table._data.eq(param.get_data()))
                        .and(Music_Table.list_id.eq(Database.PLAYLIST_ID_MYLOVE))
                        .querySingle();
                isLoved = music != null;
                // MediaControllerCompat controller = mediaSession.getController();

                // ComponentName serviceName = new ComponentName(CoreServices.this, PhoneMp3Activity.class);
                Intent intent = new Intent(CoreServices.this, PhoneMp3Activity.class);
                intent.putExtra("skipIndex", "true");
                // intent.setComponent(serviceName);

                PendingIntent activity = PendingIntent.getActivity(CoreServices.this, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(CoreServices.this, "musicPlay");
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

    // 资源准备完成 回调
    @Override
    public void onPrepared(final MediaPlayer mp) {

        currentMusicSeted = false;

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
                    // 最近播放列表中，有这首歌曲
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

        // 进行播放
        mp.start();

        // 改变会话状态
        mediaSession.setMetadata(Utils.APP.music2MetaData(currentMusic));
        mediaSession.setPlaybackState(_newState(PlaybackStateCompat.STATE_PLAYING));

        // 发送通知
        if (currentMusic != null) {
            sendNotify(currentMusic, true);
        } else {
            // 根据该文件路径从数据库中获取信息
            taskHelper = getTaskHelper();
            taskHelper.run(new Task.ITask<String, Music>() {
                @Override
                public String getParam() {
                    return currentFile;
                }

                @Override
                public Music run(String filePath) throws Exception {

                    Music music = SQLite.select().from(Music.class)
                            .where(Music_Table._data.eq(filePath))
                            .and(Music_Table.list_id.eq(Database.PLAYLIST_ID_ALL))
                            .querySingle();

                    if (music == null) {
                        music = new Music();

                        // 数据库也没有这个文件的信息, 只好从文件本身获取了
                        File file = new File(filePath);
                        music.setTitle(file.getName());
                        music.setArtist("来自文件夹");
                        music.setAlbum(file.getParentFile().getCanonicalPath());

                    }

                    return music;
                }

                @Override
                public void afterRun(Music value) {
                    sendNotify(value, true);
                }
            });
        }
    }

    // 播放完成 回调
    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext(false);
    }

    // 获取下一首要播放的歌曲
    private Music getNextMusic(Music currentMusic, boolean fromUser, MediaSessionCompat mediaSession) {

        if (nextMusic != null) {
            Music m = nextMusic;
            nextMusic = null;
            return m;
        }


        List<Music> musics = currentPlayList.getMusics();

        int i = musics.indexOf(currentMusic);

        // 播放列表为空。播完这首下一首就别播放了
        if (i < 0) {
            return null;
        }

        // 播放列表不为空，根据播放方式选择下一首歌曲

        // 循环方式 (列表播放、列表循环、单曲循环)
        int repeatMode = mediaSession.getController().getRepeatMode();
        // 播放方式 (按顺序播放、随机播放)
        int shuffleMode = mediaSession.getController().getShuffleMode();

        if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) {
            // 列表播放
            if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                // 随机播放
                return musics.get(RANDOM.nextInt(musics.size()));
            } else {
                if (1 + i < musics.size()) {
                    return musics.get(i + 1);
                } else {
                    // 列表循环完毕。
                    if (fromUser) {
                        return musics.get(0);
                    } else {
                        return null;
                    }
                }
            }
        } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
            // 单曲循环
            return currentMusic;
        } else {
            // 列表循环
            if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                // 随机播放
                return musics.get(RANDOM.nextInt(musics.size()));
            } else {
                if (1 + i < musics.size()) {
                    return musics.get(i + 1);
                } else {
                    // 列表循环完毕。从第0首继续
                    return musics.get(0);
                }
            }
        }
    }

    // 获取上一曲播放的歌曲
    private Music getPreviousMusic(Music music, MediaSessionCompat mediaSession) {

        List<Music> musics = currentPlayList.getMusics();

        int i = musics.indexOf(music);

        // 播放列表为空。找不到上一曲
        if (i < 0) {
            return null;
        }

        // 播放列表不为空，根据播放方式选择上一首歌曲

        // 循环方式 (列表播放、列表循环、单曲循环)
        int repeatMode = mediaSession.getController().getRepeatMode();
        // 播放方式 (按顺序播放、随机播放)
        int shuffleMode = mediaSession.getController().getShuffleMode();

        if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) {
            // 列表播放
            if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                // 随机播放 - 随便选择一首
                return musics.get(RANDOM.nextInt(musics.size()));
            } else {
                if (-1 + i < 0) {
                    return musics.get(musics.size() - 1);
                } else {
                    return musics.get(-1 + i);
                }
            }
        } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
            // 单曲循环
            return currentMusic;
        } else {
            // 列表循环
            if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                // 随机播放
                return musics.get(RANDOM.nextInt(musics.size()));
            } else {
                if (-1 + i < 0) {
                    return musics.get(musics.size() - 1);
                } else {
                    return musics.get(-1 + i);
                }
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        // 尝试再次播放
        play(this.currentFile);
        return true;
    }


    private Task.TaskHelper taskHelper; // 异步任务执行器。

    private Task.TaskHelper getTaskHelper() {
        if (taskHelper == null) {
            taskHelper = Task.TaskHelper.newInstance(5);
        }
        return taskHelper;
    }

}
