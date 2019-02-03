package cn.microanswer.phonemp3.ui;

import cn.microanswer.phonemp3.logic.WebLogic;

public interface WebPage extends Page<WebLogic> {
    void loadUrl(String url);
}
