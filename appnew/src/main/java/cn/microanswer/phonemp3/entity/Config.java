package cn.microanswer.phonemp3.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.microanswer.phonemp3.Database;

@Table(name = "configtable", database = Database.class)
public class Config extends BaseModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column(name = "_Key")
    private String key;

    @Column(name = "_value")
    private String value;

    @Column(name = "_desc")
    private String desc;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Config getConfig(String key) {
        return SQLite.select().from(Config.class).where(Config_Table._Key.eq(key)).querySingle();
    }

}
