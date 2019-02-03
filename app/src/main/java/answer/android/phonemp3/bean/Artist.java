package answer.android.phonemp3.bean;

import androidx.annotation.NonNull;

/**
 * 歌手
 * Created by Microanswer on 2017/7/5.
 */

public class Artist implements Comparable<Artist> {
  private String _id, artist, artist_key, number_of_albums, number_of_tracks, leeter;

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getArtist_key() {
    return artist_key;
  }

  public void setArtist_key(String artist_key) {
    this.artist_key = artist_key;
  }

  public String getNumber_of_albums() {
    return number_of_albums;
  }

  public void setNumber_of_albums(String number_of_albums) {
    this.number_of_albums = number_of_albums;
  }

  public String getNumber_of_tracks() {
    return number_of_tracks;
  }

  public void setNumber_of_tracks(String number_of_tracks) {
    this.number_of_tracks = number_of_tracks;
  }

  public String getLeeter() {
    return leeter;
  }

  @Override
  public String toString() {
    return "Artist{" +
            "_id='" + _id + '\'' +
            ", artist='" + artist + '\'' +
            ", artist_key='" + artist_key + '\'' +
            ", number_of_albums='" + number_of_albums + '\'' +
            ", number_of_tracks='" + number_of_tracks + '\'' +
            ", leeter='" + leeter + '\'' +
            '}';
  }

  public void setLeeter(String leeter) {
    this.leeter = leeter;
  }

  @Override
  public int compareTo(@NonNull Artist o) {
    return getLeeter().compareTo(o.getLeeter());
  }
}
