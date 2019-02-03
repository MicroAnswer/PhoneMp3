package cn.microanswer.phonemp3.entity.service;

import java.util.List;

import androidx.annotation.NonNull;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.PlayList;

public interface MusicService {
    /**
     * <pre>
     *     添加一首歌曲到某个播放列表中。
     * </pre>
     * @param music 要添加的歌曲。
     * @param playListId 播放列表id。
     * @return 是否添加成功。
     * @throws Exception 错误信息。
     */
    boolean addMusic(@NonNull Music music, @NonNull String playListId) throws Exception;

    /**
     * <pre>
     *     添加一首歌曲到某个播放列表中。
     * </pre>
     * @param music 要添加的歌曲。
     * @param playList 播放列表。
     * @return 是否添加成功。
     * @throws Exception 错误信息。
     */
    boolean addMusic(@NonNull Music music,@NonNull PlayList playList) throws Exception;

    /**
     * <pre>
     *     判断某歌曲是否纯在某个播放列表中。
     * </pre>
     * @param music 要判断的歌曲。
     * @param playListId 播放列表id。
     * @return 存在返回 true， 否则返回 false；
     * @throws Exception 错误信息。
     */
    boolean exist(@NonNull Music music,@NonNull String playListId) throws Exception;

    /**
     * <pre>
     *     更新某首歌曲的信息。
     * </pre>
     * @param music 要更新的歌曲。
     * @return 是否更新成功。
     * @throws Exception 错误信息。
     */
    boolean updateMusic(@NonNull Music music) throws Exception;

    /**
     * <pre>
     *     更新某首歌曲的 update_at 字段为当前时间
     * </pre>
     * @param musicId 歌曲id
     * @return 是否更新成功。
     * @throws Exception 错误信息。
     */
    boolean updateMusicTime(String musicId) throws Exception;

    /**
     * <pre>
     *     更新某首歌曲的 update_at 字段为当前时间
     * </pre>
     * @param music 要更新的歌曲。
     * @return 是否更新成功。
     * @throws Exception 错误信息。
     */
    boolean updateMusicTime(@NonNull Music music) throws Exception;

    /**
     * <pre>
     *     获取播放列表中的歌曲。
     * </pre>
     * @param playList 播放列表。
     * @return 歌曲集合，如果某个播放列表中没有歌曲，返回一个空集合。
     * @throws Exception 错误信息。
     */
    List<Music> getMusics(@NonNull PlayList playList) throws Exception;

    /**
     * <pre>
     *     获取播放列表中的歌曲。
     * </pre>
     * @param playListId 播放列表id。
     * @return 歌曲集合。
     * @throws Exception 错误信息。
     */
    List<Music> getMusics(@NonNull String playListId) throws Exception;

    /**
     * <pre>
     *     删除某一首歌曲。
     * </pre>
     * @param musicId 歌曲id。
     * @return 是否删除成功。
     * @throws Exception 错误信息。
     */
    boolean deleteMusic(@NonNull String musicId) throws Exception;

    /**
     * <pre>
     *     删除某一首歌曲。
     * </pre>
     * @param music 要删除的歌曲。
     * @return 是否删除成功。
     * @throws Exception 错误信息。
     */
    boolean deleteMusic(@NonNull Music music) throws Exception;
}
