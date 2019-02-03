package cn.microanswer.phonemp3.logic;

import cn.microanswer.phonemp3.ui.MainPage;

public interface MainLogic extends Logic<MainPage> {
    void onMenuClick(int menuId);
    void onExitClick();
    void onPlayControlerClick();

    void onBtnLoveClick();

    void onBtnNextClick();

    void onPausePlayClick();
}
