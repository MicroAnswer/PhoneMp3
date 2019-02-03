package cn.microanswer.phonemp3.logic;

import android.view.View;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.ui.Main_AllMucis_M_Page;

public interface Main_AllMusic_M_Logic extends Logic<Main_AllMucis_M_Page> {

    void onBtnScannClick();

    void onPermissionsResult(String[] permissions, int[] grantResults);

    void onMusicItemClick(View v, Music music, int position);

    void doItemMenuClick(Music music, int position, int itemMenuId);
}
