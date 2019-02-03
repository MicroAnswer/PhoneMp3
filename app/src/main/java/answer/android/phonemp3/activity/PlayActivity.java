package answer.android.phonemp3.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.qiujuer.genius.blur.StackBlur;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.PlayServiceBridgeAIDL;
import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.bean.PlayList;
import answer.android.phonemp3.db.DataBaseManager;
import answer.android.phonemp3.dialog.OneMusicInfoDialog;
import answer.android.phonemp3.dialog.PlayingListDialog;
import answer.android.phonemp3.fragment.LovesFragment;
import answer.android.phonemp3.service.PhoneMp3PlayService;
import answer.android.phonemp3.tool.CurrentPlayingList;
import answer.android.phonemp3.tool.CurrentPlayingList3;
import answer.android.phonemp3.tool.Tool;
import answer.android.phonemp3.view.RollIngOverCircleImageView;

/**
 * 播放界面
 * Created by Microanswer on 2017/6/19.
 */

public class PlayActivity extends BaseActivity implements ServiceConnection, View.OnClickListener, Runnable, SeekBar.OnSeekBarChangeListener {

    private int BLUR_RADIUS = 130;
    private View activity_play_bg, activity_play_volset;
    private ActionBar actionBar;
    private PlayServiceBridgeAIDL playServiceBridgeAIDL; // 与服务绑定的桥梁
    private ImageView playway, up, play$pause, next, playlist, love; // 功能按钮
    private SeekBar seekBar, volSeekbar;
    private RollIngOverCircleImageView activity_play_img;
    private TextView startTimeTxt, endTimeTxt;
    private boolean canRefresh = false;
    private List<Music> playIngMusics; // 正在播放的歌曲

    private int[] playways = new int[]{CurrentPlayingList.PLAY_LIST, CurrentPlayingList.PLAY_RANDOM, CurrentPlayingList.PLAY_RELIST, CurrentPlayingList.PLAY_RESINGLE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolBar = getToolBar();
        setSupportActionBar(toolBar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        activity_play_bg = findViewById(R.id.activity_play_bg);
        activity_play_img = (RollIngOverCircleImageView) findViewById(R.id.activity_play_img);
        playway = (ImageView) findViewById(R.id.activity_play_playwayimg);
        playway.setOnClickListener(this);
        up = (ImageView) findViewById(R.id.activity_play_upimg);
        up.setOnClickListener(this);
        play$pause = (ImageView) findViewById(R.id.activity_play_playpauseimg);
        play$pause.setOnClickListener(this);
        next = (ImageView) findViewById(R.id.activity_play_nextimg);
        next.setOnClickListener(this);
        playlist = (ImageView) findViewById(R.id.activity_play_playlistimg);
        playlist.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.activity_play_seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(Integer.MAX_VALUE);
        volSeekbar = (SeekBar) findViewById(R.id.activity_play_volseekbar);
        volSeekbar.setOnSeekBarChangeListener(volseekbarchangerlistener);
        volSeekbar.setMax(getMusicMaxVol());
        volSeekbar.setProgress(getCurrentMusicVol());
        activity_play_volset = findViewById(R.id.activity_play_volset);
        activity_play_volset.setOnClickListener(this);
        startTimeTxt = (TextView) findViewById(R.id.activity_play_starttimetxt);
        endTimeTxt = (TextView) findViewById(R.id.activity_play_endtimetxt);
        love = (ImageView) findViewById(R.id.activity_play_love);
        love.setOnClickListener(this);
        findViewById(R.id.activity_play_share).setOnClickListener(this);
        findViewById(R.id.activity_play_info).setOnClickListener(this);
        findViewById(R.id.activity_play_menu).setOnClickListener(this);

// 将界面与服务绑定
        Intent intent = new Intent(this, PhoneMp3PlayService.class);
        intent.putExtra("client", "answer.android.phonemp3");
        bindService(intent, this, BIND_AUTO_CREATE);

        canRefresh = true;
        // 开始进度条获取
        x.task().postDelayed(this, 300);
    }

    @Override
    protected void registerMorAction(IntentFilter intentFilter) {
        super.registerMorAction(intentFilter);
        intentFilter.addAction(ACTION.PLAYINDEX_CHANGE);
    }

    // 开始图片转圈
    public void startCircleRun() {
        activity_play_img.startRotation();
    }

    // 暂停图片转圈
    public void pauseCircleRun() {
        activity_play_img.pauseRotation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playServiceBridgeAIDL = PlayServiceBridgeAIDL.Stub.asInterface(service);
        if (playServiceBridgeAIDL != null) {
            try {
                PlayList playList = getDataBaseManager().getPlayList(DataBaseManager.CURRENT_PLAYLIST_ID);
                // Log.i("Microanswer","正在播放列表：" + playList.getId() + ", size=" + playList.getMusics().size());
                // Log.i("Microanswer","第一:" + playList.getMusics().get(0).getTitle() + ", 最后:" + playList.getMusics().get(playList.getMusics().size() - 1).getTitle());
                if (playList != null && playList.getMusics() != null) {
                    playIngMusics = playList.getMusics();
                }
                final Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
                // Log.i("Microanswer", "当前:" + currentMusic);
                final boolean isPlaying = playServiceBridgeAIDL.isPlaying();
                final int p = (int) playServiceBridgeAIDL.getCurrentMusicPosition();
                final int playWay = playServiceBridgeAIDL.getPlayWay();

                if (null != currentMusic) {

                    // 获取专辑图片并设置背景
                    x.task().run(new Runnable() {
                        @Override
                        public void run() {
                            final boolean in = getDataBaseManager().isInPlayList(currentMusic, LovesFragment.LOVELISTID);
                            Bitmap ablumBitmap = Tool.getAblumBitmap(PlayActivity.this, currentMusic.getAlbum_id());
                            if (ablumBitmap == null) {
                                ablumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_defaultmusic);
                            }
                            final Bitmap bitmap = StackBlur.blurNatively(ablumBitmap, BLUR_RADIUS, false);
                            final Bitmap finalAblumBitmap = ablumBitmap;
                            x.task().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (in) {
                                        love.setImageResource(R.drawable.ic_love);
                                    } else {
                                        love.setImageResource(R.drawable.ic_unlove);
                                    }
                                    activity_play_bg.setBackgroundDrawable(new BitmapDrawable(bitmap));
                                    activity_play_img.setImageBitmap(finalAblumBitmap);
                                    getSupportActionBar().setTitle(currentMusic.getTitle());
                                    getSupportActionBar().setSubtitle(currentMusic.getArtist());
                                    if (playWay == playways[0]) {
                                        playway.setImageResource(R.drawable.ic_listplay);
                                    } else if (playWay == playways[1]) {
                                        playway.setImageResource(R.drawable.ic_playrandom);
                                    } else if (playWay == playways[2]) {
                                        playway.setImageResource(R.drawable.ic_relistplay);
                                    } else if (playWay == playways[3]) {
                                        playway.setImageResource(R.drawable.ic_singleplay);
                                    }
                                    startTimeTxt.setText(Tool.parseTime(0));
                                    String duration = currentMusic.getDuration();
                                    if (!TextUtils.isEmpty(duration) && !"null".equals(duration)) {
                                        endTimeTxt.setText(Tool.parseTime(Long.parseLong(duration)));
                                    } else {
                                        endTimeTxt.setText(Tool.parseTime(0));
                                    }
                                    if (isPlaying) {
                                        startCircleRun();
                                        play$pause.setImageResource(R.drawable.ic_pause2);
                                        seekBar.setProgress(p);
                                    } else {
                                        play$pause.setImageResource(R.drawable.ic_play2);
                                    }
                                }
                            });


                        }
                    });

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        canRefresh = false;
        try {
            unbindService(this);
            if (playingListDialog != null) {
                playingListDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void beforMusicPlay() {
        super.beforMusicPlay();
        if (playServiceBridgeAIDL != null) {
            try {
                final Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();

                if (null != currentMusic) {
                    if (playingListDialog != null)
                        playingListDialog.point2PlayingPosition(playServiceBridgeAIDL.getCurrentMusicIndex());

                    // 获取专辑图片并设置背景
                    x.task().run(new Runnable() {
                        @Override
                        public void run() {
                            final boolean in = getDataBaseManager().isInPlayList(currentMusic, LovesFragment.LOVELISTID);
                            Bitmap ablumBitmap = Tool.getAblumBitmap(PlayActivity.this, currentMusic.getAlbum_id());
                            if (ablumBitmap == null) {
                                ablumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_defaultmusic);
                            }
                            final Bitmap bitmap = StackBlur.blurNatively(ablumBitmap, BLUR_RADIUS, false);
                            final Bitmap finalAblumBitmap = ablumBitmap;
                            x.task().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (in) {
                                        love.setImageResource(R.drawable.ic_love);
                                    } else {
                                        love.setImageResource(R.drawable.ic_unlove);
                                    }
                                    startTimeTxt.setText(Tool.parseTime(0));
                                    endTimeTxt.setText(Tool.parseTime(Long.parseLong(currentMusic.getDuration())));
                                    activity_play_bg.setBackgroundDrawable(new BitmapDrawable(bitmap));
                                    activity_play_img.setImageBitmap(finalAblumBitmap);
                                    startCircleRun();
                                    getSupportActionBar().setTitle(currentMusic.getTitle());
                                    getSupportActionBar().setSubtitle(currentMusic.getArtist());
                                    play$pause.setImageResource(R.drawable.ic_pause2);
                                }
                            });


                        }
                    });

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onMusicStop(boolean fromuser) {
        super.onMusicStop(fromuser);
        pauseCircleRun();
        play$pause.setImageResource(R.drawable.ic_play2);

    }

    @Override
    protected void onMusicPause() {
        super.onMusicPause();
        pauseCircleRun();
        play$pause.setImageResource(R.drawable.ic_play2);
    }


    PlayingListDialog playingListDialog;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.activity_play_playlistimg) {
            // 点击播放列表按钮
            if (playIngMusics == null) {
                playIngMusics = new ArrayList<>();
            }
            if (playingListDialog == null) {
                try {
                    playingListDialog = new PlayingListDialog(this, playIngMusics, playServiceBridgeAIDL.getCurrentMusicIndex());
                    playingListDialog.setOnItemClickListener(new PlayingListDialog.OnItemClickListener() {
                        @Override
                        public void onDelete(int position) {
                            // Log.e("aaaa", position + "");
                            int size = playIngMusics.size();
                            Music music = playIngMusics.remove(position);
                            broadRemoveMusic(music);
                            playingListDialog.notifyItemRemoved(position, size);
                        }

                        @Override
                        public void onclear() {
                            Tool.alert(PlayActivity.this, "确定要清空播放列表？", new Tool.OnClick() {
                                @Override
                                public String getBtnTxt() {
                                    return getResources().getString(R.string.alertok);
                                }

                                @Override
                                public void d0() {
                                    broadClearPlayList();
                                }
                            }, new Tool.OnClick() {
                                @Override
                                public String getBtnTxt() {
                                    return getResources().getString(R.string.cancel);
                                }

                                @Override
                                public void d0() {

                                }
                            });
                        }

                        @Override
                        public void onClick(int position) {
                            try {
                                playServiceBridgeAIDL.playIndex(position);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (playServiceBridgeAIDL != null) {
                try {
                    playingListDialog.show((int) playServiceBridgeAIDL.getCurrentMusicIndex());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.activity_play_nextimg) {
            if (playServiceBridgeAIDL != null) {
                try {
                    seekBar.setProgress(0);
                    broadNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.activity_play_playpauseimg) {
            if (playServiceBridgeAIDL != null) {
                try {
                    broadPause$Play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.activity_play_playwayimg) {
            // 播放顺序切换
            if (playServiceBridgeAIDL != null) {
                try {
                    int playWay = playServiceBridgeAIDL.getPlayWay();
                    playWay++;
                    if (playWay - 1 == playways.length) {
                        playWay = 1;
                    }
                    // 保存播放顺序信息
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(CurrentPlayingList3.PREFERENCE_KEY_PLAYWAY, playWay).commit();
                    playServiceBridgeAIDL.setPlayWay(playWay);
                    if (playWay == playways[0]) {
                        tip(getString(R.string.listplay));
                        this.playway.setImageResource(R.drawable.ic_listplay);
                    } else if (playWay == playways[1]) {
                        tip(getString(R.string.randomplay));
                        this.playway.setImageResource(R.drawable.ic_playrandom);
                    } else if (playWay == playways[2]) {
                        tip(getString(R.string.relistplay));
                        this.playway.setImageResource(R.drawable.ic_relistplay);
                    } else if (playWay == playways[3]) {
                        tip(getString(R.string.singleplay));
                        this.playway.setImageResource(R.drawable.ic_singleplay);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.activity_play_upimg) {
            // 上一曲
            if (playServiceBridgeAIDL != null) {
                try {
                    broadLast();
                    seekBar.setProgress(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.activity_play_volset) {
            // 点击音量调节按钮
            if (isShowvolseekbar) {
                ViewCompat.setAlpha(volSeekbar, 1f);
                ViewCompat.animate(volSeekbar).alpha(0f).setDuration(250).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        volSeekbar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
                isShowvolseekbar = false;
            } else {
                ViewCompat.setAlpha(volSeekbar, 0f);
                ViewCompat.animate(volSeekbar).alpha(1f).setDuration(250).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        volSeekbar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
                isShowvolseekbar = true;
            }
        } else if (id == R.id.activity_play_menu) {
            // 菜单点击
            openMenu();
        } else if (id == R.id.activity_play_info) {
            // 信息点击
            showMusicInfo();
        } else if (id == R.id.activity_play_share) {
            // 分享点击
            share();
        } else if (id == R.id.activity_play_love) {
            // 收藏点击
            love();
        }
    }

    private void showMusicInfo() {
        try {
            new OneMusicInfoDialog(this, playServiceBridgeAIDL.getCurrentMusic()).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // 打开菜单
    private void openMenu() {
        AlertDialog.Builder menubuilde = new AlertDialog.Builder(this);
        menubuilde.setItems(R.array.playmenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        love();
                        break;
                    case 1:
                        share();
                        break;
                    case 2:
                        showMusicInfo();
                        break;
                    case 3:
                        deleteMusic();
                        break;
                }
            }
        }).setCancelable(true).show();
    }

    private void love() {
        if (playServiceBridgeAIDL != null) {
            try {
                final Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
                if (null != currentMusic) {
                    x.task().run(new Runnable() {
                        @Override
                        public void run() {
                            boolean in = getDataBaseManager().isInPlayList(currentMusic, LovesFragment.LOVELISTID);
                            if (in) {
                                // 取消收藏
                                if (getDataBaseManager().removeMusicFromPlayList(LovesFragment.LOVELISTID, currentMusic))
                                    x.task().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(ACTION.LOVE_CHANGE);
                                            intent.putExtra("love", false);
                                            intent.putExtra("music", currentMusic);
                                            sendBroadcast(intent);
                                            Tool.showSystemStatusBarPlayNotify(PlayActivity.this, "开始播放", Tool.getAblumBitmap(PlayActivity.this, currentMusic.getAlbum_id()), currentMusic.getTitle(), currentMusic.getArtist(), true, getDataBaseManager().isInPlayList(currentMusic, LovesFragment.LOVELISTID));
                                            love.setImageResource(R.drawable.ic_unlove);
                                        }
                                    });
                                else {
                                    logger.info("取消收藏失败");
                                }
                            } else {
                                // 收藏
                                getDataBaseManager().addMusicToPlayList(LovesFragment.LOVELISTID, currentMusic);
                                x.task().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ACTION.LOVE_CHANGE);
                                        intent.putExtra("love", true);
                                        intent.putExtra("music", currentMusic);
                                        sendBroadcast(intent);
                                        tip("收藏成功");
                                        Tool.showSystemStatusBarPlayNotify(PlayActivity.this, "开始播放", Tool.getAblumBitmap(PlayActivity.this, currentMusic.getAlbum_id()), currentMusic.getTitle(), currentMusic.getArtist(), true, getDataBaseManager().isInPlayList(currentMusic, LovesFragment.LOVELISTID));
                                        love.setImageResource(R.drawable.ic_love);
                                    }
                                });
                            }

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onGetBroad(Intent intent) {
        super.onGetBroad(intent);
        if (ACTION.LOVE_CHANGE.equals(intent.getAction())) {
            boolean love = intent.getBooleanExtra("love", false);
            if (love) {
                this.love.setImageResource(R.drawable.ic_love);
            } else {
                this.love.setImageResource(R.drawable.ic_unlove);
            }
        } else if (ACTION.PLAYINDEX_CHANGE.equals(intent.getAction())) {
            int index = intent.getIntExtra(ACTION.EXTRA_INDEx, 0);
            if (playingListDialog != null) {
                playingListDialog.point2PlayingPosition(index);
            }
        } else if (ACTION.PLAY_FLISH.equals(intent.getAction())) {
            finish();
        }
    }

    // 风向当前播放的歌曲
    private void share() {
        try {
            Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
            if (currentMusic != null) {
                Tool.share(PlayActivity.this, new File(currentMusic.get_data()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除当前播放的歌曲
    private void deleteMusic() {
        try {
            if (playServiceBridgeAIDL != null) {
                final Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
                if (currentMusic != null) {
                    confirm(getString(R.string.deletetip), new Click() {
                        @Override
                        public void d0() {
                            // 确定删除
                            File f = new File(currentMusic.get_data());
                            if (f.exists()) {
                                if (f.delete()) {
                                    x.task().run(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent innn = new Intent(ACTION.DELETE);
                                            innn.putExtra("music", currentMusic);
                                            innn.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, currentMusic);
                                            sendBroadcast(innn);

                                            // 从数据库全部移除这首歌
                                            getDataBaseManager().removeMusic(currentMusic);

                                            // 当前播放列表中移除这首歌
                                            final int i = playIngMusics.indexOf(currentMusic);
                                            playIngMusics.remove(currentMusic);

                                            // 发送取消收藏广播
                                            Intent innnn2 = new Intent(ACTION.LOVE_CHANGE);
                                            innnn2.putExtra("love", false);
                                            innnn2.putExtra("music", currentMusic);
                                            innnn2.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, currentMusic);

                                            sendBroadcast(innnn2);

                                            try {
                                                playServiceBridgeAIDL.playNext();
                                                playServiceBridgeAIDL.removeFromPlayList(currentMusic);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                            x.task().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // 更新界面
                                                    if (playingListDialog != null) {
                                                        playingListDialog.notifyItemRemoved(i, playIngMusics.size());
                                                    }

                                                    toast("删除成功", DUR_SHORT);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            alert(Tool.Exception2String(e));
        }
    }

    private boolean isShowvolseekbar = false; // 默认音量调节是隐藏的


    @Override
    public void run() {
        // 每一秒都执行,获取歌曲进度条
        if (playServiceBridgeAIDL != null && !isSeekBarTouching) {
            try {
                if (playServiceBridgeAIDL.isPlaying()) {
                    Music currentMusic = playServiceBridgeAIDL.getCurrentMusic();
                    if (currentMusic != null) {
                        int i = 0;
                        try {
                            i = Integer.parseInt(currentMusic.getDuration());
                        } catch (Exception e) {

                        }
                        seekBar.setMax(i);
                        long currentMusicPosition = playServiceBridgeAIDL.getCurrentMusicPosition();
                        seekBar.setProgress((int) currentMusicPosition);
                        startTimeTxt.setText(Tool.parseTime(currentMusicPosition));
                        endTimeTxt.setText(Tool.parseTime(i));
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (canRefresh) {
            x.task().postDelayed(this, 1000);
        }

    }

    private boolean isSeekBarTouching = false;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekBarTouching = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekBarTouching = false;
        if (playServiceBridgeAIDL != null) {
            try {
                playServiceBridgeAIDL.seekTo(seekBar.getProgress());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onMusicVolChange(int vol) {
        volSeekbar.setProgress(vol);
    }

    private boolean isVolTouchIng = false; // 标记手指是否在音量控制条上
    private SeekBar.OnSeekBarChangeListener volseekbarchangerlistener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                setMusicVol(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isVolTouchIng = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isVolTouchIng = false;
            // 8秒后自动关闭
            x.task().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isVolTouchIng) {
                        ViewCompat.setAlpha(volSeekbar, 1f);
                        ViewCompat.animate(volSeekbar).alpha(0f).setDuration(250).setListener(new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(View view) {

                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                volSeekbar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(View view) {

                            }
                        }).start();
                        isShowvolseekbar = false;
                    }
                }
            }, 8 * 1000);
        }
    };
}
