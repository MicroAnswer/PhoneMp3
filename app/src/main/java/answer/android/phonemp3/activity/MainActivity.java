package answer.android.phonemp3.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import android.support.design.widget.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.support.v4.view.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.PlayServiceBridgeAIDL;
import answer.android.phonemp3.R;
import answer.android.phonemp3.adapter.MainViewPagerAdapter;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.dialog.ToolDialog;
import answer.android.phonemp3.fragment.SettingFragment;
import answer.android.phonemp3.interfaces.ServiceBindListener;
import answer.android.phonemp3.service.PhoneMp3PlayService;
import answer.android.phonemp3.tool.Tool;
import answer.android.phonemp3.view.PlayBarViewHolder;

public class MainActivity extends BaseActivity implements ServiceConnection {

    private ActionBar actionBar;
    private PopupMenu popMenu;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PlayBarViewHolder playBarViewHolder;

    private MainViewPagerAdapter mainViewPagerAdapter;
    private PlayServiceBridgeAIDL playServiceBridgeAIDL; // 与服务绑定的桥梁
    private ArrayList<ServiceBindListener> serviceBindListeners;
    private ToolDialog mToolDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        drawStateBarcolor(getResources().getColor(R.color.colorPrimary));
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        View tmv = toolbar.findViewById(R.id.action_bar_logo$title);

        // 实列化菜单
        popMenu = new PopupMenu(this, tmv);
        popMenu.inflate(R.menu.main_menu);
        try {
            Field mpopup = PopupMenu.class.getDeclaredField("mPopup");
            mpopup.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mpopup.get(popMenu);
            mPopup.setForceShowIcon(true);
        } catch (Exception e) {

        }
        popMenu.setOnMenuItemClickListener(menuItemClickListener);
        tmv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        setActionBarcontentShadow(8);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();


        tabLayout = (TabLayout) findViewById(R.id.activity_main_tablayout);
        viewPager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        // viewPager.setOffscreenPageLimit(3);


        // 关联tabLayout与Viewpager
        tabLayout.setupWithViewPager(viewPager);

        // 设置Viewpager适配器
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), this);
        mainViewPagerAdapter.onMainActivityCreate(this);

        //申请权限
        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (PackageManager.PERMISSION_GRANTED == i) {
            viewPager.setAdapter(mainViewPagerAdapter);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 18);
        }

        View playbar = findViewById(R.id.activity_main_playbar);
        playbar.setOnClickListener(playBarClick);
        // 实列化播放面板
        playBarViewHolder = new PlayBarViewHolder(playbar);
        bindlistener2playview();


        // 将界面与服务绑定
        Intent intent = new Intent(this, PhoneMp3PlayService.class);
        intent.putExtra("client", "answer.android.phonemp3");
        bindService(intent, this, BIND_AUTO_CREATE);

        // 发送app打开日志
        Tool.log2Service(this);

        // 检查更新
        x.task().postDelayed(newVersionChecker, 2000);
    }

    private Runnable newVersionChecker = new Runnable() {
        @Override
        public void run() {

            final String IGNOREVERSIONKEY = "ignoreversion";

            SharedPreferences sharedPreferences = getSharedPreferences();

            long lastnewversioncheckdate = sharedPreferences.getLong(SettingFragment.KEY_NEWVERSIONCHECKDATE, 0L);

            // 上次检查更新时间距离现在有一天了,再次进行检查更新
            if (System.currentTimeMillis() - lastnewversioncheckdate >= 24 * 3600) {
                Tool.checkupdate(MainActivity.this, new Tool.UpdateListener() {
                    @Override
                    public void onNew(final String url,
                                      final String name,
                                      final String version,
                                      final String newFunctions,
                                      final String size,
                                      final String updateat,
                                      final String createdat,
                                      final boolean mustDownload) {

                        String ignoreVeriosn = getSharedPreferences().getString(IGNOREVERSIONKEY, "-");

                        if(version.equals(ignoreVeriosn)) {
                            // 被忽略的版本,不再提示
                            return;
                        }

                        final View view = View.inflate(MainActivity.this, R.layout.dialog_newversion, null);
                        TextView newfunctionTv = (TextView) view.findViewById(R.id.newfunctioncontent);
                        newfunctionTv.setText(String.valueOf(newFunctions));
                        if (mustDownload) {
                            view.findViewById(R.id.newfunctionnothint).setVisibility(View.GONE);
                        }

                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(name)
                                .setView(view)
                                .setPositiveButton("立刻下载", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(MainActivity.this, NewVersionDownloadActivity.class);
                                        intent.putExtra("url", url);
                                        intent.putExtra("name", name);
                                        intent.putExtra("version", version);
                                        intent.putExtra("newfunction", newFunctions);
                                        intent.putExtra("size", size);
                                        intent.putExtra("updateat", updateat);
                                        intent.putExtra("createdat", createdat);
                                        startActivity(intent);
                                    }
                                }).setCancelable(!mustDownload).create();
                        if (!mustDownload) {
                            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean isChecked = ((CheckBox)view.findViewById(R.id.newfunctionnothint)).isChecked();

                                    if(isChecked) {
                                        getSharedPreferences().edit().putString(IGNOREVERSIONKEY, version).apply();
                                    }else {
                                        getSharedPreferences().edit().putString(IGNOREVERSIONKEY, "").apply();
                                    }
                                }
                            });
                        }

                        dialog.show();
                    }

                    @Override
                    public void onThis() {
                        logger.info("已经是最新版本");
                    }
                }, false);
            }

        }
    };

    @Override
    protected void onGetBroad(Intent intent) {
        super.onGetBroad(intent);
        if (ACTION.PLAY_FLISH.equals(intent.getAction())) {
            if (playBarViewHolder != null) {
                playBarViewHolder.getTitle().setText(R.string.nothingPlay);
                playBarViewHolder.getSubtitle().setText("");
                playBarViewHolder.getPlay$pause().setImageResource(R.drawable.ic_play);
                playBarViewHolder.getPlayBarImg().setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

    public String getPlayingListId() {
        if (playServiceBridgeAIDL != null) {
            try {
                return playServiceBridgeAIDL.getPlayListId();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void bindlistener2playview() {
        playBarViewHolder.getPlay$pause().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playServiceBridgeAIDL != null) {
                    try {
                        broadPause$Play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        playBarViewHolder.getNext().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadNext();
            }
        });
    }

    public void addServiceBindListeners(ServiceBindListener serviceBindListener) {
        if (this.serviceBindListeners == null) {
            this.serviceBindListeners = new ArrayList<>();
        }
        this.serviceBindListeners.add(serviceBindListener);
    }

    public void clearServiceBindListeners() {
        if (this.serviceBindListeners != null) {
            this.serviceBindListeners.clear();
        }
    }

    public void removeServiceBindListeners(ServiceBindListener serviceBindListener) {
        if (this.serviceBindListeners != null) {
            this.serviceBindListeners.remove(serviceBindListeners);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 18) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewPager.setAdapter(mainViewPagerAdapter);
            } else {
                AlertDialog.Builder tipdialog = new AlertDialog.Builder(this);
                tipdialog.setTitle(R.string.tip);
                tipdialog.setMessage(R.string.musicpermisiondenied);
                tipdialog.setPositiveButton(R.string.repermission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 18);
                    }
                });
                tipdialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        }
    }

    public PlayBarViewHolder getPlayBarViewHolder() {
        return playBarViewHolder;
    }

    // 显示菜单
    private void showMenu(View v) {
        popMenu.show();
    }

    // 菜单监听器
    PopupMenu.OnMenuItemClickListener menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            onClick(item.getItemId());
            return true;
        }

        private void onClick(int id) {
            switch (id) {
                case R.id.popmenu_main_about:
                    // 点击关于
                    HashMap<String, String> m = new HashMap<>();
                    m.put("url", getString(R.string.abouturl));
                    m.put("title", getString(R.string.about));
                    go2Activity(WebViewActivity.class, m);
                    break;
                case R.id.popmenu_main_exit:
                    // 点击退出
                    mexit();
                    break;
                case R.id.popmenu_main_head:
                    // 点击头像
                    go2Activity(UserActivity.class);
                    break;
                case R.id.popmenu_main_ptalk:
                    // 点击评价
                    Tool.go2AppMarkt(MainActivity.this);
                    break;
                case R.id.popmenu_main_setting:
                    // 点击设置
                    go2Activity(SettingActivity.class);
                    break;
                case R.id.popmenu_main_tool:
                    showTool();
                    // 点击工具
                    break;
                default:
                    break;
            }
        }
    };

    public int getAudioSeesionId() {
        try {
            return playServiceBridgeAIDL.getAudioSessionId();
        } catch (Exception e) {
            logger.err(e);
        }
        return -1;
    }


    private void showTool() {
        if (mToolDialog == null) {
            mToolDialog = new ToolDialog(this, playServiceBridgeAIDL);
        }
        mToolDialog.show();
    }

    View.OnClickListener playBarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (playServiceBridgeAIDL != null && playServiceBridgeAIDL.getCurrentMusic() != null) {
                    go2Activity(PlayActivity.class);
                } else {
                    tip("什么都没播放");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 退出
    private void mexit() {
        // stopService(new Intent(this, PhoneMp3PlayService.class));
        getSharedPreferences().edit().putLong("lastExit", 0).commit(); // 重置退出的时间
        finish();
        if (playServiceBridgeAIDL != null) {
            try {
                playServiceBridgeAIDL.pause();
                Tool.cancelPlayNotify(this);
            } catch (Exception e) {
                logger.err(e);
            }
        }
        // stopService(new Intent(MainActivity.this, PhoneMp3PlayService.class));
    }

    @Override
    public void onBackPressed() {
        getSharedPreferences().edit().putLong("lastExit", System.currentTimeMillis()).commit(); // 保存退出的时间
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popMenu != null) {
            popMenu.dismiss();
        }
        if (mToolDialog != null) {
            mToolDialog.dismiss();
        }
        try {
            unbindService(this);
        } catch (Exception e) {
            logger.err(e);
        }
    }

    @Override
    protected void onMusicPause() {
        super.onMusicPause();
        playBarViewHolder.getPlay$pause().setImageResource(R.drawable.ic_play);
    }

    @Override
    protected void onMusicStop(boolean fromuser) {
        super.onMusicStop(fromuser);
        playBarViewHolder.getPlay$pause().setImageResource(R.drawable.ic_play);
    }

    @Override
    protected void beforMusicPlay() {
        super.beforMusicPlay();
        playBarViewHolder.getPlay$pause().setImageResource(R.drawable.ic_pause);
        if (playServiceBridgeAIDL != null) {
            try {
                Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
                playBarViewHolder.getTitle().setText(currentMusic.getTitle());
                playBarViewHolder.getSubtitle().setText(currentMusic.getArtist());
                Bitmap g = Tool.getAblumBitmap(MainActivity.this, currentMusic.getAlbum_id());
                if (null != g) {
                    playBarViewHolder.getPlayBarImg().setImageBitmap(g);
                } else {
                    playBarViewHolder.getPlayBarImg().setImageResource(R.mipmap.ic_launcher);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public PlayServiceBridgeAIDL getPlayServiceBridgeAIDL() {
        return playServiceBridgeAIDL;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, name.getPackageName() + "绑定成功。");
        playServiceBridgeAIDL = PlayServiceBridgeAIDL.Stub.asInterface(service);
        if (serviceBindListeners != null) {
            for (int a = 0; a < serviceBindListeners.size(); a++) {
                serviceBindListeners.get(a).onConnected(playServiceBridgeAIDL);
            }
        }
        if (playBarViewHolder != null) {
            try {
                Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
                if (currentMusic != null) {
                    playBarViewHolder.getTitle().setText(currentMusic.getTitle());
                    playBarViewHolder.getSubtitle().setText(currentMusic.getArtist());
                    Bitmap ablumBitmap = Tool.getAblumBitmap(MainActivity.this, currentMusic.getAlbum_id());
                    if (null != ablumBitmap)
                        playBarViewHolder.getPlayBarImg().setImageBitmap(ablumBitmap);
                    if (playServiceBridgeAIDL.isPlaying()) {
                        playBarViewHolder.getPlay$pause().setImageResource(R.drawable.ic_pause);
                    } else {
                        playBarViewHolder.getPlay$pause().setImageResource(R.drawable.ic_play);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, name.getPackageName() + ", 断开绑定。");
        if (serviceBindListeners != null) {
            for (int a = 0; a < serviceBindListeners.size(); a++) {
                serviceBindListeners.get(a).onDisConnected();
            }
        }
    }
}
