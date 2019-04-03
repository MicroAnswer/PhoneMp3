package cn.microanswer.phonemp3.logic;

import android.os.Bundle;

import cn.microanswer.phonemp3.ui.Page;

public interface Logic<P extends Page> {

    /**
     * 获取逻辑处理器对应的界面
     *
     * @return
     */
    P getPage();

    /**
     * 页面打开完整过后，该方法调起
     */
    void onPageCreated(Bundle savedInstanceState, Bundle arguments);

    void onResume();
}
