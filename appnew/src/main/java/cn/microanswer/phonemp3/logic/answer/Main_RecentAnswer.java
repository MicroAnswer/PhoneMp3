package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;

import java.util.List;

import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.logic.Main_RecentLogic;
import cn.microanswer.phonemp3.ui.Main_RecentPage;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class Main_RecentAnswer extends BaseAnswer<Main_RecentPage> implements Main_RecentLogic {

    private Logger logger = Logger.getLogger(getClass());

    public Main_RecentAnswer(Main_RecentPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        getPhoneMp3Activity().addMyMediaController(this);

        loadRecentMusic();
    }

    @Override
    public void onBrowserConnected() {
        super.onBrowserConnected();
        loadRecentMusic();
    }

    @Override
    public void onMetadataChanged(final MediaMetadataCompat metadata) {
        super.onMetadataChanged(metadata);
        Task.TaskHelper.getInstance().run(new Task.ITask<MediaMetadataCompat, Music>() {
            @Override
            public MediaMetadataCompat getParam() {
                return metadata;
            }

            @Override
            public Music run(MediaMetadataCompat param) throws Exception {

                return Utils.APP.metaData2Music(param);
            }

            @Override
            public void afterRun(Music object) {
                super.afterRun(object);
                if (object != null) {
                    getPage().newMusicPlayed(object);
                }

            }
        });
    }

    private void loadRecentMusic() {
        // logger.i("开始加载最近播放");
        // 向服务索取音乐数据

        Task.TaskHelper.getInstance().run(new Task.ITask<Void, List<Music>>() {

            @Override
            public List<Music> run(Void param) throws Exception {

                PlayList p = new PlayList();
                p.setId(Database.PLAYLIST_ID_HISTORY);
                return p.getMusics(false);
            }

            @Override
            public void afterRun(List<Music> value) {
                super.afterRun(value);
                getPage().showData(true, value);
            }
        });
    }

    @Override
    public void onMusicItemClick(View v, Music music, int position) {
        getPhoneMp3Activity().mGetMediaController().getTransportControls().playFromMediaId(String.valueOf(music.getId()), null);
    }
}
