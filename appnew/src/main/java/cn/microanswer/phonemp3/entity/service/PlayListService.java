package cn.microanswer.phonemp3.entity.service;

import java.util.List;

import androidx.annotation.NonNull;
import cn.microanswer.phonemp3.entity.PlayList;

public interface PlayListService {
    /**
     * <pre>
     * 根据播放列表ID获取某播放列表。
     * 当 playListID 是
     * Database.PLAYLIST_ID_ALL、
     * Database.PLAYLIST_ID_MYLOVE、
     * Database.PLAYLIST_ID_CURRENT、
     * Database.PLAYLIST_ID_HISTORY
     * 之一时， 必然能获取到播放列表结果。
     * 如果为其它值，可能返回 null；
     * </pre>
     *
     * @param playListId 播放列表id。
     * @return 对应播放列表 or null。
     * @throws Exception 错误信息。
     */
    List<PlayList> getPlayList(@NonNull String playListId) throws Exception;

    /**
     * <pre>
     * 添加一个播放列表，传入的playList不应该设置好了id。
     * </pre>
     *
     * @param playList 传入要添加的播放列表。
     * @return 如果成功 返回true，否则返回false。
     * @throws Exception 错误信息。
     */
    boolean addPlayList(@NonNull PlayList playList) throws Exception;

    /**
     * <pre>
     *     根据播放列表id删除某一个播放列表。
     * </pre>
     * @param playListId 播放列表id。
     * @return 是否删除成功。
     * @throws Exception 错误信息。
     */
    boolean deletePlayList(@NonNull String playListId) throws Exception;

    /**
     * <pre>
     *     更新播放列表。
     * </pre>
     * @param playList 要跟新的播放列表。
     * @return 是否更新成功。
     * @throws Exception 错误信息。
     */
    boolean updatePlayList(@NonNull PlayList playList) throws Exception;
}
