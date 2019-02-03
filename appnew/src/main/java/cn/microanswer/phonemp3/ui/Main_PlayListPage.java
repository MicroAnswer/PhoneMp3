package cn.microanswer.phonemp3.ui;

import java.util.List;

import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.logic.Main_PlayListLogic;

public interface Main_PlayListPage extends Page<Main_PlayListLogic> {

    void showLoading();

    void showData(List<PlayList> lists);

    void showEmpty();


}
