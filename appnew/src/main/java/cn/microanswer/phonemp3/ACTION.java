package cn.microanswer.phonemp3;

/**
 * Created by Micro on 2018年8月7日21:20:05
 */

public class ACTION {
    /**
     * 开始播放歌曲前的广播
     */
    public final static String BEFOR_PLAY = "cn.microanswer.beforplay_b_a";
    /**
     * 暂停恢复到播放的广播
     */
    public final static String PAUSE_2_PLAY = "cn.microanswer.pause2play_b_c";
    /**
     * 播放变成暂停的广播
     */
    public final static String PLAY_2_PAUSE = "cn.microanswer.play2pause_b_c";
    /**
     * 播放完成的广播
     */
    public final static String AFTER_PLAY = "cn.microanswer.afterplay_c_b";

    /**
     * 主题色改变。
     */
    public final static String THEME_COLOR_CHANGE = "cn.microanswer.themechange";

    /**
     * 设置当前正在播放的歌曲。 MainAnswer.java 第91 行使用 和 CoreServices1.java 第199行左右使用
     */
    public static final String SET_CURRENT_MUSIC = "cn.microanswer.setcurrentmusic";

    /**
     * 设置下一曲要播放的歌曲。
     */
    public static final String SET_NEXT_MUSIC = "cn.microanswer.setnextmusic";

    /**
     * 歌曲被删除的通知
     */
    public static final String MUSIC_DELETED = "cn.microanswer.deletedmusic";

    public static class NOTIFICATION {
        /**
         * 播放上一曲的Action
         */
        public final static String PREVIOUS = "cn.microanswer.previos_s_2";
        /**
         * 播放暂停的Action
         */
        public final static String TOGGLE = "cn.microanswer.toggle_2_x";
        /**
         * 播放下一曲的Action
         */
        public final static String NEXT = "cn.microanswer.next_n_r";

        /**
         * 收藏的action
         */
        public final static String LOVE = "cn.microanswer.love_l_z";
    }
}
