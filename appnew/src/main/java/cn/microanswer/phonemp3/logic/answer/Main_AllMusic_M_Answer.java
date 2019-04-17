package cn.microanswer.phonemp3.logic.answer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.List;

import androidx.core.content.ContextCompat;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.ACTION;
import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.Music_Table;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.entity.PlayList_Table;
import cn.microanswer.phonemp3.logic.Main_AllMusic_M_Logic;
import cn.microanswer.phonemp3.ui.Main_AllMucis_M_Page;
import cn.microanswer.phonemp3.ui.dialogs.AddMusicToPlayListDialog;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class Main_AllMusic_M_Answer extends BaseAnswer<Main_AllMucis_M_Page> implements Main_AllMusic_M_Logic {
    // private static Logger logger = new Logger(Main_AllMusic_M_Answer.class);

    private String currentScan = ""; // 标记当前扫描到的
    static List<Music> musics;

    public Main_AllMusic_M_Answer(Main_AllMucis_M_Page page) {
        super(page);
        musics = null;
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        getPhoneMp3Activity().addMyMediaController(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (musics == null) {
            loadAllMusics();
        } else {
            getPage().showData(true, musics);
        }
    }

    @Override
    public void onBrowserConnected() {
        super.onBrowserConnected();
        loadAllMusics();
    }

    /**
     * 加载所有的歌曲。<p>
     * 从本软件的数据库加载。
     * </p>
     */
    private void loadAllMusics() {
        // 先检查是否有权限
        int i = ContextCompat.checkSelfPermission(getPhoneMp3Activity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (i == PackageManager.PERMISSION_DENIED) {
            // 没有权限
            // 请求权限
            String[] per;
            if (Build.VERSION.SDK_INT <= 15) {
                per = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            } else {
                per = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
            }
            getPage().requestPermission(per);

            return;
        }

        // 加载数据库中的所有歌曲。没加载到任何歌曲，则显示扫描按钮。
        Task.TaskHelper.getInstance().run(new Task.ITask<Void, List<Music>>() {

            @Override
            public Void getParam() {

                getPage().showLoading(false);
                getPage().setScanHint("");

                return super.getParam();
            }

            @Override
            public List<Music> run(Void param) {
                PlayList allMusicPlayList = SQLite.select().from(PlayList.class)
                        .where(PlayList_Table._id.eq(Database.PLAYLIST_ID_ALL))
                        .querySingle();

                if (allMusicPlayList == null) {
                    // 在数据库中还没有这个播放列表， 立刻创建
                    allMusicPlayList = new PlayList();
                    allMusicPlayList.setId(Database.PLAYLIST_ID_ALL);
                    allMusicPlayList.setName("所有歌曲");
                    allMusicPlayList.setRamark("该列表保存了手机中所有的歌曲");
                    allMusicPlayList.insert();
                }

                return allMusicPlayList.getMusics();
            }

            @Override
            public void afterRun(List<Music> value) {
                super.afterRun(value);

                if (value == null || value.size() <= 0) {
                    getPage().showEmpty(true);
                } else {
                    musics = value;
                    getPage().showData(true, value);
                }

            }
        });
    }

    // private ResultReceiver scannerResultReceiver = null;
    private Handler scannerHandler = null;

    @Override
    public void onBtnScannClick() {
        if (scannerHandler == null) {
            scannerHandler = new Handler();
        }

        // 扫描开始
        // 显示扫描提示
        getPage().showLoading(false);
        Task.TaskHelper.getInstance().run(new Task.ITask<Context, List<Music>>() {
            @Override
            public Context getParam() {
                return getPhoneMp3Activity();
            }

            @Override
            public List<Music> run(Context param) {

                // 执行扫描
                Utils.APP.scanAllMusic(param, new Utils.APP.OnScannListener() {
                    public void onStart() {
                    }

                    public void onMusic(Music music) {
                        currentScan = music.get_data();
                        scannerHandler.post(() -> {
                            String txt = String.format("已扫描到：%s", currentScan);
                            getPage().setScanHint(txt);
                        });
                    }

                    public void onEnd() {
                    }
                });

                // 进行查询结果。
                PlayList allMusicPlayList = SQLite.select().from(PlayList.class)
                        .where(PlayList_Table._id.eq(Database.PLAYLIST_ID_ALL))
                        .querySingle();

                if (allMusicPlayList == null) {
                    // 在数据库中还没有这个播放列表， 立刻创建
                    allMusicPlayList = new PlayList();
                    allMusicPlayList.setId(Database.PLAYLIST_ID_ALL);
                    allMusicPlayList.setName("所有歌曲");
                    allMusicPlayList.setRamark("该列表保存了手机中所有的歌曲");
                    allMusicPlayList.insert();
                }

                return allMusicPlayList.getMusics();
            }

            @Override
            public void afterRun(List<Music> value) {
                super.afterRun(value);


                if (value == null || value.size() <= 0) {
                    getPage().showEmpty(true);
                } else {
                    getPage().showData(true, value);
                }
            }
        });
    }

    @Override
    public void onPermissionsResult(String[] permissions, int[] grantResults) {
        boolean result = true;
        if (grantResults != null) {
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    result = false;
                    break;
                }
            }
        }

        if (!result) { // 授权没通过
            Utils.UI.confirm(getPhoneMp3Activity(),
                    "程序没有权限获取您手机里的歌曲。",
                    "重新获取",
                    (dialog, which) -> loadAllMusics());
        } else {
            // 授权通过的
            loadAllMusics();
        }

    }

    @Override
    public void onMusicItemClick(View v, final Music music, int position) {
        getPhoneMp3Activity().mGetMediaController().getTransportControls().playFromMediaId(String.valueOf(music.getId()), null);
    }

    @Override
    public void doItemMenuClick(Music music, int position, int itemMenuId) {
        switch (itemMenuId) {
            case R.id.nextPlay:
                _nextPlay(music, position);
                break;
            case R.id.addToPlayList:
                _add2List(music, position);
                break;
            case R.id.share:
                _share(music, position);
                break;
            case R.id.detail:
                _musicDetail(music, position);
                break;
            case R.id.delete:
                _delete(music, position);
                break;
            default:
                Toast.makeText(getPhoneMp3Activity(), "未知命令", Toast.LENGTH_SHORT).show();
        }
    }

    // 下一曲播放
    private void _nextPlay(Music music, int position) {
        MediaControllerCompat.TransportControls transportControls = getPhoneMp3Activity().mGetMediaController().getTransportControls();
        transportControls.sendCustomAction(ACTION.SET_NEXT_MUSIC, Utils.APP.music2Bundle(music));
        Toast.makeText(getPhoneMp3Activity(), "下一曲将播放：" + music.getTitle(), Toast.LENGTH_SHORT).show();
    }

    // 添加到歌单
    private void _add2List(Music music, int position) {
        new AddMusicToPlayListDialog(getPhoneMp3Activity(), music).show();
    }

    // 分享
    private void _share(Music music, int position) {

    }

    // 查看歌曲信息
    private void _musicDetail(Music music, int position) {

    }

    // 删除
    private void _delete(Music music, int position) {
        Utils.UI.confirm(getPhoneMp3Activity(), getPhoneMp3Activity().getString(R.string.deleteMusicTip, music.getTitle()), null, (dialog, which) -> {
            Task.TaskHelper.getInstance().run(new Task.ITask<Music, Boolean>() {
                @Override
                public Music getParam() {
                    return music;
                }

                @Override
                public Boolean run(Music param) throws Exception {

                    String data = param.get_data();
                    long l = SQLite.delete(Music.class)
                            .where(Music_Table._data.eq(data))
                            .executeUpdateDelete();
                    boolean databaseDeleteSuccess = 1L <= l;

                    if (databaseDeleteSuccess) {
                        File file = new File(data);
                        return file.delete();
                    }
                    return false;
                }

                @Override
                public void afterRun(Boolean value) {
                    super.afterRun(value);
                    if (value != null && value) { //TODO 删除歌曲后需、、后续处理， 如果这首歌曲正在播放，应该播放下一曲。
                        Utils.APP.sendBroadcast(getPhoneMp3Activity(), ACTION.MUSIC_DELETED, music.get_data());
                        MediaControllerCompat.TransportControls transportControls = getPhoneMp3Activity().mGetMediaController().getTransportControls();
                        if (null != transportControls) {
                            transportControls.sendCustomAction(ACTION.MUSIC_DELETED, Utils.APP.music2Bundle(music));
                        }
                        Toast.makeText(getPhoneMp3Activity(), getPhoneMp3Activity().getString(R.string.deleteSucess), Toast.LENGTH_SHORT).show();
                        getPage().dataDeleted(position);
                        Task.TaskHelper.getInstance().run(() -> Utils.APP.updateFileFromDatabase(getPhoneMp3Activity(), music.get_data()));
                    } else {
                        Toast.makeText(getPhoneMp3Activity(), getPhoneMp3Activity().getString(R.string.deleteFail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    super.onError(e);
                }
            });
        }).show();
    }
}
