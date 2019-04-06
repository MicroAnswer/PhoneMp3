package cn.microanswer.phonemp3.ui;

import java.io.File;
import java.util.List;

import cn.microanswer.phonemp3.logic.LogLogic;

public interface LogPage extends Page<LogLogic> {

    // 显示日志列表
    void showLogList(List<File> fileList);

    // 显示日志加载提示
    void showLoading();

    // 显示空数据
    void showEmpty();
}
