package answer.android.phonemp3.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import answer.android.phonemp3.bean.Music;

/**
 * 正在播放列表工具类
 * Created by Microanswer on 2017/6/16.
 */
public class CurrentPlayingList3 {
    public static final String PLAY_ID_INGKEY = "kljsdfg";
    /**
     * 配置文件中配置播放顺序的key
     */
    public final static String PREFERENCE_KEY_PLAYWAY = "key_play_way";
    private final String TAG = "CurrentPlayingList2";
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

    // 当前正在播放的歌曲
    private Music now;

    // 下一首要播放的歌曲
    private Music next;

    // 播放列表id
    private String playlistId;

    /**
     * 播放方式
     */
    private int playWay = PLAY_LIST;

    private CurrentPlayingList3(Context c) {
        // 取到默认配置文件
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        if (defaultSharedPreferences.contains(PREFERENCE_KEY_PLAYWAY)) {
            // 获取播放方式[默认顺序播放]
            playWay = defaultSharedPreferences.getInt(PREFERENCE_KEY_PLAYWAY, PLAY_LIST);
            Log.i("Microanswer", "读取到设置的播放方式:" + playWay);
        } else {
            playWay = PLAY_LIST;
            defaultSharedPreferences.edit().putInt(PREFERENCE_KEY_PLAYWAY, playWay).commit();
            Log.i("Microanswer", "未设置播放方式:" + playWay);
        }

        playingMusics = new ArrayList<>();
    }

    public int getPlayWay() {
        return playWay;
    }

    public void setPlayWay(int playWay) {

        if (playWay != 1 && playWay != 2 && playWay != 3 && playWay != 4) {
            throw new RuntimeException("未知的播放方式");
        }

        this.playWay = playWay;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    private void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void removeFromPlayingList(Music music) {
        playingMusics.remove(music);
    }

    public Music removeFromPlayingList(int index) {
        Music remove = playingMusics.get(index);
        removeFromPlayingList(remove);
        return remove;
    }


    public int getNowIndexInPlayingMusicsList() {
        if (now == null) {
            return 0;
        } else {
            return playingMusics.indexOf(now);
        }
    }

    /**
     * 添加一首歌曲
     *
     * @param music
     */
    public void addMusic(Music music) {
        if (!playingMusics.contains(music)) {

            // 直接添加到末尾显得非常不优雅
            playingMusics.add(music);

            // 应该添加到当前播放歌曲的后一个位置, 涉及到播放列表顺序展示问题
            // if (now !=null) {
            //     playingMusics.add(getNowIndexInPlayingMusicsList()+1, music);
            // } else {
            //     // 否则添加到第一个
            //     playingMusics.add(0, music);
            // }

            this.playlistId = "--";
        }
    }

    /**
     * 设置当前正在播放的歌曲
     *
     * @param music
     */
    public void setNowPlayingMusic(Music music) {
        this.now = music;
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
        int index = getNowIndexInPlayingMusicsList();
        if (index == 0) {
            return null;
        } else
            return playingMusics.get(index - 1);
    }

    // 获取关闭软件后重新打开软件以后，上次播放的歌曲
    public Music getLastPlaying(String id) {
        for (Music m : playingMusics) {
            if (id.equals(m.get_id())) {
                return m;
            }
        }
        return null;
    }

    /**
     * 获取下一曲
     *
     * @return 下一曲
     */
    public Music getNext_(boolean fromComputer) {
        if (next != null) {
            Music n = next;
            next = null;
            return n;
        } else {

            // 产生下一曲

            if (playWay == PLAY_LIST || playWay == PLAY_RELIST) {
                // 按列表播放， 播放到最后一首歌曲过后停止播放， 就不返回next了
                if (null == now) {
                    return playingMusics.get(0);
                } else {

                    if (getNowIndexInPlayingMusicsList() == playingMusics.size() - 1) {
                        // 已经播放到了最后一首歌曲了
                        if (playWay == PLAY_LIST) {
                            // 不返回了
                            next = null;
                            return null;
                        } else {
                            return playingMusics.get(0);
                        }
                    } else {
                        return playingMusics.get(getNowIndexInPlayingMusicsList() + 1);
                    }
                }

            } else if (playWay == PLAY_RESINGLE) {
                if (now != null && fromComputer) {
                    return now;
                } else {
                    if (getNowIndexInPlayingMusicsList() == playingMusics.size() - 1) {
                        return playingMusics.get(0);
                    } else {
                        return playingMusics.get(getNowIndexInPlayingMusicsList() + 1);
                    }
                }
            } else if (playWay == PLAY_RANDOM) {
                if (playingMusics.size() == 0) {
                    return null;
                }
                return playingMusics.get(Tool.randomInt(playingMusics.size()));
            } else {
                throw new RuntimeException("查找下一曲失败：未知的播放方式");
            }
        }
    }

    /**
     * 设置正在播放列表内容
     *
     * @param musics     内容
     * @param playListId 播放列表id
     */
    public void setMusics(List<Music> musics, String playListId) {
        this.playingMusics.clear();
        this.playingMusics.addAll(musics);
        this.playlistId = playListId;
        if (playWay == PLAY_RANDOM) {
            this.now = playingMusics.get(Tool.randomInt(playingMusics.size()));
        } else {
            this.now = playingMusics.get(0);
        }
    }

    /**
     * 播放列表中是否还有可以提供播放的歌曲
     *
     * @return true 还有可播放的
     */
    public boolean hasMore() {
        return playingMusics.size() > 0 && !(playWay == PLAY_LIST && getNowIndexInPlayingMusicsList() == playingMusics.size() - 1);
    }


    // 单列===
    private static CurrentPlayingList3 list;

    public static CurrentPlayingList3 getCurrentPlayingList(Context c) {
        if (list == null) {
            list = new CurrentPlayingList3(c);
        }
        return list;
    }

    // 添加到下一曲播放
    public void add2Next_(Music m) {
        next = m;
    }

    public void clearPlayList() {
        playingMusics.clear();
        now = null;
        next = null;
    }
}
