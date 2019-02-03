package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.entity.PlayList_Table;
import cn.microanswer.phonemp3.logic.Main_PlayListLogic;
import cn.microanswer.phonemp3.ui.Main_PlayListPage;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.Task;

public class Main_PlayListAnswer extends BaseAnswer<Main_PlayListPage> implements Main_PlayListLogic {
    private Logger logger = Logger.getLogger(Main_PlayListAnswer.class);
    private final static PlayList HADONE = new PlayList();
    private final static PlayList ERROR = new PlayList();

    public Main_PlayListAnswer(Main_PlayListPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

        loadAllMyPlayList();
    }

    /**
     * 加载所有用户自己创建的播放列表
     */
    private void loadAllMyPlayList() {

        Task.TaskHelper.getInstance().run(new Task.ITask<Void, List<PlayList>>() {
            @Override
            public Void getParam() {
                getPage().showLoading();
                return super.getParam();
            }

            @Override
            public List<PlayList> run(Void param) throws Exception {
                return SQLite.select().from(PlayList.class)
                        .where(PlayList_Table.name.eq(Database.PLAYLIST_NAME_USEROWN))
                        .queryList();
            }

            @Override
            public void afterRun(List<PlayList> value) {
                super.afterRun(value);
                logger.i("用户播放列表加载完成：" + value);
                if (value == null || value.isEmpty()) {
                    getPage().showEmpty();
                } else {
                    getPage().showData(value);
                }
            }
        });

    }

    @Override
    public void onSaveMyPlayList(final String remark, final OnSaveMyPlayListListener listListener) {

        Task.TaskHelper.getInstance().run(new Task.ITask<String, PlayList>() {
            @Override
            public String getParam() {
                return remark;
            }

            @Override
            public PlayList run(String remark) throws Exception {

                // 先查询是否有这个名字的列表了。

                PlayList playList = PlayList.getByNameAndRemark(Database.PLAYLIST_NAME_USEROWN, remark);
                if (playList != null) {
                    // 已存在
                    return HADONE;
                } else {
                    // 不存在
                    playList = new PlayList();
                    playList.setId(String.valueOf(Math.random()));
                    playList.setName(Database.PLAYLIST_NAME_USEROWN);
                    playList.setRamark(remark);
                    boolean save = playList.save();
                    if (save) {
                        return playList;
                    } else {
                        ERROR.setName("创建失败");
                        return ERROR;
                    }
                }
            }

            @Override
            public void afterRun(PlayList value) {
                super.afterRun(value);
                if (listListener != null) {
                    if (value == HADONE) {
                        listListener.onComp(0, null, null);
                    } else if (value == ERROR) {
                        listListener.onComp(2, null, ERROR.getName());
                    } else {
                        listListener.onComp(1, value, null);
                    }
                }
            }
        });

    }
}
