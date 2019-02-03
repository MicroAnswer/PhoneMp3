package cn.microanswer.phonemp3.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import cn.microanswer.phonemp3.Database;

/**
 * Created by Microanswer on 2017/6/19.
 */
@Table(name = "playlisttable", database = Database.class)
public class PlayList extends BaseModel {

    private List<Music> musics;

    @PrimaryKey
    @Column(name = "_id")
    private String id;

    @Column
    private String name;

    @Column
    private String ramark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "musics")
    public List<Music> getMusics() {
        if (null == musics || musics.isEmpty()) {
            musics = SQLite.select().from(Music.class)
                    .where(Music_Table.list_id.eq(id))
                    .queryList();
        }
        return musics;
    }

    // @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "musics")
    public List<Music> getMusics(boolean ascending) {
        return SQLite.select().from(Music.class)
                .where(Music_Table.list_id.eq(id))
                .orderBy(Music_Table.update_at, ascending)
                .queryList();
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRamark() {
        return ramark;
    }

    public void setRamark(String ramark) {
        this.ramark = ramark;
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "musics=" + musics +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ramark='" + ramark + '\'' +
                '}';
    }

    public static PlayList getByNameAndRemark(String name, String remark) {
        return SQLite.select().from(PlayList.class).where(PlayList_Table.name.eq(name))
                .and(PlayList_Table.ramark.eq(remark)).querySingle();
    }
}
