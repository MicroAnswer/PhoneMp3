package cn.microanswer.phonemp3.ui;

import cn.microanswer.phonemp3.logic.ScannLogic;

public interface ScannPage extends Page<ScannLogic> {
    void showLoading();
    void setScannHint(String txt);
    void showResult(String txt);
}
