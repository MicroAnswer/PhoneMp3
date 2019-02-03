package answer.android.phonemp3.bean;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import answer.android.phonemp3.db.DataBaseManager;

/**
 * 音乐bean对象
 * Created by Micro on 2017/6/14.
 */

@Table(name = DataBaseManager.MUSIC_TABLENAME)
public class Music implements Parcelable, Comparable<Music> {

  @Column(name = "letter", isId = false, autoGen = false)
  private String letter;

  @Column(name = "_id", isId = true, autoGen = false)
  private String _id;

  @Column(name = "_data", isId = false, autoGen = false)
  private String _data;

  @Column(name = "_display_name", isId = false, autoGen = false)
  private String _display_name;

  @Column(name = "_size", isId = false, autoGen = false)
  private String _size;

  @Column(name = "mime_type", isId = false, autoGen = false)
  private String mime_type;

  @Column(name = "date_added", isId = false, autoGen = false)
  private String date_added;

  @Column(name = "is_drm", isId = false, autoGen = false)
  private String is_drm;

  @Column(name = "date_modified", isId = false, autoGen = false)
  private String date_modified;

  @Column(name = "title", isId = false, autoGen = false)
  private String title;

  @Column(name = "title_key", isId = false, autoGen = false)
  private String title_key;

  @Column(name = "duration", isId = false, autoGen = false)
  private String duration;

  @Column(name = "composer", isId = false, autoGen = false)
  private String composer;

  @Column(name = "track", isId = false, autoGen = false)
  private String track;

  @Column(name = "year", isId = false, autoGen = false)
  private String year;

  @Column(name = "is_ringtone", isId = false, autoGen = false)
  private String is_ringtone;

  @Column(name = "is_music", isId = false, autoGen = false)
  private String is_music;

  @Column(name = "is_alarm", isId = false, autoGen = false)
  private String is_alarm;

  @Column(name = "is_notification", isId = false, autoGen = false)
  private String is_notification;

  @Column(name = "is_podcast", isId = false, autoGen = false)
  private String is_podcast;

  @Column(name = "bookmark", isId = false, autoGen = false)
  private String bookmark;

  @Column(name = "album_artist", isId = false, autoGen = false)
  private String album_artist;

  @Column(name = "artist_id", isId = false, autoGen = false)
  private String artist_id;

  @Column(name = "artist_key", isId = false, autoGen = false)
  private String artist_key;

  @Column(name = "artist", isId = false, autoGen = false)
  private String artist;

  @Column(name = "album_id", isId = false, autoGen = false)
  private String album_id;

  @Column(name = "album_key", isId = false, autoGen = false)
  private String album_key;

  @Column(name = "album", isId = false, autoGen = false)
  private String album;

  @Column(name = "list_id", isId = false, autoGen = false)
  private String list_id;

  public boolean isLoved;

  public Music() {
  }

  public Music(Music music) {
    this.letter = new String(music.letter + "");
    this._id = new String(music._id + "");
    this._data = new String(music._data + "");
    this._display_name = new String(music._display_name + "");
    this._size = new String(music._size + "");
    this.mime_type = new String(music.mime_type + "");
    this.date_added = new String(music.date_added + "");
    this.is_drm = new String(music.is_drm + "");
    this.date_modified = new String(music.date_modified + "");
    this.title = new String(music.title + "");
    this.title_key = new String(music.title_key + "");
    this.duration = new String(music.duration + "");
    this.composer = new String(music.composer + "");
    this.track = new String(music.track + "");
    this.year = new String(music.year + "");
    this.is_ringtone = new String(music.is_ringtone + "");
    this.is_music = new String(music.is_music + "");
    this.is_alarm = new String(music.is_alarm + "");
    this.is_notification = new String(music.is_notification + "");
    this.is_podcast = new String(music.is_podcast + "");
    this.bookmark = new String(music.bookmark + "");
    this.album_artist = new String(music.album_artist + "");
    this.artist_id = new String(music.artist_id + "");
    this.artist_key = new String(music.artist_key + "");
    this.artist = new String(music.artist + "");
    this.album_id = new String(music.album_id + "");
    this.album_key = new String(music.album_key + "");
    this.album = new String(music.album + "");
    this.list_id = new String(music.list_id + "");
    this.updateAt = new String(music.getUpdateAt());
  }

  protected Music(Parcel in) {
    readFromParcel(in);
  }

  public Music readFromParcel(Parcel in) {
    letter = in.readString();
    _id = in.readString();
    _data = in.readString();
    _display_name = in.readString();
    _size = in.readString();
    mime_type = in.readString();
    date_added = in.readString();
    is_drm = in.readString();
    date_modified = in.readString();
    title = in.readString();
    title_key = in.readString();
    duration = in.readString();
    composer = in.readString();
    track = in.readString();
    year = in.readString();
    is_ringtone = in.readString();
    is_music = in.readString();
    is_alarm = in.readString();
    is_notification = in.readString();
    is_podcast = in.readString();
    bookmark = in.readString();
    album_artist = in.readString();
    artist_id = in.readString();
    artist_key = in.readString();
    artist = in.readString();
    album_id = in.readString();
    album_key = in.readString();
    album = in.readString();
    list_id = in.readString();
    updateAt = in.readString();
    return this;
  }

  public String getList_id() {
    return list_id;
  }

  public void setList_id(String list_id) {
    this.list_id = list_id;
  }

  public static final Creator<Music> CREATOR = new Creator<Music>() {
    @Override
    public Music createFromParcel(Parcel in) {
      return new Music(in);
    }

    @Override
    public Music[] newArray(int size) {
      return new Music[size];
    }
  };

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
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

  @Override
  public String toString() {
    return "Music{" +
            "letter='" + letter + '\'' +
            ", _id='" + _id + '\'' +
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
            ", list_id='" + list_id + '\'' +
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(letter);
    dest.writeString(_id);
    dest.writeString(_data);
    dest.writeString(_display_name);
    dest.writeString(_size);
    dest.writeString(mime_type);
    dest.writeString(date_added);
    dest.writeString(is_drm);
    dest.writeString(date_modified);
    dest.writeString(title);
    dest.writeString(title_key);
    dest.writeString(duration);
    dest.writeString(composer);
    dest.writeString(track);
    dest.writeString(year);
    dest.writeString(is_ringtone);
    dest.writeString(is_music);
    dest.writeString(is_alarm);
    dest.writeString(is_notification);
    dest.writeString(is_podcast);
    dest.writeString(bookmark);
    dest.writeString(album_artist);
    dest.writeString(artist_id);
    dest.writeString(artist_key);
    dest.writeString(artist);
    dest.writeString(album_id);
    dest.writeString(album_key);
    dest.writeString(album);
    dest.writeString(list_id);
    dest.writeString(updateAt);
  }

  public void setUpdateAt(String updateAt) {
    this.updateAt = updateAt;
  }

  public String getUpdateAt() {
    return updateAt;
  }

  @Column(name = "update_at", isId = false, autoGen = false)
  private String updateAt;
}
