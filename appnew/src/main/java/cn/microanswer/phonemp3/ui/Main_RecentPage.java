package cn.microanswer.phonemp3.ui;

import java.util.List;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.Main_RecentLogic;

public interface Main_RecentPage extends Page<Main_RecentLogic> {
    void showLoading(boolean anim);

    void showEmpty(boolean anim);

    void showData(boolean anim, List<Music> data);

    /**
     * 将指定位置的歌曲移动到另一位置
     *
     * @param from 起始位置
     * @param i    目标位置
     */
    void move(int from, int i);

    /**
     * 添加一首歌曲到指定位置
     *
     * @param i      目标位置
     * @param object 歌曲
     */
    void add(int i, Music object);

    /**
     * 有一首歌曲播放了
     *
     * @param object 歌曲
     */
    void newMusicPlayed(Music object);
}
