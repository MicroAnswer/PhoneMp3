package cn.microanswer.phonemp3.ui;

import cn.microanswer.phonemp3.logic.AboutLogic;

public interface AboutPage extends Page<AboutLogic> {
    void displayVersion(String version);
}
