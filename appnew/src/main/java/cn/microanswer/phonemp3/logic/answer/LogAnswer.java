package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.microanswer.phonemp3.PhoneMp3Application;
import cn.microanswer.phonemp3.logic.LogLogic;
import cn.microanswer.phonemp3.ui.LogPage;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.Task;

public class LogAnswer extends BaseAnswer<LogPage> implements LogLogic {
    private static Logger logger = Logger.getLogger(LogAnswer.class);

    // 日志文件列表
    // 此列表中不会包含今天的日志文件。
    private static List<File> fileList;

    public LogAnswer(LogPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadAndDisplayLogList();
    }

    // 加载并显示日志数据。
    private void loadAndDisplayLogList() {
        if (fileList != null && !fileList.isEmpty()) {
            getPage().showLogList(fileList);
        } else {
            getPage().showLoading();
            Task.TaskHelper.getInstance().run(new Task.ITask<Object, List<File>>() {
                @Override
                public List<File> run(Object param) throws Exception {
                    String logFileName = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(new Date()) + ".log";
                    File logDir = PhoneMp3Application.DIR_LOG;
                    File[] listFiles = logDir.listFiles(pathname -> !pathname.getName().equals(logFileName));
                    return Arrays.asList(listFiles);
                }

                @Override
                public void afterRun(List<File> value) {
                    super.afterRun(value);
                    if (value != null && !value.isEmpty()) {
                        getPage().showLogList(value);
                    } else {
                        getPage().showEmpty();
                    }
                }
            });

        }
    }

    @Override
    public void onRefreshClick() {
        loadAndDisplayLogList();
    }
}
