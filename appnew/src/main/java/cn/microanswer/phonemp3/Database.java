package cn.microanswer.phonemp3;

/**
 * 此文件相当于数据库配置文件
 */
@com.raizlabs.android.dbflow.annotation.Database(name = Database.name, version = Database.version)
public class Database {
    public static final String name = "data";
    public static final int version = 7;

    public static final String PLAYLIST_ID_ALL = "allmusic"; // 所有歌曲列表
    public static final String PLAYLIST_ID_MYLOVE = "loveid"; // 我的收藏列表
    public static final String PLAYLIST_ID_CURRENT = "currentplayinglist"; // 正在播放列表
    public static final String PLAYLIST_ID_HISTORY = "history"; // 播放记录列表
    public static final String CONFIG_LASTPLAYMUSIC_KEY = "lastPlayMusic"; // 保存的当前播放的歌曲

    // 用户自己创建的播放列表都只是这个名字， remark 字段是用户输入的名字。
    public static final String PLAYLIST_NAME_USEROWN = "userplaylist";
}
