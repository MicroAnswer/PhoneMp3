package cn.microanswer.phonemp3.logic;

import java.io.File;

import cn.microanswer.phonemp3.ui.LogPage;

public interface LogLogic extends Logic<LogPage> {

    // 当刷新按钮点击时调用
    // 此方法应该加载日志并显示。
    void onRefreshClick();

    void onLogItemClick(int position, File f);

    // 处理清空日志按钮点击事件
    void doClearLog();
}
