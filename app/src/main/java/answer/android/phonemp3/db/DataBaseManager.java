package answer.android.phonemp3.db;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;
import java.util.UUID;

import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.bean.PlayList;
import answer.android.phonemp3.fragment.PlayhistoryFragment;


/**
 * 为了解决前一个数据库管理中出现的问题，
 * Created by Micro on 2017-9-12.
 */

public class DataBaseManager implements DbManager.DbUpgradeListener {

    // 所有歌曲表
    public static final String MUSIC_TABLENAME = "allmusictable";
    // 所有列表表
    // 列表字段: _id, name
    public static final String LISTS_TABLENAME = "playlisttable";

    // 正在播放列表ID
    public static final String CURRENT_PLAYLIST_ID = "currentplayinglist";


    private DbManager.DaoConfig daoConfig = null;
    private DbManager dbManager;


    public DataBaseManager() {
        daoConfig = new DbManager.DaoConfig();
        daoConfig.setDbName("data.db");
        daoConfig.setDbVersion(3);
        daoConfig.setDbUpgradeListener(this);
        dbManager = x.getDb(daoConfig);
    }

    public static DataBaseManager getDataBaseManager(Context context) {
        return new DataBaseManager();
    }

    /**
     * 获取所有的播放列表，注意：[返回的每个playlist对象中没有具体歌曲信息]
     *
     * @return ArrayList
     */
    public List<PlayList> getAllPlayList() {
        try {
            return dbManager.findAll(PlayList.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取某一个播放列表的信息，不包括播放列表歌曲内容
     *
     * @param playListId
     * @return
     */
    public PlayList getPlayEasyList(String playListId) {
        try {
            return dbManager.findById(PlayList.class, playListId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个播放列表信息，包括播放列表的歌曲内容
     *
     * @param playListId
     * @return
     */
    public PlayList getPlayList(String playListId) {
        try {
            PlayList easyPlayList = getPlayEasyList(playListId);

            if (easyPlayList == null) {
                return null;
            }

            List<Music> musics = dbManager.selector(Music.class).where("list_id", "=", playListId).findAll();

            easyPlayList.setMusics(musics);

            return easyPlayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据歌曲Id获取歌曲
     *
     * @param musicId
     * @return
     */
    public Music getMusic(String musicId) {
        try {
            return dbManager.findById(Music.class, musicId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从数据库移出一首歌曲
     *
     * @param music
     * @return
     */
    public boolean removeMusic(Music music) {
        if (null == music) {
            return false;
        }
        try {
            music.set_id(null);
            dbManager.delete(music);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据音乐id从数据库移除一首歌曲
     *
     * @param musicId
     * @return
     */
    public boolean removeMusic(String musicId) {
        try {
            dbManager.deleteById(Music.class, musicId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断某一首歌是否在某个播放列表里面
     *
     * @param music
     * @param playlistId
     * @return
     */
    public boolean isInPlayList(Music music, String playlistId) {

        try {

            List<Music> musics = dbManager.selector(Music.class)
                    .where("list_id", "=", playlistId)
                    .and("_data", "=", music.get_data())
                    .findAll();

            if (musics != null) {
                if (musics.size() > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加一个播放列表
     *
     * @param _id  播放列表id
     * @param name 播放列表名称
     * @return
     */
    public boolean addPlayList(String _id, String name) {
        try {

            PlayList playList = new PlayList();
            playList.setName(name);
            playList.setId(_id);
            dbManager.save(playList);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新播放记录中的某一首歌曲的最近播放时间
     *
     * @param music
     */
    public void updateHistoryMusicDate(Music music) {
        if (null == music) {
            return;
        }

        try {

            // 重新赋值music, 目的是保障修改的music一定只能是history下的而不应该是别的播放列表的
            music = dbManager.selector(Music.class).where("list_id", "=", PlayhistoryFragment.HISTORY$ID)
                    .and("_data", "=", music.get_data())
                    .findFirst();

            music.setUpdateAt(String.valueOf(System.currentTimeMillis()));
            dbManager.saveOrUpdate(music);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除播放时间最久远的一首歌曲
     *
     * @return
     */
    public int deleteTheMinMusic() {
        try {

            // 获取最小的日期
            Cursor cursor = dbManager.execQuery("SELECT min(update_at) from " + DataBaseManager.MUSIC_TABLENAME + " WHERE list_id='" + PlayhistoryFragment.HISTORY$ID + "'");

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    String update_at = cursor.getString(0);
                    cursor.close();
                    return dbManager.executeUpdateDelete("DELETE FROM " + DataBaseManager.MUSIC_TABLENAME + " WHERE update_at='" + update_at + "'");
                }
                cursor.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 统计某个播放列表里面有多少首歌曲
     *
     * @param playlistId
     * @return
     */
    public int countOfPlayList(String playlistId) {
        try {
            Cursor cursor = dbManager.execQuery("SELECT count(*) from " + DataBaseManager.MUSIC_TABLENAME + " WHERE list_id='" + playlistId + "'");
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    int co = cursor.getInt(0);
                    cursor.close();
                    return co;
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 更新一个播放列表的信息
     *
     * @param _id
     * @param name
     * @param remark
     * @return
     */
    public boolean updatePlaylist(String _id, String name, String remark) {
        try {

            PlayList playList = new PlayList();
            playList.setId(_id);
            playList.setName(name);
            playList.setRamark(remark);
            dbManager.saveOrUpdate(playList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清空一个播放列表
     *
     * @param playLisstId
     * @return
     */
    public boolean clearPlayList(String playLisstId) {

        try {

            int i = dbManager.executeUpdateDelete("DELETE FROM " + DataBaseManager.MUSIC_TABLENAME + " WHERE list_id='" + playLisstId + "'");

            return i > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    /**
     * 从播放列表移除某一首歌曲
     *
     * @param playList_id
     * @param m
     * @return
     */
    public boolean removeMusicFromPlayList(String playList_id, Music m) {
        try {
            return dbManager.executeUpdateDelete("DELETE FROM " + DataBaseManager.MUSIC_TABLENAME + " WHERE _id='" + m.get_id() + "' AND list_id='" + playList_id + "'") > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加歌曲到播放列表
     *
     * @param playList_id
     * @param m
     */
    public void addMusicToPlayList(String playList_id, Music m) {

        if (m != null && !TextUtils.isEmpty(playList_id)) {
            try {

                if (isInPlayList(m, playList_id)) {
                    return;
                }

                m.set_id(UUID.randomUUID().toString());
                m.setList_id(playList_id);
                m.setUpdateAt(String.valueOf(System.currentTimeMillis()));
                dbManager.save(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 批量添加歌曲到播放列表
     *
     * @param playList_id
     * @param musics
     */
    public void addMusicToPlayList(String playList_id, List<Music> musics) {
        try {

            for (int index = 0; index < musics.size(); index++) {
                Music m = musics.get(index);
                m.set_id(UUID.randomUUID().toString());
                m.setList_id(playList_id);
                m.setUpdateAt(String.valueOf(System.currentTimeMillis()));
            }
            dbManager.save(musics);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void superClose() {
    }


    @Override
    public void onUpgrade(DbManager dbManager, int i, int i1) {
        try {
            dbManager.addColumn(Music.class, "update_at");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
