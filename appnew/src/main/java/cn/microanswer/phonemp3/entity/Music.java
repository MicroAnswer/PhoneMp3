package cn.microanswer.phonemp3.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import androidx.annotation.NonNull;
import cn.microanswer.phonemp3.Database;

/**
 * 音乐bean对象
 * Created by Micro on 2017/6/14.
 */

@Table(name = "allmusictable", database = Database.class)
public class Music extends BaseModel implements Comparable<Music> {

    @Column
    private String letter;

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String _data;

    @Column
    private String _display_name;

    @Column
    private String _size;

    @Column
    private String mime_type;

    @Column
    private String date_added;

    @Column
    private String is_drm;

    @Column
    private String date_modified;

    @Column
    private String title;

    @Column
    private String title_key;

    @Column
    private String duration;

    @Column
    private String composer;

    @Column
    private String track;

    @Column
    private String year;

    @Column
    private String is_ringtone;

    @Column
    private String is_music;

    @Column
    private String is_alarm;

    @Column
    private String is_notification;

    @Column
    private String is_podcast;

    @Column
    private String bookmark;

    @Column
    private String album_artist;

    @Column
    private String artist_id;

    @Column
    private String artist_key;

    @Column
    private String artist;

    @Column
    private String album_id;

    @Column
    private String album_key;

    @Column
    private String album;

    @Column(name = "list_id")
    private String listId;

    @Column(name = "update_at")
    private String updateAt;

    @Column(name = "cover_path")
    private String coverPath;

    @Column
    private String isNew;

    public static Music from(Music music) {
        if (music == null) {
            return null;
        }
        Music music1 = new Music();
        music1.setListId(music.getListId());
        music1.setId(music.getId());
        music1.setIsNew(music.getIsNew());
        music1.set_data(music.get_data());
        music1.set_display_name(music.get_display_name());
        music1.set_size(music.get_size());
        music1.setAlbum(music.getAlbum());
        music1.setAlbum_artist(music.getAlbum_artist());
        music1.setAlbum_id(music.getAlbum_id());
        music1.setAlbum_key(music.getAlbum_key());
        music1.setArtist(music.getArtist());
        music1.setArtist_id(music.getArtist_id());
        music1.setArtist_key(music.getArtist_key());
        music1.setBookmark(music.getBookmark());
        music1.setComposer(music.getComposer());
        music1.setCoverPath(music.getCoverPath());
        music1.setDate_added(music.getDate_added());
        music1.setDate_modified(music.getDate_modified());
        music1.setDuration(music.getDuration());
        music1.setIs_alarm(music.getIs_alarm());
        music1.setIs_drm(music.getIs_drm());
        music1.setIs_music(music.getIs_music());
        music1.setIs_notification(music.getIs_notification());
        music1.setIs_podcast(music.getIs_podcast());
        music1.setIs_ringtone(music.getIs_ringtone());
        music1.setLetter(music.getLetter());
        music1.setMime_type(music.getMime_type());
        music1.setTitle(music.getTitle());
        music1.setTitle_key(music.getTitle_key());
        music1.setTrack(music.getTrack());
        music1.setUpdateAt(music.getUpdateAt());
        music1.setYear(music.getYear());
        return music1;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getIsNew() {
        return isNew;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListId() {
        return listId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String get_data() {
        return _data;
    }

    public void set_data(String _data) {
        this._data = _data;
    }

    public String get_display_name() {
        return _display_name;
    }

    public void set_display_name(String _display_name) {
        this._display_name = _display_name;
    }

    public String get_size() {
        return _size;
    }

    public void set_size(String _size) {
        this._size = _size;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getIs_drm() {
        return is_drm;
    }

    public void setIs_drm(String is_drm) {
        this.is_drm = is_drm;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_key() {
        return title_key;
    }

    public void setTitle_key(String title_key) {
        this.title_key = title_key;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIs_ringtone() {
        return is_ringtone;
    }

    public void setIs_ringtone(String is_ringtone) {
        this.is_ringtone = is_ringtone;
    }

    public String getIs_music() {
        return is_music;
    }

    public void setIs_music(String is_music) {
        this.is_music = is_music;
    }

    public String getIs_alarm() {
        return is_alarm;
    }

    public void setIs_alarm(String is_alarm) {
        this.is_alarm = is_alarm;
    }

    public String getIs_notification() {
        return is_notification;
    }

    public void setIs_notification(String is_notification) {
        this.is_notification = is_notification;
    }

    public String getIs_podcast() {
        return is_podcast;
    }

    public void setIs_podcast(String is_podcast) {
        this.is_podcast = is_podcast;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getAlbum_artist() {
        return album_artist;
    }

    public void setAlbum_artist(String album_artist) {
        this.album_artist = album_artist;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getArtist_key() {
        return artist_key;
    }

    public void setArtist_key(String artist_key) {
        this.artist_key = artist_key;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_key() {
        return album_key;
    }

    public void setAlbum_key(String album_key) {
        this.album_key = album_key;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }

    /**
     * 查询数据库获取某首歌曲是否收藏了
     *
     * @param m
     * @return
     */
    public static boolean isLove(Music m) {
        return (m != null) && (SQLite.select().from(Music.class).where(Music_Table._data.eq(m.get_data()))
                .and(Music_Table.list_id.eq(Database.PLAYLIST_ID_MYLOVE)).querySingle() != null);
    }

    @Override
    public String toString() {
        return "Music{" +
                "letter='" + letter + '\'' +
                ", _id='" + id + '\'' +
                ", _data='" + _data + '\'' +
                ", _display_name='" + _display_name + '\'' +
                ", _size='" + _size + '\'' +
                ", mime_type='" + mime_type + '\'' +
                ", date_added='" + date_added + '\'' +
                ", is_drm='" + is_drm + '\'' +
                ", date_modified='" + date_modified + '\'' +
                ", title='" + title + '\'' +
                ", title_key='" + title_key + '\'' +
                ", duration='" + duration + '\'' +
                ", composer='" + composer + '\'' +
                ", track='" + track + '\'' +
                ", year='" + year + '\'' +
                ", is_ringtone='" + is_ringtone + '\'' +
                ", is_music='" + is_music + '\'' +
                ", is_alarm='" + is_alarm + '\'' +
                ", is_notification='" + is_notification + '\'' +
                ", is_podcast='" + is_podcast + '\'' +
                ", bookmark='" + bookmark + '\'' +
                ", album_artist='" + album_artist + '\'' +
                ", artist_id='" + artist_id + '\'' +
                ", artist_key='" + artist_key + '\'' +
                ", artist='" + artist + '\'' +
                ", album_id='" + album_id + '\'' +
                ", album_key='" + album_key + '\'' +
                ", album='" + album + '\'' +
                ", list_id='" + listId + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Music o) {
        return letter.compareTo(o.getLetter());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Music) {
            Music m = (Music) obj;

            return m._data.equals(_data);
        }

        return false;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    /**
     * 收藏 、 取消收藏 某首歌曲
     *
     * @return -1 操作失败， 1 取消收藏成功， 2 收藏成功
     */
    public static int toggleLove(Music music) {
        if (music == null) {
            return -1;
        }
        music = from(music);
        boolean love = Music.isLove(music);
        if (love) {
            // 从收藏列表删除关系。
            if (SQLite.delete().from(Music.class)
                    .where(Music_Table._data.eq(music.get_data()))
                    .and(Music_Table.list_id.eq(Database.PLAYLIST_ID_MYLOVE)).executeUpdateDelete() >= 1) {
                return 1;
            } else {
                return -1;
            }
        } else {
            // 收藏
            music.setId(0L);
            music.setListId(Database.PLAYLIST_ID_MYLOVE);
            if (music.insert() >= 1) {
                return 2;
            } else {
                return -1;
            }
        }
    }
}
