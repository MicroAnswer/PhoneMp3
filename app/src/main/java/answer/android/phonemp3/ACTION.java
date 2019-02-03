package answer.android.phonemp3;

/**
 * Created by Micro on 2017/6/18.
 */

public class ACTION {
  /**
   * 开始播放歌曲前的广播
   */
  public final static String BEFOR_PLAY = "com.microanswer.beforplay_b_a";
  /**
   * 播放暂停的发广播
   */
  public final static String PAUSE_PLPAY = "com.microanswer.pauseplay_b_c";
  /**
   * 播放完成的广播
   */
  public final static String AFTER_PLAY = "com.microanswer.afterplay_c_b";

  public final static String PLAY_FLISH = "com.microanswer.playempty_ddd_seftgijlllll";

  public final static String CLEAR_PLAYINGLIST = "com.microanswer.clearplayinglist..asd";

  /**
   * 收藏单有变化
   */
  public final static String LOVE_CHANGE = "com.microanswer.loveplaylistchange.sdfg";

  public final static String PLAYINDEX_CHANGE = "com.microanswer.palayindexhasbeenchanged";

  public final static String EXTRA_INDEx = "sdhgiudnf";

  public final static String SET_TIMEOUT = "wertghsfkudshfkj34634hbfvkdsf";

  public final static String CANCEL_TIMEOUT = "tj3245yujsadfsferyhrjhrhttggsagrsg";

  public final static String EXTRA_TIME = "olkhyytvbxcjf";

  public final static String DELETE = "oiweufnvcxbvcxz5486sdf41";

  /**
   * 通知栏按钮点击发送的广播
   */
  public static final class ASK {
    /**
     * 上一曲
     */
    public final static String UP = "com.microanswer.playlast_as";
    /**
     * 下一曲
     */
    public final static String NEXT = "com.microanswer.playnext_sa";

    /**
     * 下一曲播放
     */
    public final static String NEXT_PLAY = "com.microanswer.nextplay_as_next";
    /**
     * 播放暂停
     */
    public final static String PLAY$PAUSE = "com.microanswer.playpause_ed";

    /**
     * 收藏
     */
    public final static String LOVE = "com.microanswer.love_oj";

    /**
     * 退出
     */
    public final static String EXIT = "com.microanswer.eexxiitt_ttiixxee";



    public static final class CONTROL {

      /**
       * 播放歌曲发送的action
       */
      public final static String PLAY = "com.microanswer.playmusicskwehfsdbfkighrb.asdf";
      public final static String PLAYALL = PLAY + ".eirgnfv";
      public final static String REMOVEFROMLISTS = "com.microanswer.removemusic";

      // 播放某一首歌的歌曲数据
      public final static String EXTRAS_MUSIC = "_sssdddfffm";
      // 播放某个播放列表的列表数据
      public final static String EXTRAS_MUSICS = "_sssdddfffms";
      public final static String EXTRAS_MUSICSLISTID = "_sssdddfffms_id";

    }

  }
}
