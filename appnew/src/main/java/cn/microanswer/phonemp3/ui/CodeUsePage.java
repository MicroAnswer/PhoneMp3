package cn.microanswer.phonemp3.ui;

import cn.microanswer.phonemp3.logic.CodeUseLogic;

public interface CodeUsePage extends Page<CodeUseLogic> {
    void showLoading();

    void showCodes(String[] titles, String[] descs);
}
