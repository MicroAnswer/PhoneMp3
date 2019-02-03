package answer.android.phonemp3.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import answer.android.phonemp3.bean.Music;

/**
 * 正在播放列表工具类
 * Created by Microanswer on 2017/6/16.
 */
public class CurrentPlayingList {
    /**
     * 配置文件中配置播放顺序的key
     */
    public final static String PREFERENCE_KEY_PLAYWAY = "key_play_way";
    private final String TAG = "CurrentPlayingList";
    /**
     * 顺序播放
     */
    public static final int PLAY_LIST = 1;
    /**
     * 随机播放
     */
    public static final int PLAY_RANDOM = 2;
    /**
     * 列表循环
     */
    public static final int PLAY_RELIST = 3;
    /**
     * 单曲循环
     */
    public static final int PLAY_RESINGLE = 4;

    // 正在播放列表
    private ArrayList<Music> playingMusics;

    // 正在播放列表中还没有播放的歌曲
    private ArrayList<Music> didnotPlays;

    // 已经播放过的歌曲
    private ArrayList<Music> playeds;

    // 当前正在播放的歌曲
    private Music now;

    // 当前播放的歌曲在列表中的位置
    private int nowIndexInPlayingMusicsList = -1;

    // 播放列表id
    private String playlistId;

    /**
     * 播放方式
     */
    private int playWay = PLAY_LIST;

    private CurrentPlayingList(Context c) {
        // 取到默认配置文件
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        if (defaultSharedPreferences.contains(PREFERENCE_KEY_PLAYWAY)) {
            // 获取播放方式[默认顺序播放]
            playWay = defaultSharedPreferences.getInt(PREFERENCE_KEY_PLAYWAY, PLAY_LIST);
        } else {
            playWay = PLAY_LIST;
            defaultSharedPreferences.edit().putInt(PREFERENCE_KEY_PLAYWAY, playWay).apply();
        }

        didnotPlays = new ArrayList<>();
        playeds = new ArrayList<>();
    }

    public int getPlayWay() {
        return playWay;
    }

    public void setPlayWay(int playWay) {
        this.playWay = playWay;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void removeFromPlayingList(Music music) {
        if (playingMusics != null && playingMusics.contains(music)) {
            playingMusics.remove(music);
        }
        if (playeds.contains(music)) {
            playeds.remove(music);
        }
        if (didnotPlays.contains(music)) {
            didnotPlays.remove(music);
        }
    }

    public Music removeFromPlayingList(int index) {
        if (playingMusics != null) {
            Music remove = playingMusics.remove(index);
            removeFromPlayingList(remove);
            return remove;
        }
        return null;
    }


    public int getNowIndexInPlayingMusicsList() {
        return nowIndexInPlayingMusicsList;
    }

    /**
     * 添加一首歌曲
     *
     * @param music
     */
    public void addMusic(Music music) {
        if (playingMusics == null) {
            playingMusics = new ArrayList<>();
        }
        // 播放列表里面没有,添加
        if (!playingMusics.contains(music)) {
            playingMusics.add(music);
        }
        // 未播放集合里面没有,添加
        if (!didnotPlays.contains(music)) {
            didnotPlays.add(music);
        }
        // 已播放列表里面有,移除
        if (playeds.contains(music)) {
            playeds.remove(music);
        }
    }

    /**
     * 设置当前正在播放的歌曲
     *
     * @param music
     */
    public void setNowPlayingMusic(Music music) {
        if (now != null) {
            if (!playeds.contains(now)) {
                playeds.add(now);
            }
        }
        if (music != null) {
            if (playingMusics != null && didnotPlays != null && didnotPlays.size() > 0) {
                switch (playWay) {
                    case PLAY_LIST:
                        int i = didnotPlays.indexOf(music);
                        if (i == 0) {
                            now = didnotPlays.remove(0);
                        } else {
                            if (playingMusics.contains(music)) {
                                int j = playingMusics.indexOf(music);
                                playeds.clear();
                                // 前面的加入已播放
                                for (int a = 0; a < j; a++) {
                                    playeds.add(playingMusics.get(a));
                                }
                                // 后面的加入未播放
                                didnotPlays.clear();
                                for (int b = j; b < playingMusics.size(); b++) {
                                    didnotPlays.add(playingMusics.get(b));
                                }
                                now = didnotPlays.remove(0);
                            }
                        }
                        break;
                    case PLAY_RELIST:
                        int i1 = playingMusics.indexOf(music);
                        if (i1 >= 0) {
                            now = playingMusics.get(0);
                        } else {
                            now = null;
                        }
                        break;
                    case PLAY_RANDOM:
                        int i2 = didnotPlays.indexOf(music);
                        if (i2 >= 0) {
                            now = didnotPlays.remove(i2);
                        } else {
                            if (playeds.contains(music) && playeds.remove(music)) {
                                if (playingMusics.contains(music)) {
                                    didnotPlays.add(0, music);
                                    Collections.sort(didnotPlays);
                                    now = music;
                                } else {
                                    now = null;
                                }
                            } else {
                                now = null;
                            }
                        }
                        break;
                    case PLAY_RESINGLE:
                        if (playingMusics.contains(music)) {
                            now = music;
                        } else {
                            now = null;
                        }
                }
            }
        }

        if (now != null && playingMusics != null) {
            nowIndexInPlayingMusicsList = playingMusics.indexOf(now);
        } else {
            nowIndexInPlayingMusicsList = -1;
        }
    }

    /**
     * 获取当前正在播放的歌曲
     *
     * @return
     */
    public Music getNowMusic() {
        return now;
    }

    /**
     * 获取正在播放列表内容
     *
     * @return
     */
    public List<Music> getPlayingMusics() {
        return playingMusics;
    }

    /**
     * 获取上一首播放的歌曲
     *
     * @return
     */
    public Music getLast() {
        if (playeds.size() > 0) {
            return playeds.get(playeds.size() - 1);
        }
        return null;
    }

    /**
     * 获取下一曲
     *
     * @return 下一曲
     */
    public Music getNext() {
        return getNext(0);
    }

    /**
     * 从正在播放的列表中获取下一曲应该播放的歌曲
     *
     * @param offset 与当前播放的歌曲的便宜歌曲数目,比如下2曲传1,下3曲传2...,下一曲传0,或者不传
     * @return 返回应该播放的下一曲
     */
    public Music getNext(int offset) {
        Music next = null;
        // 根据播放方式决定下一曲
        switch (playWay) {
            case PLAY_LIST:
                if (playingMusics != null && didnotPlays != null && didnotPlays.size() > offset) {
                    next = didnotPlays.get(offset);
                }
                break;
            case PLAY_RELIST:
                if (playingMusics != null && didnotPlays != null && didnotPlays.size() > 0) {
                    if (nowIndexInPlayingMusicsList == -1) {
                        nowIndexInPlayingMusicsList = 0;
                    }

                    // 如果全部歌曲有5首 0 1 2 3 4
                    // 现在播放到了第4首       ^
                    // 要获取下2首歌曲  [0] 1 2 3 4
                    // 下表为0
                    int index = offset + nowIndexInPlayingMusicsList + 1;
                    if (index >= playingMusics.size()) {
                        index = index - playingMusics.size();
                    }
                    next = playingMusics.get(index);
                }
                break;
            case PLAY_RANDOM:
                if (playingMusics != null && didnotPlays != null && didnotPlays.size() > 0) {
                    int index = (int) (Math.random() * (didnotPlays.size() - 1));
                    for (int i = 0; i < offset; i++) {
                        index = (int) (Math.random() * (didnotPlays.size() - 1));
                    }
                    next = didnotPlays.get(index);
                }
                break;
            case PLAY_RESINGLE:
                return now;
        }
        return next;
    }

    /**
     * 设置正在播放列表内容
     *
     * @param musics     内容
     * @param playListId 播放列表id
     */
    public void setMusics(List<Music> musics, String playListId) {
        // 没有内容传入,不做任何处理
        if (musics == null || musics.size() < 1) {
            return;
        }

        if (playingMusics == null) {
            playingMusics = new ArrayList<>();
        }

        playingMusics.clear(); // 清空现在的播放列表
        didnotPlays.clear(); // 清空未播放的集合
        playeds.clear();// 清空已经播放的集合
        now = null; // 清空正在播放的歌曲
        nowIndexInPlayingMusicsList = -1; // 清空当前播放位置的下表


        playingMusics.addAll(musics); // 将传入的播放列表添加进来
        didnotPlays.addAll(musics);// 所有歌曲视为都还没有播放过

        // 赋值播放列表id
        if (!TextUtils.equals(this.playlistId, playListId)) {
            this.playlistId = playListId;
        }
    }

    /**
     * 播放列表中是否还有可以提供播放的歌曲
     *
     * @return true 还有可播放的
     */
    public boolean hasMore() {
        switch (playWay) {
            case PLAY_LIST:
            case PLAY_RANDOM:
                return didnotPlays.size() > 0;
            case PLAY_RELIST:
            case PLAY_RESINGLE:
                return playingMusics.size() > 0;
        }
        return false;
    }


    // 单列===
    private static CurrentPlayingList list;

    public static CurrentPlayingList getCurrentPlayingList(Context c) {
        if (list == null) {
            list = new CurrentPlayingList(c);
        }
        return list;
    }

    // 添加到下一曲播放
    public void add2Next(Music m) {
        // TODO
    }
}
