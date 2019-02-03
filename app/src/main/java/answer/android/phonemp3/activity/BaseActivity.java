package answer.android.phonemp3.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.bean.PlayList;
import answer.android.phonemp3.db.DataBaseManager;
import answer.android.phonemp3.interfaces.BroadListener;
import answer.android.phonemp3.log.Logger;
import answer.android.phonemp3.tool.CurrentPlayingList3;
import answer.android.phonemp3.tool.Tool;

/**
 * 本应用的每一个Activity都继承自BaseActivity
 * 实现了一些常用的方法
 * Created by Micro on 2017/6/12.
 */

public class BaseActivity extends AppCompatActivity {
    /**
     * 短时间的toast
     */
    public static final int DUR_SHORT = Toast.LENGTH_SHORT;
    /**
     * 长时间的Toast
     */
    public static final int DUR_LONG = Toast.LENGTH_LONG;
    protected String TAG = this.getClass().getSimpleName();
    protected Logger logger;
    private AudioManager mAudioManager;
    private LinearLayout tipview;
    private ViewPropertyAnimatorCompat tipanim;
    private boolean tipisShow; // 标记tip是否正在显示
    // public boolean isServiceRunning; // 标记播放服务是否在运行
    private SystemBarTintManager tintManager;
    private ArrayList<BroadListener> broadListeners;
    private DataBaseManager dataBaseManager;

    public void tip(String msg) {
        tip(msg, null);
    }

    public void tip(String msg, String submsg) {
        if (tipview == null) {
            tipview = (LinearLayout) findViewById(R.id.activity_toolbar_tipcontent);
        }
        tipview.setVisibility(View.VISIBLE);
        TextView t = (TextView) tipview.findViewById(R.id.activity_toolbar_tipmsg);
        TextView ts = (TextView) tipview.findViewById(R.id.activity_toolbar_tipsmallmsg);
        t.setText(msg == null ? "" : msg);
        if (TextUtils.isEmpty(submsg)) {
            ts.setVisibility(View.GONE);
        } else {
            ts.setVisibility(View.VISIBLE);
            ts.setText(submsg);
        }
        ViewCompat.setTranslationX(tipview, DensityUtil.getScreenWidth());
        if (tipanim != null) {
            tipanim.cancel();
        }
        tipanim = ViewCompat.animate(tipview).translationX(0).setDuration(200);
        tipanim.start();
        tipisShow = true;
        x.task().removeCallbacks(tiprunnable);
        x.task().postDelayed(tiprunnable, 3000);
    }

    private Runnable tiprunnable = new Runnable() {
        @Override
        public void run() {
            if (tipisShow) {
                tipisShow = false;
                ViewCompat.setTranslationX(tipview, 0);
                ViewCompat.animate(tipview).translationX(tipview.getWidth()).setDuration(200)
                        .start();
            }
        }
    };

    public void go2Activity(Class<? extends AppCompatActivity> ac) {
        go2Activity(ac, null);
    }

    public void go2Activity(Class<? extends AppCompatActivity> ac, Map<String, String> parmap) {
        Intent intent = new Intent(this, ac);
        if (parmap != null) {
            Set<Map.Entry<String, String>> entries = parmap.entrySet();
            for (Map.Entry<String, String> e : entries) {
                intent.putExtra(e.getKey(), e.getValue() == null ? "" : e.getValue());
            }
        }
        startActivity(intent);
    }

    // 下一曲
    public void broadNext() {
        Intent intent = new Intent(ACTION.ASK.NEXT);
        sendBroadcast(intent);
    }

    // 上一曲
    public void broadLast() {
        Intent intent = new Intent(ACTION.ASK.UP);
        sendBroadcast(intent);
    }

    // 从正在播放列表移除一首歌曲
    public void broadRemoveMusic(Music music) {
        Intent intent = new Intent(ACTION.ASK.CONTROL.REMOVEFROMLISTS);
        intent.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, music);
        intent.putExtra("music", music);
        dataBaseManager.removeMusicFromPlayList(DataBaseManager.CURRENT_PLAYLIST_ID, music);
        dataBaseManager.updatePlaylist(DataBaseManager.CURRENT_PLAYLIST_ID, null, "--");
        sendBroadcast(intent);
    }

    // 播放某首歌曲(通过发送广播)
    public void broadPlay(final Music music) {

        x.task().run(new Runnable() {
            @Override
            public void run() {
                // 获取正在播放列表
                PlayList playEasyList = dataBaseManager.getPlayEasyList(DataBaseManager.CURRENT_PLAYLIST_ID);
                if (null == playEasyList) {
                    // 没有正在播放列表数据条目
                    // 创建
                    dataBaseManager.addPlayList(DataBaseManager.CURRENT_PLAYLIST_ID, getString(R.string.nowplaying));
                }

                dataBaseManager.updatePlaylist(DataBaseManager.CURRENT_PLAYLIST_ID, null, "--");
                dataBaseManager.addMusicToPlayList(DataBaseManager.CURRENT_PLAYLIST_ID, music);

                Intent intent = new Intent();
                intent.setAction(ACTION.ASK.CONTROL.PLAY);
                intent.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, music);
                sendBroadcast(intent);
            }
        });


    }

    // 播放某列表歌曲（通过广播）
    public void broadPlay(final List<Music> musics, final String playListId) {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                // 获取正在播放列表
                PlayList playEasyList = dataBaseManager.getPlayEasyList(DataBaseManager.CURRENT_PLAYLIST_ID);
                if (null == playEasyList) {
                    // 没有正在播放列表数据条目
                    // 创建
                    dataBaseManager.addPlayList(DataBaseManager.CURRENT_PLAYLIST_ID, getString(R.string.nowplaying));
                }

                dataBaseManager.updatePlaylist(DataBaseManager.CURRENT_PLAYLIST_ID, null, playListId);
                dataBaseManager.clearPlayList(DataBaseManager.CURRENT_PLAYLIST_ID); // 清空正在播放列表
                dataBaseManager.addMusicToPlayList(DataBaseManager.CURRENT_PLAYLIST_ID, musics);

                // 测试
                // Log.i("Microanswer", "马上读取存入的:");
                // PlayList playList = dataBaseManager.getPlayList(DataBaseManager.CURRENT_PLAYLIST_ID);
                // for (Music music : playList.getMusics()) {
                //     Log.i("Microanswer", music + "");
                // }

                Intent intent = new Intent();
                intent.setAction(ACTION.ASK.CONTROL.PLAYALL);
                // intent.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSICS, musics);
                // intent.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSICSLISTID, playListId);
                sendBroadcast(intent);
            }
        });
    }

    public DataBaseManager getDataBaseManager() {
        if (dataBaseManager == null) {
            dataBaseManager = DataBaseManager.getDataBaseManager(this);
        }
        return dataBaseManager;
    }


    // 通过广播控制暂停播放
    public void broadPause$Play() {
        Intent intent = new Intent();
        intent.setAction(ACTION.ASK.PLAY$PAUSE);
        sendBroadcast(intent);
    }

    // 通过广播发送一首下一曲应该播放的歌曲
    public void broadNextPlay(Music music) {
        Intent intent = new Intent(ACTION.ASK.NEXT_PLAY);
        intent.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, music);
        intent.putExtra("music", music);
        sendBroadcast(intent);
    }

    // 通过发送广播，清空播放列表
    public void broadClearPlayList() {
        // 获取正在播放列表
        PlayList playEasyList = dataBaseManager.getPlayEasyList(DataBaseManager.CURRENT_PLAYLIST_ID);
        if (null == playEasyList) {
            // 没有正在播放列表数据条目
            // 创建
            dataBaseManager.addPlayList(DataBaseManager.CURRENT_PLAYLIST_ID, getString(R.string.nowplaying));
        }
        dataBaseManager.clearPlayList(DataBaseManager.CURRENT_PLAYLIST_ID); // 清空正在播放列表
        getSharedPreferences().edit().remove(CurrentPlayingList3.PLAY_ID_INGKEY).commit();
        sendBroadcast(new Intent(ACTION.CLEAR_PLAYINGLIST));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseManager = DataBaseManager.getDataBaseManager(this);
        // isServiceRunning = Tool.isServiceRunning(this, "answer.android.phonemp3.service.PhoneMp3PlayService");
        logger = Logger.getLogger(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Android5.0版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏颜色
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                //设置导航栏颜色
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            } else {
                //透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //透明导航栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                //创建状态栏的管理实例
                tintManager = new SystemBarTintManager(this);
                //激活状态栏设置
                tintManager.setStatusBarTintEnabled(true);

                //激活导航栏设置
                tintManager.setNavigationBarTintEnabled(false);
                //设置状态栏颜色
                tintManager.setTintResource(Color.TRANSPARENT);
                //设置导航栏颜色
                tintManager.setNavigationBarTintResource(Color.TRANSPARENT);
            }
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 动态注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION.BEFOR_PLAY);
        intentFilter.addAction(ACTION.AFTER_PLAY);
        intentFilter.addAction(ACTION.PAUSE_PLPAY);
        intentFilter.addAction(ACTION.PLAY_FLISH);
        // intentFilter.addAction(ACTION.ASK.LOVE);
        intentFilter.addAction(ACTION.DELETE);
        intentFilter.addAction(ACTION.LOVE_CHANGE);
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerMorAction(intentFilter);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    protected void onMusicStop(boolean fromuser) {
    }

    protected void beforMusicPlay() {
    }

    protected void onMusicPause() {
    }

    protected void registerMorAction(IntentFilter intentFilter) {
    }

    protected void onGetBroad(Intent intent) {
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION.AFTER_PLAY.equals(action)) {
                // 播放完成一首歌的广播
                String fromuser = intent.getStringExtra("fromuser");
                onMusicStop("true".equals(fromuser));
            } else if (ACTION.BEFOR_PLAY.equals(action)) {
                // 开始播放一首歌前的广播
                beforMusicPlay();
            } else if (ACTION.PAUSE_PLPAY.equals(action)) {
                // 暂停波放的广播
                onMusicPause();
            } else if ("android.media.VOLUME_CHANGED_ACTION".equals(action)) {
                onMusicVolChange(getCurrentMusicVol());
            }
            onGetBroad(intent);
            if (broadListeners != null) {
                for (BroadListener b : broadListeners) {
                    b.onGetBroad(intent);
                }
            }
        }
    };

    protected void onMusicVolChange(int vol) {
    }

    protected void drawStateBarcolor(int color) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(color);
                if (tintManager != null) {
                    tintManager.setTintColor(color);
                }
                return;
            }
            if (tintManager != null) {
                //设置状态栏颜色
                tintManager.setTintColor(color);
                //设置导航栏颜色
                // tintManager.setNavigationBarTintResource(Color.TRANSPARENT);
            }
        }
    }

    protected void drawNavigationBarColor(int color) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setNavigationBarColor(color);
                if (tintManager != null) {
                    tintManager.setNavigationBarTintColor(color);
                }
                return;
            }
            if (tintManager != null) {
                //设置状态栏颜色
                // tintManager.setTintResource(color);
                //设置导航栏颜色
                tintManager.setNavigationBarTintColor(color);
            }
        }
    }

    public Toolbar getToolBar() {
        return (Toolbar) findViewById(R.id.activity_toolbar);
    }

    public File getNewVersionApkDir() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/answer.android.phonemp3/apk/");
    }

    /**
     * 获取音乐最大音量
     *
     * @return
     */
    public int getMusicMaxVol() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    // 获取音乐当前音量
    public int getCurrentMusicVol() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // 设置音量
    public void setMusicVol(int vol) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0); //vol:音量绝对值
    }

    // 设置标题栏阴影
    protected void setActionBarcontentShadow(int dp) {
        View actionbarcontent = findViewById(R.id.action_bar_content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionbarcontent.setElevation(DensityUtil.dip2px(dp));
        }
    }

    /**
     * 将系统状态栏遮挡的部分使用一个同高度的view填充
     */
    @Deprecated
    protected void suitStatusBar() {
        View actionbarcontent = findViewById(R.id.action_bar_content);
        if (null != actionbarcontent) {
            LinearLayout linearLayout = (LinearLayout) actionbarcontent;
            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Tool.getStatusBarHeight(this)));
            linearLayout.addView(v, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                linearLayout.setElevation(DensityUtil.dip2px(8f));
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * 请求全屏
     */
    protected void turnOnFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 关闭全屏
     */
    protected void turnOffFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private AlertDialog alert;

    /**
     * 弹出alert
     *
     * @param msg 内容
     */
    public void alert(String msg) {

        if (Build.VERSION.SDK_INT >= 17) {
            if (isDestroyed()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (isFinish()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (alert == null) {
            alert = new AlertDialog.Builder(this)
                    .setTitle(R.string.alerttitle)
                    .setPositiveButton(R.string.alertok, null).create();
        }
        alert.setMessage(msg);
        alert.show();
    }

    AlertDialog confirm;

    /**
     * 确认框
     *
     * @param msg 内容
     */
    public void confirm(String msg, final Click... clicks) {
        if (confirm == null) {
            confirm = new AlertDialog.Builder(this).setTitle(R.string.confirmtitle).create();
        }
        confirm.setMessage(msg);
        confirm.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirmok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (clicks != null && clicks.length == 1)
                    clicks[0].d0();
            }
        });
        confirm.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.confirmcancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (clicks != null && clicks.length == 2)
                    clicks[1].d0();
            }
        });
        confirm.show();
    }

    /**
     * 弹出吐司
     *
     * @param msg 内容
     * @param dur 时间[DUR_LONG,DUR_SHORT]
     */
    public void toast(String msg, int dur) {
        Toast.makeText(this, msg, dur).show();
    }

    /**
     * 获取默认配置文件
     *
     * @return SharedPreferences 配置文件对象
     */
    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void finish() {
        super.finish();
        isFinish = true;
    }

    private boolean isFinish = false;

    public boolean isFinish() {
        return isFinish;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null)
            alert.dismiss();
        if (confirm != null)
            confirm.dismiss();
        if (logger != null) {
            logger.close();
        }
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            logger.err(e);
        }
    }

    /**
     * 获取自身应用版本号
     *
     * @return version 版本号
     */
    protected String getVersionName() {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            Log.e(BaseActivity.class.getSimpleName(), "获取应用的版本出错.");
            e.printStackTrace();
        }
        return Tool.getSelfVersion(this);
    }

    // 授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //confirm 按钮点击事件
    public interface Click {
        void d0();
    }

    public ArrayList<BroadListener> getBroadListeners() {
        return broadListeners;
    }

    public void setBroadListeners(ArrayList<BroadListener> broadListeners) {
        this.broadListeners = broadListeners;
    }

    public boolean hasBroadListener(BroadListener broadListener) {
        return this.broadListeners != null && broadListeners.contains(broadListener);
    }

    public void addBroadListener(BroadListener broadListener) {
        if (this.broadListeners == null) {
            this.broadListeners = new ArrayList<>();
        }
        broadListeners.add(broadListener);
    }
}
