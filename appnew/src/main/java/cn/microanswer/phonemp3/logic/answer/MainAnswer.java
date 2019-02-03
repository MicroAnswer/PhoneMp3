package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.ACTION;
import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Config;
import cn.microanswer.phonemp3.entity.Config_Table;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.MainLogic;
import cn.microanswer.phonemp3.ui.MainPage;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;
import cn.microanswer.phonemp3.ui.fragments.Main_AllMusicFragment;
import cn.microanswer.phonemp3.ui.fragments.Main_MyLoveFragment;
import cn.microanswer.phonemp3.ui.fragments.Main_PlayListFragment;
import cn.microanswer.phonemp3.ui.fragments.Main_RecentFragment;
import cn.microanswer.phonemp3.ui.fragments.PlayFragment;
import cn.microanswer.phonemp3.ui.fragments.SetFragment;
import cn.microanswer.phonemp3.ui.fragments.WebFragment;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class MainAnswer extends BaseAnswer<MainPage> implements MainLogic {
    public MainAnswer(MainPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle argments) {

        // 本类要接受播放各状态变化事件，所以执行这行代码。
        getPhoneMp3Activity().addMyMediaController(this);

        // 设置菜单显示夜间模式还是日间模式
        getPage().setDayModeMenu(SettingHolder.getSettingHolder().isDayMode());

        if (savedInstanceState == null) {
            // 初始默认所有歌曲界面
            getPage().show(Main_AllMusicFragment.class);
        }

        // 进入界面就尝试加载当前正在播放的歌曲信息，如果加载出来了，则显示
        initCurrentPlayingMusic();
    }

    @Override
    public void onBrowserConnected() {
        super.onBrowserConnected();

        initCurrentPlayingMusic();
    }

    // 获取正在播放的歌曲并显示。
    private void initCurrentPlayingMusic() {
        final MediaControllerCompat mediaControllerCompat = getPhoneMp3Activity().mGetMediaController();
        if (mediaControllerCompat != null && mediaControllerCompat.isSessionReady()) {

            // 获取正在播放的歌曲
            MediaMetadataCompat metadata = mediaControllerCompat.getMetadata();
            if (metadata != null) {
                onMetadataChanged(metadata);
            } else {
                // 没有获取到正在播放的歌曲。尝试从数据库获取
                Task.TaskHelper.getInstance().run(new Task.ITask<Void, MediaMetadataCompat>() {
                    @Override
                    public MediaMetadataCompat run(Void param) throws Exception {
                        Config config = SQLite.select().from(Config.class)
                                .where(Config_Table._Key.eq(Database.CONFIG_LASTPLAYMUSIC_KEY)).querySingle();
                        if (config != null && !TextUtils.isEmpty(config.getValue())) {
                            final String musicStr = config.getValue();

                            // 将这首歌同时也设置到服务中，这样当按下播放按钮时，播放这首歌.
                            // 为什么这么做？ 因为米有获取到正在播放的歌曲，说明这是刚打开软件，开没播放歌曲
                            // 这首歌既然显示到界面上了，所以要让播放按钮点击时播放这首歌，就必须先把这首歌
                            // 设置到服务中
                            PhoneMp3Activity phoneMp3Activity = getPhoneMp3Activity();
                            if (phoneMp3Activity != null) {
                                phoneMp3Activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bundle arg = new Bundle();
                                        arg.putString("music", musicStr);
                                        mediaControllerCompat.getTransportControls().sendCustomAction(ACTION.SET_CURRENT_MUSIC, arg);
                                    }
                                });
                            }

                            return Utils.APP.music2MetaData(JSON.parseObject(musicStr, Music.class));
                        }
                        return null;
                    }

                    @Override
                    public void afterRun(MediaMetadataCompat value) {
                        super.afterRun(value);
                        if (value != null) {
                            onMetadataChanged(value);
                        }
                    }
                });
            }

            // 播放状态获取
            PlaybackStateCompat playbackState = mediaControllerCompat.getPlaybackState();
            onPlaybackStateChanged(playbackState);
        }
    }

    @Override
    public void onMetadataChanged(final MediaMetadataCompat metadata) {
        super.onMetadataChanged(metadata);
        Task.TaskHelper.getInstance().run(new Task.ITask<MediaMetadataCompat, Object[]>() {
            @Override
            public MediaMetadataCompat getParam() {
                return metadata;
            }

            @Override
            public Object[] run(MediaMetadataCompat param) throws Exception {
                Object[] r = new Object[2];
                Music m = Utils.APP.metaData2Music(param);
                r[0] = m;
                r[1] = Music.isLove(m);
                return r;
            }

            @Override
            public void afterRun(Object[] object) {
                super.afterRun(object);
                Music value = (Music) object[0];
                boolean love = (boolean) object[1];
                getPage().updateControllerInfo(value.getCoverPath(), value.getTitle(), String.format("%s - %s", value.getArtist(), value.getAlbum()), love);
            }
        });
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        super.onPlaybackStateChanged(state);
        getPage().applyPlayStateChange(state);
    }

    @Override
    public void onMenuClick(int menuId) {
        switch (menuId) {
            case R.id.menuCellRecentPlay:
                jumpToRecentPlay();
                break;
            case R.id.menuCellMyLove:
                jumpToMyLove();
                break;
            case R.id.menuCellMyPlayList:
                jumpToMyPlayList();
                break;
            case R.id.menuCellAllMusic:
                jumpToAllMusic();
                break;
            case R.id.menuCellNightMode:
                changeDayNightMode();
                break;
            case R.id.menuCellSetting:
                jumpToSet();
                break;
            case R.id.menuCellAbout:
                jumpToAbout();
                break;
            default:
                Utils.UI.alert(getPhoneMp3Activity(), "紧急开发中…");
        }
    }

    // 切换日间模式和夜间模式
    private void changeDayNightMode() {
        boolean dayMode = SettingHolder.getSettingHolder().isDayMode();
        dayMode = !dayMode;
        getPage().setDayModeMenu(dayMode);
        SettingHolder.getSettingHolder().setDayMode(dayMode).commit();
        getPhoneMp3Activity().recreate();
    }

    @Override
    public void onExitClick() {
        getPhoneMp3Activity().finish();
    }

    @Override
    public void onPlayControlerClick() {
        getPhoneMp3Activity().push(PlayFragment.class);
    }

    @Override
    public void onBtnLoveClick() {
        MediaControllerCompat mediaControllerCompat = getPhoneMp3Activity().mGetMediaController();
        MediaControllerCompat.TransportControls transportControls = mediaControllerCompat.getTransportControls();
        transportControls.sendCustomAction(ACTION.NOTIFICATION.LOVE, null);
    }

    @Override
    public void onBtnNextClick() {
        MediaControllerCompat mediaControllerCompat = getPhoneMp3Activity().mGetMediaController();
        MediaControllerCompat.TransportControls transportControls = mediaControllerCompat.getTransportControls();
        transportControls.skipToNext();
    }

    @Override
    public void onPausePlayClick() {
        MediaControllerCompat mediaControllerCompat = getPhoneMp3Activity().mGetMediaController();
        MediaControllerCompat.TransportControls transportControls = mediaControllerCompat.getTransportControls();
        if (mediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            transportControls.pause();
        } else {
            transportControls.play();
        }
    }

    private void jumpToRecentPlay() {
        getPage().show(Main_RecentFragment.class);
    }

    private void jumpToMyLove() {
        getPage().show(Main_MyLoveFragment.class);
    }

    private void jumpToMyPlayList () {
        getPage().show(Main_PlayListFragment.class);
    }

    private void jumpToAllMusic() {
        getPage().show(Main_AllMusicFragment.class);
    }

    private void jumpToSet() {
        getPhoneMp3Activity().push(SetFragment.class);
    }

    private void jumpToAbout() {
        Bundle b = new Bundle();
        b.putString("url", "http://microanswer.cn/html/phonemp3/about.html");
        getPhoneMp3Activity().push(WebFragment.class, b);
    }
}
