package answer.android.phonemp3.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import org.xutils.common.task.AbsTask;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.PlayServiceBridgeAIDL;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.bean.PlayList;
import answer.android.phonemp3.db.DataBaseManager;
import answer.android.phonemp3.fragment.LovesFragment;
import answer.android.phonemp3.fragment.PlayhistoryFragment;
import answer.android.phonemp3.tool.CurrentPlayingList3;
import answer.android.phonemp3.tool.Mp3Player;
import answer.android.phonemp3.tool.Tool;

/**
 * 歌曲播放服务
 * Created by Micro on 2017/6/18.
 */

public class PhoneMp3PlayService extends Service implements Mp3Player.Mp3PlayerListener, AudioManager.OnAudioFocusChangeListener {

    // 可以同时保留多个桥梁链接
    private HashMap<String, PlayServiceBridgeAIDL.Stub> stubs;
    private CurrentPlayingList3 currentPlayingList;
    private DataBaseManager dataBaseManager;
    private AudioManager audioManager;
    private Mp3Player mp3Player;
    private ComponentName mComponentName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (stubs == null) {
            stubs = new HashMap<>();
        }
        String client;
        if (intent != null) {
            client = intent.getStringExtra("client");
            if (TextUtils.isEmpty(client)) {
                client = "common";
            }
        } else {
            client = "common";
        }
        PlayServiceBridgeAIDL.Stub stub = stubs.get(client);

        if (stub == null) {
            stub = createNewPlayServiceBridgeAIDLStub();
            stubs.put(client, stub);
        }
        return stub;
    }

    private PlayServiceBridgeAIDL.Stub createNewPlayServiceBridgeAIDLStub() {
        return new PlayServiceBridgeAIDL.Stub() {
            @Override
            public synchronized void play(Music music) throws RemoteException {
                currentPlayingList.addMusic(music);
                currentPlayingList.setNowPlayingMusic(music);
                mp3Player.playMusic(music);
            }

            @Override
            public synchronized void pause() throws RemoteException {
                mp3Player.pause();
            }

            @Override
            public synchronized boolean isPlaying() throws RemoteException {
                return mp3Player.isPlaying();
            }

            @Override
            public synchronized boolean isPause() throws RemoteException {
                return mp3Player.isPause();
            }

            @Override
            public synchronized boolean isStop() throws RemoteException {
                return mp3Player.isStop();
            }

            @Override
            public synchronized int getAudioSessionId() throws RemoteException {
                return mp3Player.getAudioSessionId();
            }

            @Override
            public synchronized String getPlayListId() {
                PlayList playEasyList = dataBaseManager.getPlayEasyList(DataBaseManager.CURRENT_PLAYLIST_ID);
                if (playEasyList != null) {
                    return playEasyList.getRamark();
                }
                return null;
            }

            @Override
            public synchronized void rePause() {
                mp3Player.play();
            }

            // 播放下一曲
            @Override
            public synchronized void playNext() {
                PhoneMp3PlayService.this.next(false);
            }

            // 播放上一曲
            @Override
            public synchronized void playLast() {
                PhoneMp3PlayService.this.playLast();
            }

            @Override
            public synchronized Music getCurrentMusic() throws RemoteException {
                return mp3Player.getPlayinMusic();
            }

            // 从播放列表移除一首歌
            @Override
            public synchronized void removeFromPlayList(Music music) throws RemoteException {
                currentPlayingList.removeFromPlayingList(music);
                if (music.equals(mp3Player.getPlayinMusic())) {
                    // 移除的歌曲正好是正在播放的歌曲，这个时候进行下一曲操作
                    next(false);
                }
            }

            @Override
            public synchronized void playIndex(int index) throws RemoteException {
                Music playingMusic = currentPlayingList.getPlayingMusics().get(index);
                play(playingMusic);
            }

            @Override
            public synchronized int getCurrentMusicIndex() throws RemoteException {
                return currentPlayingList.getNowIndexInPlayingMusicsList();
            }

            @Override
            public synchronized long getCurrentMusicPosition() throws RemoteException {
                return mp3Player.getCurrentPosition();
            }

            @Override
            public int getPlayWay() throws RemoteException {
                return currentPlayingList.getPlayWay();
            }

            @Override
            public void setPlayWay(int way) throws RemoteException {
                currentPlayingList.setPlayWay(way);
            }

            @Override
            public boolean isSetTime() throws RemoteException {
                return start != null;
            }

            // @Override
            // public synchronized List<Music> getCurrentMusics() throws RemoteException {
            //   List<Music> playingMusics = currentPlayingList.getPlayingMusics();
            //   ArrayList<Music> mms = new ArrayList<>();
            //   if (playingMusics != null) {
            //     mms.addAll(playingMusics);
            //   }
            //   return mms;
            // }
            //  @Override
            //  public synchronized void setPlayingList(List<Music> musics, String playListID) throws RemoteException {
            //    if (musics != null) {
            //      currentPlayingList.setMusics(musics, playListID);
            //      Music next = currentPlayingList.getNext();
            //      currentPlayingList.setNowPlayingMusic(next);
            //      next = currentPlayingList.getNowMusic();
            //      mp3Player.playMusic(next);
            //    }
            //  }

            @Override
            public synchronized void seekTo(long position) throws RemoteException {
                mp3Player.seekTo(position);
            }

            @Override
            public synchronized void stop() throws RemoteException {
                mp3Player.stop();
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataBaseManager = DataBaseManager.getDataBaseManager(this);
        currentPlayingList = CurrentPlayingList3.getCurrentPlayingList(this);
        mp3Player = new Mp3Player();
        mp3Player.addMp3PlayListener(this);

        // 读取现有的正在播放列表是否有数据
        PlayList playList = dataBaseManager.getPlayList(DataBaseManager.CURRENT_PLAYLIST_ID);
        if (playList != null) {
            List<Music> musics = playList.getMusics();
            if (musics != null && musics.size() > 0) {
                currentPlayingList.setMusics(musics, DataBaseManager.CURRENT_PLAYLIST_ID);
                Music lastplay = currentPlayingList.getLastPlaying(getSharedPreferences().getString(CurrentPlayingList3.PLAY_ID_INGKEY, ""));
                if (null == lastplay) {
                    lastplay = currentPlayingList.getNowMusic();
                }
                currentPlayingList.setNowPlayingMusic(lastplay);
                mp3Player.setMusic(lastplay);
            }
        }
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mComponentName = new ComponentName(getPackageName(), MediaButtonRecver.class.getName());
        audioManager.registerMediaButtonEventReceiver(mComponentName);

        // requestAudioFocus();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION.ASK.EXIT);
        intentFilter.addAction(ACTION.ASK.LOVE);
        intentFilter.addAction(ACTION.ASK.PLAY$PAUSE);
        intentFilter.addAction(ACTION.ASK.NEXT_PLAY);
        intentFilter.addAction(ACTION.ASK.NEXT);
        intentFilter.addAction(ACTION.ASK.UP);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.addAction(ACTION.LOVE_CHANGE);
        intentFilter.addAction(ACTION.SET_TIMEOUT);
        intentFilter.addAction(ACTION.CLEAR_PLAYINGLIST);
        intentFilter.addAction(ACTION.CANCEL_TIMEOUT);
        intentFilter.addAction(ACTION.ASK.CONTROL.PLAY);
        intentFilter.addAction(ACTION.ASK.CONTROL.PLAYALL);
        intentFilter.addAction(ACTION.ASK.CONTROL.REMOVEFROMLISTS);
        intentFilter.setPriority(2147483647);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void requestAudioFocus() {
        // 注册音频焦点
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    private void dropAudioFocus() {
        // 丢弃音频焦点
        if (audioManager != null)
            audioManager.abandonAudioFocus(this);
    }

    private void love() {
        final Music currentMusic = mp3Player.getPlayinMusic();
        if (null != currentMusic) {
            x.task().run(new Runnable() {
                @Override
                public void run() {
                    boolean in = dataBaseManager.isInPlayList(currentMusic, LovesFragment.LOVELISTID);
                    if (in) {
                        // 取消收藏
                        dataBaseManager.removeMusicFromPlayList(LovesFragment.LOVELISTID, currentMusic);
                        Intent intent = new Intent(ACTION.LOVE_CHANGE);
                        intent.putExtra("love", false);
                        intent.putExtra("music", currentMusic);
                        sendBroadcast(intent);

                    } else {
                        // 收藏
                        dataBaseManager.addMusicToPlayList(LovesFragment.LOVELISTID, currentMusic);
                        Intent intent = new Intent(ACTION.LOVE_CHANGE);
                        intent.putExtra("love", true);
                        intent.putExtra("music", currentMusic);
                        sendBroadcast(intent);
                    }
                }
            });
        }
    }

    private AbsTask<Object> start;

    // 设定定时退出时间
    private void setTimeOut(final long time) {
        start = x.task().start(new AbsTask<Object>() {
            @Override
            protected Object doBackground() throws Throwable {
                SystemClock.sleep(time);
                return null;
            }

            @Override
            protected void onSuccess(Object result) {
                if (mp3Player != null) {
                    mp3Player.stop();
                    Tool.cancelPlayNotify(PhoneMp3PlayService.this);
                    start = null;
                }
            }

            @Override
            protected void onError(Throwable ex, boolean isCallbackError) {

            }
        });

        Toast.makeText(this, "将在" + Tool.parseTime2(time) + "后停止播放", Toast.LENGTH_SHORT).show();
    }

    // 取消定时
    private void cancelTime() {
        if (start != null) {
            start.cancel();
            start = null;
            Toast.makeText(this, "已取消定时", Toast.LENGTH_SHORT).show();
        }
    }

    // 接收通知栏的按钮事件广播
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION.CLEAR_PLAYINGLIST.equals(action)) {
                currentPlayingList.clearPlayList();
                mp3Player.empty();
            } else if (ACTION.CANCEL_TIMEOUT.equals(action)) {
                cancelTime();
            } else if (ACTION.SET_TIMEOUT.equals(action)) {
                long longExtra = intent.getLongExtra(ACTION.EXTRA_TIME, 0);
                if (longExtra > 0) {
                    setTimeOut(longExtra);
                }
            } else if (ACTION.LOVE_CHANGE.equals(action)) {
                Music music = intent.getParcelableExtra("music");
                boolean islove = intent.getBooleanExtra("love", false);
                if (music != null && music.equals(mp3Player.getPlayinMusic())) {
                    Bitmap ablumBitmap = Tool.getAblumBitmap(PhoneMp3PlayService.this, music.getAlbum_id());
                    Tool.showSystemStatusBarPlayNotify(PhoneMp3PlayService.this, islove ? "已收藏" : "取消收藏", ablumBitmap, music.getTitle(), music.getArtist(), true, islove);
                }
            } else if (ACTION.ASK.EXIT.equals(action)) {
                if (mp3Player != null) {
                    mp3Player.stop();
                    Tool.cancelPlayNotify(PhoneMp3PlayService.this);
                }
            } else if (ACTION.ASK.LOVE.equals(action)) {
                love();
                // 收藏
            } else if (ACTION.ASK.NEXT.equals(action)) {
                // 下一曲
                next(false);
            } else if (ACTION.ASK.PLAY$PAUSE.equals(action)) {
                // 暂停播放
                play$pause();
            } else if (ACTION.ASK.UP.equals(action)) {
                // 上一曲
                playLast();
            } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                // 耳机插拔
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        // 耳机拔出
                        mp3Player.pause();
                        audioManager.unregisterMediaButtonEventReceiver(mComponentName);
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        // Log.i("MediaPlayer", "耳机插入");
                        // 耳机插入不自动播放
                        // mp3Player.play();
                        audioManager.registerMediaButtonEventReceiver(mComponentName);
                    }
                }
            } else if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {

                // 获得KeyEvent对象
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                // 获得按键码
                int keycode = event.getKeyCode();

                switch (keycode) {
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        //播放下一首
                        next(false);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        //播放上一首
                        playLast();
                        break;
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                        //中间按钮,暂停or播放
                        //可以通过发送一个新的广播通知正在播放的视频页面,暂停或者播放视频
                        // Log.i("MediaPlayer", "耳机暂停");
                        play$pause();
                        break;
                    default:
                        break;
                }
            } else if (ACTION.ASK.CONTROL.PLAY.equals(action)) {
                Music music = intent.getParcelableExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC);
                currentPlayingList.addMusic(music);
                currentPlayingList.setNowPlayingMusic(music);
                mp3Player.playMusic(music);
            } else if (ACTION.ASK.CONTROL.PLAYALL.equals(action)) {
                String playlistId = DataBaseManager.CURRENT_PLAYLIST_ID;
                PlayList playList = dataBaseManager.getPlayList(playlistId);
                PlayList playList1 = DataBaseManager.getDataBaseManager(PhoneMp3PlayService.this).getPlayList(DataBaseManager.CURRENT_PLAYLIST_ID);
                if (playList != null) {
                    List<Music> musics = playList.getMusics();
                    if (musics != null && musics.size() > 0) {
                        currentPlayingList.setMusics(musics, playlistId);
                        Music next = currentPlayingList.getNowMusic();
                        currentPlayingList.setNowPlayingMusic(next);
                        mp3Player.playMusic(next);
                    } else {
                        Toast.makeText(PhoneMp3PlayService.this, "播放列表没有歌曲", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhoneMp3PlayService.this, "播放列表读取失败", Toast.LENGTH_SHORT).show();
                }
            } else if (ACTION.ASK.CONTROL.REMOVEFROMLISTS.equals(action)) {
                // 移除一首歌曲
                Music music = intent.getParcelableExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC);
                currentPlayingList.removeFromPlayingList(music);
                if (music.equals(mp3Player.getPlayinMusic())) {
                    // 移除的歌曲正好是正在播放的歌曲，这个时候进行下一曲操作
                    next(false);
                } else {
                    Intent intent1 = new Intent(ACTION.PLAYINDEX_CHANGE);
                    intent1.putExtra(ACTION.EXTRA_INDEx, currentPlayingList.getNowIndexInPlayingMusicsList());
                    sendBroadcast(intent1);
                }
            } else if (ACTION.ASK.NEXT_PLAY.equals(action)) {
                Music m = intent.getParcelableExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC);
                if (m == null) {
                    m = intent.getParcelableExtra("music");
                }
                currentPlayingList.add2Next_(m);
            }
        }
    };

    // 下一曲
    private void next(boolean fromcomputer) {
        if (currentPlayingList.hasMore()) {
            Music next = null;
            next = currentPlayingList.getNext_(fromcomputer);
            currentPlayingList.setNowPlayingMusic(next);
            mp3Player.playMusic(next);
        } else {
            // m没有歌曲了，停止播放
            // mp3Player.empty();
            Toast.makeText(this, "没有更多歌曲了", Toast.LENGTH_SHORT).show();
        }
    }

    // 播放暂停
    private void play$pause() {
        Log.i("MediaPlayer", "服务中: play$pause");
        if (mp3Player.isPlaying()) {
            mp3Player.pause();
        } else {
            mp3Player.play();
        }
    }

    // 播放上一曲
    private void playLast() {
        if (currentPlayingList.hasMore()) {
            Music next = currentPlayingList.getLast();
            currentPlayingList.setNowPlayingMusic(next);
            mp3Player.playMusic(next);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            dropAudioFocus();
            mp3Player.release();
            // mp3Player = null;
            unregisterReceiver(broadcastReceiver);
            audioManager.unregisterMediaButtonEventReceiver(mComponentName);
            Tool.cancelPlayNotify(this);
            dataBaseManager.superClose();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void beforePlaying(Mp3Player mp3Player) {
        requestAudioFocus();
        audioManager.registerMediaButtonEventReceiver(mComponentName);
        final Music playinMusic = mp3Player.getPlayinMusic();
        sendBrodcast(ACTION.BEFOR_PLAY, playinMusic);
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("isplaying", true).putString(CurrentPlayingList3.PLAY_ID_INGKEY, playinMusic.get_id()).commit();

        boolean inPlayList = dataBaseManager.isInPlayList(playinMusic, PlayhistoryFragment.HISTORY$ID);
        if (inPlayList) {
            // 在历史记录，则更新时间
            dataBaseManager.updateHistoryMusicDate(playinMusic);
        } else {
            // 不在记录里，添加
            dataBaseManager.addMusicToPlayList(PlayhistoryFragment.HISTORY$ID, playinMusic);
        }
        int i = dataBaseManager.countOfPlayList(PlayhistoryFragment.HISTORY$ID);
        if (i > PlayhistoryFragment.MaxHistoryCount) {
            int i1 = dataBaseManager.deleteTheMinMusic();
            Log.i("aaaa", "删除最老播放的一首歌曲 受影响的行数:" + i1);
        }


        Bitmap ablumBitmap = Tool.getAblumBitmap(this, playinMusic.getAlbum_id());
        Tool.showSystemStatusBarPlayNotify(this, "开始播放", ablumBitmap, playinMusic.getTitle(), playinMusic.getArtist(), true, dataBaseManager.isInPlayList(playinMusic, LovesFragment.LOVELISTID));
        if (ablumBitmap == null) {
            // 专辑图片如果是空 ，尝试从mp3文件中读取
            x.task().run(new Runnable() {
                @Override
                public void run() {
                    String abid = playinMusic.getAlbum_id();
                    //Log.i("aaaa", "没有专辑图" + abid);
                    try {
                        File t = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.android.providers.media/albumthumbs/");
                        if (!t.exists()) {
                            t.mkdirs();
                        }
                        //Log.i("aaaa", "获取专辑图" + abid);
                        // File mp3Ablum = Tool.getMp3Ablum(new File(playinMusic.get_data()), t);

                        //Log.i("aaaa", "专辑图" + abid + "获取成功");
                        // if (mp3Ablum != null) {
                        //     boolean b = Tool.replarAblum(abid, mp3Ablum, getApplication());
                        //     if (b) {
                        //         //Log.i("aaaa", "修复完成");
                        //         if (playinMusic.getAlbum_id().equals(abid)) {
                        //             x.task().post(new Runnable() {
                        //                 @Override
                        //                 public void run() {
                        //                     Tool.showSystemStatusBarPlayNotify(getApplicationContext(), "开始播放", Tool.getAblumBitmap(getApplicationContext(), playinMusic.getAlbum_id()), playinMusic.getTitle(), playinMusic.getArtist(), true, dataBaseManager.isInPlayList(playinMusic, LovesFragment.LOVELISTID));
                        //                 }
                        //             });
                        //         }
                        //     }
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onPause(Mp3Player mp3Player) {
        Music playinMusic = mp3Player.getPlayinMusic();

        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("isplaying", false).commit();
        Tool.showSystemStatusBarPlayNotify(this, "暂停播放", Tool.getAblumBitmap(this, playinMusic.getAlbum_id()), playinMusic.getTitle(), playinMusic.getArtist(), false, dataBaseManager.isInPlayList(playinMusic, LovesFragment.LOVELISTID));
        sendBrodcast(ACTION.PAUSE_PLPAY);
    }

    @Override
    public void afterStop(Mp3Player mp3Player, boolean fromuser) {
        HashMap<String, String> map = new HashMap<>();
        map.put("fromuser", fromuser + "");
        sendBrodcast(ACTION.AFTER_PLAY, map);
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putBoolean("isplaying", false).commit();
        if (!fromuser) {
            next(true);
        }
    }

    @Override
    public void onEmpty(Mp3Player mp3Player) {
        sendBrodcast(ACTION.PLAY_FLISH);
        Tool.cancelPlayNotify(this);
    }

    private void sendBrodcast(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void sendBrodcast(String action, Music m) {
        Intent intent = new Intent(action);
        intent.putExtra("music", m);
        intent.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, m);
        sendBroadcast(intent);
    }

    private void sendBrodcast(String action, Map<String, String> values) {
        Intent intent = new Intent(action);

        if (values != null && values.size() > 0) {
            Set<Map.Entry<String, String>> entries = values.entrySet();
            for (Map.Entry<String, String> e : entries) {
                intent.putExtra(e.getKey(), e.getValue());
            }
        }
        sendBroadcast(intent);
    }

    private int state;
    private boolean hasheadset; // 失去焦点前是否插入耳机

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN: // 完全获取了音频焦点
                if (state == Mp3Player.STATE_PLAYING && hasheadset == Tool.hasHeadset(PhoneMp3PlayService.this)) {
                    mp3Player.play();
                    state = 0;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: // 你的焦点会短暂失去，但是你可以与新的使用者共同使用音频焦点
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: // 会短暂的失去焦点
            case AudioManager.AUDIOFOCUS_LOSS: // 完全失去了音频焦点
                state = mp3Player.getState();
                mp3Player.pause();
                hasheadset = Tool.hasHeadset(PhoneMp3PlayService.this);
                break;

        }
    }


}
