package cn.microanswer.phonemp3;

import android.app.Application;

import com.bumptech.glide.request.RequestOptions;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;

import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.util.CrashHandler;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class PhoneMp3Application extends Application {
    public static File DIR_LOG = null; // 日志文件所在的文件夹

    // Glide 图片加载选项。
    public static final RequestOptions REQUEST_OPTIONS = new RequestOptions()
            .placeholder(R.drawable.icon_ablem)
            .error(R.drawable.icon_ablem)
            .fitCenter();

    @Override
    public void onCreate() {
        super.onCreate();

        // 日志目录
        DIR_LOG = new File(getFilesDir().getAbsolutePath(), "logs");

        // 加载设置
        SettingHolder.getSettingHolder().init(PhoneMp3Application.this);

        // 设定程序崩溃时异常搜集
        CrashHandler crashHandler = new CrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        // 初始化数据库框架
        FlowManager.init(this);

        new Logger(PhoneMp3Application.class).i("程序启动：" + Utils.COMMON.getDateStr("yyyy年MM月dd HH:mm:ss"));
    }
}
