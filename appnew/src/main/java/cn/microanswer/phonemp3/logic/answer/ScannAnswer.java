package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.ScannLogic;
import cn.microanswer.phonemp3.ui.ScannPage;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class ScannAnswer extends BaseAnswer<ScannPage> implements ScannLogic {

    private int totalMusicCount = 0;

    private Handler handler;

    public ScannAnswer(ScannPage page) {
        super(page);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

    }

    @Override
    public void onScannClick() {

        // 显示扫描提示。
        getPage().showLoading();

        Task.TaskHelper.getInstance().run(new Task.ITask<Object, Object>() {
            @Override
            public Object run(Object param) throws Exception {
                Utils.APP.scanAllMusic(getPhoneMp3Activity(), new MScannListener());
                return null;
            }
        });


    }

    private void refresh() {


    }

    private class MScannListener implements Utils.APP.OnScannListener, Runnable {
        private final Music START = new Music();
        private final Music END = new Music();

        private Music currentMusic;

        @Override
        public void onStart() {
            totalMusicCount = 0;
            currentMusic = START;
            Task.TaskHelper.getInstance().post(this);
        }

        @Override
        public void onMusic(Music music) {
            currentMusic = music;
            totalMusicCount++;
            Task.TaskHelper.getInstance().post(this);
        }

        @Override
        public void onEnd() {
            currentMusic = END;
            Main_AllMusic_M_Answer.musics = null;
            Task.TaskHelper.getInstance().post(this);
        }

        @Override
        public void run() {

            if (currentMusic != null) {
                if (currentMusic == START) {
                    getPage().setScannHint("开始扫描");
                } else if (currentMusic == END) {
                    if (totalMusicCount == 0) {
                        getPage().showResult("扫描完成，没有发现新歌曲哦！");
                    } else {
                        getPage().showResult(String.format("扫描完成，共扫描到 %s 首新歌曲。", totalMusicCount));
                    }
                } else {
                    getPage().setScannHint(String.format("已扫描到 %s 首歌曲\n%s", totalMusicCount, currentMusic.getTitle()));
                }
            }

        }
    }
}
