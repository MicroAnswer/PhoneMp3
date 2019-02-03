package cn.microanswer.phonemp3.logic;

import android.view.View;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.ui.Main_MyLovePage;

public interface Main_MyLoveLogic extends Logic<Main_MyLovePage> {
    void onItemClick(View v, Music music, int position);
}
