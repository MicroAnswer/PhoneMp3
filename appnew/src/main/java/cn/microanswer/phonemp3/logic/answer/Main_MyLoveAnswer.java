package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;

import java.util.List;

import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.logic.Main_MyLoveLogic;
import cn.microanswer.phonemp3.ui.Main_MyLovePage;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class Main_MyLoveAnswer extends BaseAnswer<Main_MyLovePage> implements Main_MyLoveLogic {

    public Main_MyLoveAnswer(Main_MyLovePage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        getPhoneMp3Activity().addMyMediaController(this);

        loadMyLove();
    }

    @Override
    public void onBrowserConnected() {
        super.onBrowserConnected();
        loadMyLove();
    }

    private void loadMyLove() {
        // 解析并展示数据
        Task.TaskHelper.getInstance().run(new Task.ITask<String, List<Music>>() {
            @Override
            public String getParam() {
                return Database.PLAYLIST_ID_MYLOVE;
            }

            @Override
            public List<Music> run(String param) throws Exception {
                PlayList playList = new PlayList();
                playList.setId(param);
                return playList.getMusics();
            }

            @Override
            public void afterRun(List<Music> value) {
                super.afterRun(value);
                getPage().showData(true, value);
            }
        });

    }

    @Override
    public void onMetadataChanged(final MediaMetadataCompat metadata) {
        super.onMetadataChanged(metadata);

        Task.TaskHelper.getInstance().run(new Task.ITask<MediaMetadataCompat, Object[]>() {
            @Override
            public MediaMetadataCompat getParam() {
                return metadata;
            }

            @Override
            public Object[] run(MediaMetadataCompat param) throws Exception {
                Object[] r = new Object[2];
                Music m = Utils.APP.metaData2Music(param);
                r[0] = m;
                r[1] = Music.isLove(m);
                return r;
            }

            @Override
            public void afterRun(Object[] object) {
                super.afterRun(object);
                Music value = (Music) object[0];
                boolean love = (boolean) object[1];
                if (love) {
                    getPage().addLoved(value);
                } else {
                    getPage().removeLoved(value);
                }
            }
        });

    }

    @Override
    public void onItemClick(View v, Music music, int position) {
        getPhoneMp3Activity().mGetMediaController().getTransportControls().playFromMediaId(String.valueOf(music.getId()), null);
    }
}
