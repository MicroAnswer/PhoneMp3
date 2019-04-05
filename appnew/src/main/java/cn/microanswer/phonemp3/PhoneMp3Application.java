package cn.microanswer.phonemp3;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;

import cn.microanswer.phonemp3.util.CrashHandler;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class PhoneMp3Application extends Application {
    private Logger logger;
    public static File DIR_LOG = null; // 日志文件所在的文件夹
    public static File DIR_SET = null; // 设置内容文件所在的文件夹

    @Override
    public void onCreate() {
        super.onCreate();

        // 日志目录
        DIR_LOG = new File(getFilesDir().getAbsolutePath(), "logs");

        // 设置目录
        DIR_SET = new File(getFilesDir().getAbsolutePath(), "set");

        logger = new Logger(PhoneMp3Application.class);

        // 初始化设置值
        Task.TaskHelper.getInstance().run(new Task.ITask<Object, Object>() {
            @Override
            public Object run(Object param) throws Exception {
                SettingHolder.getSettingHolder().init();
                return null;
            }
        });

        // 设定程序崩溃时异常搜集
        CrashHandler crashHandler = new CrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        // 初始化数据库框架
        FlowManager.init(this);

        logger.i("程序启动：" + Utils.COMMON.getDateStr("yyyy年MM月dd HH:mm:ss"));
    }
}
