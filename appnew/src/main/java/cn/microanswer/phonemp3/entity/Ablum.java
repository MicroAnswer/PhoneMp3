package cn.microanswer.phonemp3.entity;

import com.raizlabs.android.dbflow.structure.BaseModel;

import androidx.annotation.NonNull;

@Deprecated
public class Ablum extends BaseModel implements Comparable<Ablum> {
    private String _id;
    private String album;
    private String album_key;
    private String minyear;
    private String maxyear;
    private String artist;
    private String artist_id;
    private String artist_key;
    private String numsongs;
    private String album_art;
    private String letter;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbum_key() {
        return album_key;
    }

    public void setAlbum_key(String album_key) {
        this.album_key = album_key;
    }

    public String getMinyear() {
        return minyear;
    }

    public void setMinyear(String minyear) {
        this.minyear = minyear;
    }

    public String getMaxyear() {
        return maxyear;
    }

    public void setMaxyear(String maxyear) {
        this.maxyear = maxyear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public String getNumsongs() {
        return numsongs;
    }

    public void setNumsongs(String numsongs) {
        this.numsongs = numsongs;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Ablum) {
            Ablum aa = (Ablum) obj;
            return null != aa.get_id() && aa.get_id().equals(get_id());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Ablum{" +
                "_id='" + _id + '\'' +
                ", album='" + album + '\'' +
                ", album_key='" + album_key + '\'' +
                ", minyear='" + minyear + '\'' +
                ", maxyear='" + maxyear + '\'' +
                ", artist='" + artist + '\'' +
                ", artist_id='" + artist_id + '\'' +
                ", artist_key='" + artist_key + '\'' +
                ", numsongs='" + numsongs + '\'' +
                ", album_art='" + album_art + '\'' +
                ", letter='" + letter + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Ablum o) {
        return letter.compareTo(o.getLetter());
    }
}
