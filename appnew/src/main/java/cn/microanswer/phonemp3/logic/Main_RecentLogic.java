package cn.microanswer.phonemp3.logic;

import android.view.View;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.ui.Main_RecentPage;

public interface Main_RecentLogic extends Logic<Main_RecentPage> {
    /**
     * 某首歌曲条目点击
     *
     * @param v        界面
     * @param music    被点击的歌曲
     * @param position 位置
     */
    void onMusicItemClick(View v, Music music, int position);
}
