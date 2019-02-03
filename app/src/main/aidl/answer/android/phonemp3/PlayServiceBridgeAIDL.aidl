// PlayServiceBridgeAIDL.aidl
package answer.android.phonemp3;
import answer.android.phonemp3.bean.Music;

// Declare any non-default types here with import statements
// 远程播放服务链接桥梁

interface PlayServiceBridgeAIDL {
    /*
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     *
     *   void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
     *       double aDouble, String aString);
     */

     /*
     * 播放某首歌曲
     */
     void play(inout Music music);

     // 播放播放列表中某个位置上的歌曲
     void playIndex(int index);

     // 播放下一曲
     void playNext();

     // 播放上一曲
     void playLast();

     // 暂停播放
     void pause();

     // 是否正在播放
     boolean isPlaying();

     // 是否暂停
     boolean isPause();

     // 是否已设定退出定时
     boolean isSetTime();

     // 从暂停回复
     void rePause();

     // 是否停止
     boolean isStop();

     // 获取audiosessionid
     int getAudioSessionId();

     // 获取正在播放的歌曲
     Music getCurrentMusic();

     // 获取当前播放的歌曲播放到的位置
     long getCurrentMusicPosition();

     // 获取播放顺序
     int getPlayWay();

     // 设置播放顺序
     void setPlayWay(int way);

     // 获取当前播放的歌曲在播放列表中的位置
     int getCurrentMusicIndex();

     // 获取正在播放列表
     // List<Music> getCurrentMusics();

     // 设置正在播放列表的歌曲
     // void setPlayingList(inout List<Music> musics, String playListId);

     // 获取正在播放列表id
     String getPlayListId();

     // 移动到某个位置播放
     void seekTo(long position);

     // 停止播放
     void stop();

     // 从播放列表移除某一首歌曲
     void removeFromPlayList(inout Music music);

     // 播放暂停会发送广播
     // 开始播放前会发送广播
     // 开始播放后每一秒会发送一个广播，播放进度
     // 播放完成后会发送广播

}
