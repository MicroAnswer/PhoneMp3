package cn.microanswer.phonemp3.ui;

import java.util.List;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.Main_MyLoveLogic;

public interface Main_MyLovePage extends Page<Main_MyLoveLogic> {
    void showLoading(boolean anim);

    void showEmpty(boolean anim);

    void showData(boolean anim, List<Music> data);

    /**
     * 添加一首收藏的歌曲。显示到界面
     *
     * @param value
     */
    void addLoved(Music value);

    /**
     * 从界面移除一首歌曲。
     *
     * @param value
     */
    void removeLoved(Music value);
}
