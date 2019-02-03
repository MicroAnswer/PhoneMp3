package cn.microanswer.phonemp3.ui;

import java.util.List;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.Main_AllMusic_M_Logic;

public interface Main_AllMucis_M_Page extends Page<Main_AllMusic_M_Logic> {

    void showLoading(boolean anim);

    void showEmpty(boolean anim);

    void setScanHint(String txt);

    void showData(boolean anim,List<Music> data);

    void requestPermission(String[] ps);
}
