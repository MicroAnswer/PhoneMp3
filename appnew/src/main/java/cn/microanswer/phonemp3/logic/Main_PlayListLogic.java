package cn.microanswer.phonemp3.logic;

import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.ui.Main_PlayListPage;

public interface Main_PlayListLogic extends Logic<Main_PlayListPage> {

    /**
     * 完成保存一个新播放列表的功能。
     *
     * @param remark 播放列表备注。（此字段实际上是用户输入的名字。显示的时候也显示为列表名字，但是存到remark字段）
     */
    void onSaveMyPlayList(String remark, OnSaveMyPlayListListener playListListener);

    /**
     * 保存播放列表监听
     */
    interface OnSaveMyPlayListListener {


        /**
         * 结果回调
         *
         * @param status   0 = 已存在，1=成功， 2=失败
         * @param playList 成功后有
         * @param msg      失败后有
         */
        void onComp(int status, PlayList playList, String msg);
    }
}
