package cn.microanswer.phonemp3.logic;

import cn.microanswer.phonemp3.ui.AboutPage;

public interface AboutLogic extends Logic<AboutPage> {
    String getVersion();

    void onItemClick(int id);
}
