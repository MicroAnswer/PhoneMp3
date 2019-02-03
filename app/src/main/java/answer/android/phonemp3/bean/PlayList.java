package answer.android.phonemp3.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

import answer.android.phonemp3.db.DataBaseManager;

/**
 * Created by Microanswer on 2017/6/19.
 */

@Table(name= DataBaseManager.LISTS_TABLENAME)
public class PlayList {

  private List<Music> musics;

  @Column(name="_id", isId = true, autoGen = false)
  private String id;

  @Column(name = "name", isId = false, autoGen = false)
  private String name;

  @Column(name = "remark", isId = false, autoGen = false)
  private String ramark;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Music> getMusics() {
    return musics;
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
}
