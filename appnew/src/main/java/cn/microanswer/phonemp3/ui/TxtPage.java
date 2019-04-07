package cn.microanswer.phonemp3.ui;

import cn.microanswer.phonemp3.logic.TxtLogic;

// 显示文本内容的页面
public interface TxtPage extends Page<TxtLogic> {
    void showLoading();
    void showTxt(String txt);
}
