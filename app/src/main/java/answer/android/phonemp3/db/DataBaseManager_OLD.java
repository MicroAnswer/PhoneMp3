package answer.android.phonemp3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.bean.PlayList;
import answer.android.phonemp3.fragment.LovesFragment;
import answer.android.phonemp3.fragment.PlayhistoryFragment;
import answer.android.phonemp3.log.Logger;

/**
 * 数据库管理类
 * Created by Micro on 2017/6/29.
 */

@Deprecated
public class DataBaseManager_OLD {
  private static final Object lock = new Object();
  // 所有歌曲表
  public static final String MUSIC_TABLENAME = "allmusictable";
  // 所有列表表
  // 列表字段: _id, name
  public static final String LISTS_TABLENAME = "playlisttable";

  // 正在播放列表ID
  public static final String CURRENT_PLAYLIST_ID = "currentplayinglist";

  private final String TAG = "DataBaseManager";


  private DataBaseOpenHelper dataBaseOpenHelper;
  private SQLiteDatabase readDB, writeDB;

  private DataBaseManager_OLD(Context context) {
    dataBaseOpenHelper = new DataBaseOpenHelper(context, context.getResources().getInteger(R.integer.databaseversion));
  }

  private static DataBaseManager_OLD dataBaseManager;

  public static DataBaseManager_OLD getDataBaseManager(Context context) {
    if (dataBaseManager == null) {
      dataBaseManager = new DataBaseManager_OLD(context.getApplicationContext());
    }
    return dataBaseManager;
  }


  // 获取所有的播放列表
  public ArrayList<PlayList> getAllPlayList() {
    checkAndInitReadDB();
    Cursor query = readDB.query(LISTS_TABLENAME, null, null, null, null, null, null);
    ArrayList<PlayList> playLists = null;
    if (query.getCount() > 0 && query.moveToFirst()) {
      playLists = new ArrayList<>();
      do {
        PlayList playList = new PlayList();
        playList.setName(query.getString(query.getColumnIndex("name")));
        playList.setId(query.getString(query.getColumnIndex("_id")));
        playList.setRamark(query.getString(query.getColumnIndex("remark")));
        playLists.add(playList);
      } while (query.moveToNext());
      query.close();
    }
    close();
    return playLists;
  }


  // 只获取播放列表信息，不获取里面的歌曲信息
  public PlayList getPlayEasyList(String id) {
    checkAndInitReadDB();
    PlayList playList = null;
    Cursor query = readDB.query(LISTS_TABLENAME, null, "_id=?", new String[]{id}, null, null, null);
    if (query != null && query.getCount() > 0 && query.moveToFirst()) {
      playList = new PlayList();
      playList.setId(query.getString(query.getColumnIndex("_id")));
      playList.setName(query.getString(query.getColumnIndex("name")));
      playList.setRamark(query.getString(query.getColumnIndex("remark")));
      query.close();
    }
    close();
    return playList;
  }

  // 根据playlistid获取播放列表
  public PlayList getPlayList(String id) {
    try {
      checkAndInitReadDB();
      // 2张表一起查询
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("SELECT ");
      stringBuilder.append(LISTS_TABLENAME + "._id AS plist_id,");
      stringBuilder.append(LISTS_TABLENAME + ".name AS list_name,");
      stringBuilder.append(LISTS_TABLENAME + ".remark AS list_remark,");
      stringBuilder.append(MUSIC_TABLENAME + ".letter,");
      stringBuilder.append(MUSIC_TABLENAME + "._id,");
      stringBuilder.append(MUSIC_TABLENAME + "._data,");
      stringBuilder.append(MUSIC_TABLENAME + "._display_name,");
      stringBuilder.append(MUSIC_TABLENAME + "._size,");
      stringBuilder.append(MUSIC_TABLENAME + ".mime_type,");
      stringBuilder.append(MUSIC_TABLENAME + ".date_added,");
      stringBuilder.append(MUSIC_TABLENAME + ".is_drm,");
      stringBuilder.append(MUSIC_TABLENAME + ".date_modified,");
      stringBuilder.append(MUSIC_TABLENAME + ".title,");
      stringBuilder.append(MUSIC_TABLENAME + ".title_key,");
      stringBuilder.append(MUSIC_TABLENAME + ".duration,");
      stringBuilder.append(MUSIC_TABLENAME + ".composer,");
      stringBuilder.append(MUSIC_TABLENAME + ".track,");
      stringBuilder.append(MUSIC_TABLENAME + ".year,");
      stringBuilder.append(MUSIC_TABLENAME + ".is_ringtone,");
      stringBuilder.append(MUSIC_TABLENAME + ".is_alarm,");
      stringBuilder.append(MUSIC_TABLENAME + ".is_notification,");
      stringBuilder.append(MUSIC_TABLENAME + ".is_podcast,");
      stringBuilder.append(MUSIC_TABLENAME + ".bookmark,");
      stringBuilder.append(MUSIC_TABLENAME + ".album_artist,");
      stringBuilder.append(MUSIC_TABLENAME + ".artist_id,");
      stringBuilder.append(MUSIC_TABLENAME + ".artist_key,");
      stringBuilder.append(MUSIC_TABLENAME + ".artist,");
      stringBuilder.append(MUSIC_TABLENAME + ".album_id,");
      stringBuilder.append(MUSIC_TABLENAME + ".album_key,");
      stringBuilder.append(MUSIC_TABLENAME + ".album,");
      stringBuilder.append(MUSIC_TABLENAME + ".is_music,");
      stringBuilder.append(MUSIC_TABLENAME + ".list_id, ");
      stringBuilder.append(MUSIC_TABLENAME + ".update_at ");
      stringBuilder.append("FROM " + LISTS_TABLENAME + ", " + MUSIC_TABLENAME + " ");
      stringBuilder.append("WHERE " + LISTS_TABLENAME + "._id = " + MUSIC_TABLENAME + ".list_id AND " + MUSIC_TABLENAME + ".list_id = ?;");
      String sql = stringBuilder.toString();
      Cursor cursor = readDB.rawQuery(sql, new String[]{id});
      PlayList playList = null;
      if (cursor.getCount() > 0 && cursor.moveToFirst()) {
        playList = new PlayList();
        playList.setMusics(new ArrayList<Music>());
        do {
          playList.setId(cursor.getString(cursor.getColumnIndex("plist_id")));
          playList.setName(cursor.getString(cursor.getColumnIndex("list_name")));
          playList.setRamark(cursor.getString(cursor.getColumnIndex("list_remark")));
          Music m = getMusicFromCursor(cursor);
          if(LovesFragment.LOVELISTID.equals(id)) {
            m.isLoved = true;
          }
          playList.getMusics().add(m);
        } while (cursor.moveToNext());
        // Log.i(TAG, playList.getMusics().size() + "");
        cursor.close();
      }
      close();
      return playList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // 根据musicid获取音乐
  public Music getMusic(String id) {
    checkAndInitReadDB();
    Cursor query = readDB.query(MUSIC_TABLENAME, null, "_id=?", new String[]{id}, null, null, null);
    Music music = null;
    if (query.moveToFirst()) {
      music = getMusicFromCursor(query);
    }
    query.close();
    close();
    return music;
  }

  // 更具歌曲id移除一首歌曲,任何播放列表中的都会被移除
  public boolean removeMusic(String id) {
    checkAndInitWriteDB();
    boolean a = 0 < writeDB.delete(MUSIC_TABLENAME, "_id=?", new String[]{id});
    close();
    return a;
  }

  // 判断某一首歌曲是否在某个播放列表里面
  public boolean isInPlayList(Music music, String playlistId) {
    try {
      checkAndInitReadDB();
      Cursor cursor = readDB.rawQuery("SELECT * from " + MUSIC_TABLENAME + " where list_id = ? and _id = ?", new String[]{playlistId, music.get_id()});
      if (cursor != null && cursor.getCount() > 0) {
        cursor.close();
        close();
        return true;
      }
      close();
      return false;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // 添加一个播放列表
  public boolean addPlayList(String _id, String name) {
    checkAndInitWriteDB();
    ContentValues contentValues = new ContentValues();
    contentValues.put("_id", _id);
    contentValues.put("name", name);
    long name1 = writeDB.insert(LISTS_TABLENAME, "name", contentValues);
    close();
    return name1 != -1L;
  }

  // 更新某一首歌曲的播放时间为当前时间
  public void updateHistoryMusicDate(Music music) {
    checkAndInitWriteDB();

    ContentValues contentValues = new ContentValues();
    contentValues.put("update_at", String.valueOf(System.currentTimeMillis()));
    writeDB.update(MUSIC_TABLENAME, contentValues, "_id=? and list_id=?", new String[]{music.get_id(), PlayhistoryFragment.HISTORY$ID});

    close();
  }

  // 删除播放时间最久远的一首歌曲
  public int deleteTheMinMusic() {
    checkAndInitWriteDB();
    int i=-1;
    // 获取最小的日期
    Cursor query = writeDB.query(MUSIC_TABLENAME, new String[]{"min(update_at)"}, "list_id=?", new String[]{PlayhistoryFragment.HISTORY$ID}, null, null, null);
    if(query!=null && query.getCount() > 0 && query.moveToFirst()) {
      String update_at = query.getString(0);
      i = writeDB.delete(MUSIC_TABLENAME, "update_at=?", new String[]{update_at});
      // writeDB.execSQL("delete from " + MUSIC_TABLENAME + " where update_at = (select min(update_at) from " + MUSIC_TABLENAME + ") and list_id = '" + PlayhistoryFragment.HISTORY$ID + "'");
      query.close();
    }
    close();
    return i;
  }

  public int countOfPlayList(String playlistId) {
    checkAndInitReadDB();
    Cursor query = readDB.query(MUSIC_TABLENAME, new String[]{"count(*)"}, "list_id=?", new String[]{playlistId}, null, null, null);
    int co = 0;
    if (query != null && query.getCount() > 0 && query.moveToFirst()) {
      co = query.getInt(0);
      query.close();
    }
    close();
    return co;
  }

  // 修改一个播放列表的信息
  public boolean updatePlaylist(String _id, String name, String remark) {

    ContentValues contentValues = new ContentValues();
    if (!TextUtils.isEmpty(name)) {
      contentValues.put("name", name);
    }
    if (!TextUtils.isEmpty(remark)) {
      contentValues.put("remark", remark);
    }
    if (contentValues.size() <= 0) {
      return false;
    }
    checkAndInitWriteDB();
    boolean b = writeDB.update(LISTS_TABLENAME, contentValues, "_id=?", new String[]{_id}) > 0;
    close();
    return b;
  }

  // 清空一个播放列表的全部内容
  public boolean clearPlayList(String playLisstId) {
    checkAndInitWriteDB();
    int delete = writeDB.delete(MUSIC_TABLENAME, "list_id=?", new String[]{playLisstId});
    close();
    return delete > 0;
  }

  //从播放列表移除某首歌曲
  public boolean removeMusicFromPlayList(String playList_id, Music m) {
    checkAndInitWriteDB();
    int delete = writeDB.delete(MUSIC_TABLENAME, "_id=? and list_id=?", new String[]{m.get_id(), playList_id});
    close();
    return delete > 0;
  }

  // 添加歌曲到播放列表
  public void addMusicToPlayList(String playList_id, Music m) {
    if (m == null) {
      return;
    }
    if (playList_id != null) {
      m.setList_id(playList_id);
    }
    checkAndInitWriteDB();
    // 查询数据是否存在
    Cursor cursor = writeDB.rawQuery("SELECT * from " + MUSIC_TABLENAME + " where list_id = ? and _id = ?", new String[]{playList_id, m.get_id()});
    if (cursor != null && cursor.getCount() > 0) {
      cursor.close();
      close();
      Log.i("ogger", "播放列表：" + playList_id + ", 已存在歌曲：" + m);
      // 已存在,不用添加额
      return;
    }

    ContentValues c = new ContentValues();
    c.put("letter", m.getLetter());
    c.put("_id", m.get_id());
    c.put("_data", m.get_data());
    c.put("_display_name", m.get_display_name());
    c.put("_size", m.get_size());
    c.put("mime_type", m.getMime_type());
    c.put("date_added", m.getDate_added());
    c.put("is_drm", m.getIs_drm());
    c.put("date_modified", m.getDate_modified());
    c.put("title", m.getTitle());
    c.put("title_key", m.getTitle_key());
    c.put("duration", m.getDuration());
    c.put("composer", m.getComposer());
    c.put("track", m.getTrack());
    c.put("year", m.getYear());
    c.put("is_ringtone", m.getIs_ringtone());
    c.put("is_music", m.getIs_music());
    c.put("list_id", m.getList_id());
    c.put("is_alarm", m.getIs_alarm());
    c.put("is_notification", m.getIs_notification());
    c.put("is_podcast", m.getIs_podcast());
    c.put("bookmark", m.getBookmark());
    c.put("album_artist", m.getAlbum_artist());
    c.put("artist_id", m.getArtist_id());
    c.put("artist_key", m.getArtist_key());
    c.put("artist", m.getArtist());
    c.put("album_id", m.getAlbum_id());
    c.put("album_key", m.getAlbum_key());
    c.put("album", m.getAlbum());
    c.put("update_at", String.valueOf(System.currentTimeMillis()));
    long year = writeDB.insert(MUSIC_TABLENAME, "year", c);
    Log.i("Logger", "添加到播放列表：" + year);
    close();
  }

  // 添加歌曲到某个播放列表
  public void addMusicToPlayList(String playList_id, ArrayList<Music> musics) {
    if (musics == null || musics.size() < 1) {
      return;
    }

    checkAndInitWriteDB();
    writeDB.beginTransaction();

    for (Music m : musics) {
      if (playList_id != null)
        m.setList_id(playList_id);
      ContentValues c = new ContentValues();
      c.put("letter", m.getLetter());
      c.put("_id", m.get_id());
      c.put("_data", m.get_data());
      c.put("_display_name", m.get_display_name());
      c.put("_size", m.get_size());
      c.put("mime_type", m.getMime_type());
      c.put("date_added", m.getDate_added());
      c.put("is_drm", m.getIs_drm());
      c.put("date_modified", m.getDate_modified());
      c.put("title", m.getTitle());
      c.put("title_key", m.getTitle_key());
      c.put("duration", m.getDuration());
      c.put("composer", m.getComposer());
      c.put("track", m.getTrack());
      c.put("year", m.getYear());
      c.put("is_ringtone", m.getIs_ringtone());
      c.put("is_music", m.getIs_music());
      c.put("list_id", m.getList_id());
      c.put("is_alarm", m.getIs_alarm());
      c.put("is_notification", m.getIs_notification());
      c.put("is_podcast", m.getIs_podcast());
      c.put("bookmark", m.getBookmark());
      c.put("album_artist", m.getAlbum_artist());
      c.put("artist_id", m.getArtist_id());
      c.put("artist_key", m.getArtist_key());
      c.put("artist", m.getArtist());
      c.put("album_id", m.getAlbum_id());
      c.put("album_key", m.getAlbum_key());
      c.put("album", m.getAlbum());
      c.put("update_at", String.valueOf(System.currentTimeMillis()));
      writeDB.insert(MUSIC_TABLENAME, "year", c);
    }
    writeDB.setTransactionSuccessful();
    writeDB.endTransaction();
    close();
  }


  private void checkAndInitReadDB() {
    if (readDB == null) {
      readDB = dataBaseOpenHelper.getReadableDatabase();
    }
    if (!readDB.isOpen()) {
      readDB = dataBaseOpenHelper.getReadableDatabase();
    }
  }

  private void checkAndInitWriteDB() {
    if (writeDB == null) {
      writeDB = dataBaseOpenHelper.getWritableDatabase();
    }
    if (!writeDB.isOpen()) {
      writeDB = dataBaseOpenHelper.getWritableDatabase();
    }
  }

  public void close() {
    // if (readDB != null) {
    //   readDB.close();
    //   readDB = null;
    // }
    // if (writeDB != null) {
    //   writeDB.close();
    //   writeDB = null;
    // }
  }

  public void superClose() {
    if (readDB != null) {
      readDB.close();
      readDB = null;
    }
    if (writeDB != null) {
      writeDB.close();
      writeDB = null;
    }
    dataBaseManager = null;
  }


  private Music getMusicFromCursor(Cursor cursor) {
    if (null == cursor) {
      return null;
    }
    if (cursor.getCount() <= 0) {
      return null;
    }

    Music music = null;
    try {
      String letter = cursor.getString(cursor.getColumnIndex("letter"));
      String _id = cursor.getString(cursor.getColumnIndex("_id"));
      String _data = cursor.getString(cursor.getColumnIndex("_data"));
      String _display_name = cursor.getString(cursor.getColumnIndex("_display_name"));
      String _size = cursor.getString(cursor.getColumnIndex("_size"));
      String mime_type = cursor.getString(cursor.getColumnIndex("mime_type"));
      String date_added = cursor.getString(cursor.getColumnIndex("date_added"));
      String is_drm = cursor.getString(cursor.getColumnIndex("is_drm"));
      String date_modified = cursor.getString(cursor.getColumnIndex("date_modified"));
      String title = cursor.getString(cursor.getColumnIndex("title"));
      String title_key = cursor.getString(cursor.getColumnIndex("title_key"));
      String duration = cursor.getString(cursor.getColumnIndex("duration"));
      String composer = cursor.getString(cursor.getColumnIndex("composer"));
      String track = cursor.getString(cursor.getColumnIndex("track"));
      String year = cursor.getString(cursor.getColumnIndex("year"));
      String is_ringtone = cursor.getString(cursor.getColumnIndex("is_ringtone"));
      String is_music = cursor.getString(cursor.getColumnIndex("is_music"));
      String list_id = cursor.getString(cursor.getColumnIndex("list_id"));
      String is_alarm = cursor.getString(cursor.getColumnIndex("is_alarm"));
      String is_notification = cursor.getString(cursor.getColumnIndex("is_notification"));
      String is_podcast = cursor.getString(cursor.getColumnIndex("is_podcast"));
      String bookmark = cursor.getString(cursor.getColumnIndex("bookmark"));
      String album_artist = cursor.getString(cursor.getColumnIndex("album_artist"));
      String artist_id = cursor.getString(cursor.getColumnIndex("artist_id"));
      String artist_key = cursor.getString(cursor.getColumnIndex("artist_key"));
      String artist = cursor.getString(cursor.getColumnIndex("artist"));
      String album_id = cursor.getString(cursor.getColumnIndex("album_id"));
      String album_key = cursor.getString(cursor.getColumnIndex("album_key"));
      String album = cursor.getString(cursor.getColumnIndex("album"));
      String updateAt = cursor.getString(cursor.getColumnIndex("update_at"));
      music = new Music();
      music.set_data(_data);
      music.setLetter(letter);
      music.set_id(_id);
      music.set_display_name(_display_name);
      music.set_size(_size);
      music.setMime_type(mime_type);
      music.setDate_added(date_added);
      music.setIs_drm(is_drm);
      music.setDate_modified(date_modified);
      music.setTitle(title);
      music.setTitle_key(title_key);
      music.setDuration(duration);
      music.setComposer(composer);
      music.setTrack(track);
      music.setYear(year);
      music.setIs_ringtone(is_ringtone);
      music.setIs_music(is_music);
      music.setList_id(list_id);
      music.setIs_alarm(is_alarm);
      music.setIs_notification(is_notification);
      music.setIs_podcast(is_podcast);
      music.setBookmark(bookmark);
      music.setAlbum_artist(album_artist);
      music.setArtist_id(artist_id);
      music.setArtist_key(artist_key);
      music.setArtist(artist);
      music.setAlbum_id(album_id);
      music.setAlbum_key(album_key);
      music.setAlbum(album);
      music.setUpdateAt(updateAt);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Log.i(TAG, music.toString());
    return music;
  }


  private class DataBaseOpenHelper extends SQLiteOpenHelper {

    public DataBaseOpenHelper(Context context, int version) {
      super(context, "data.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.beginTransaction();
      StringBuilder sqlbuild = new StringBuilder();
      sqlbuild.append("CREATE TABLE IF NOT EXISTS " + MUSIC_TABLENAME + " (");
      sqlbuild.append("letter CHAR,");
      sqlbuild.append("_id CHAR,");
      sqlbuild.append("_data CHAR,");
      sqlbuild.append("_display_name CHAR,");
      sqlbuild.append("_size CHAR,");
      sqlbuild.append("mime_type CHAR,");
      sqlbuild.append("date_added CHAR,");
      sqlbuild.append("is_drm CHAR,");
      sqlbuild.append("date_modified CHAR,");
      sqlbuild.append("title CHAR,");
      sqlbuild.append("title_key CHAR,");
      sqlbuild.append("duration CHAR,");
      sqlbuild.append("composer CHAR,");
      sqlbuild.append("track CHAR,");
      sqlbuild.append("year CHAR,");
      sqlbuild.append("is_ringtone CHAR,");
      sqlbuild.append("is_music CHAR,");
      sqlbuild.append("is_alarm CHAR,");
      sqlbuild.append("is_notification CHAR,");
      sqlbuild.append("is_podcast CHAR,");
      sqlbuild.append("bookmark CHAR,");
      sqlbuild.append("album_artist CHAR,");
      sqlbuild.append("artist_id CHAR,");
      sqlbuild.append("artist_key CHAR,");
      sqlbuild.append("artist CHAR,");
      sqlbuild.append("album_id CHAR,");
      sqlbuild.append("album_key CHAR,");
      sqlbuild.append("album CHAR,");
      sqlbuild.append("list_id CHAR,");
      sqlbuild.append("update_at CHAR");
      sqlbuild.append(");");
      String createLoveTableSQL = sqlbuild.toString();
      db.execSQL(createLoveTableSQL);
      db.execSQL("CREATE TABLE IF NOT EXISTS " + LISTS_TABLENAME + " (_id CHAR, name CHAR, remark CHAR);");
      db.setTransactionSuccessful();
      db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }


}
