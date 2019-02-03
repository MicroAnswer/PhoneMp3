package answer.android.phonemp3.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.xutils.x;

import answer.android.phonemp3.service.PhoneMp3PlayService;

/**
 * 音乐播放器
 * Created by Micro on 2017/6/12.
 */

public class MApplication extends Application implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "nswer.android.phonemp33";

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(this);

        x.Ext.init(this);
        // x.Ext.setDebug(false);


        // 开启播放服务
        Intent s = new Intent();
        s.setClass(this, PhoneMp3PlayService.class);
        startService(s);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("MApplication", t.getName() + ",拦截到全局未捕获错误" + e.getMessage());
        e.printStackTrace();
    }
}
