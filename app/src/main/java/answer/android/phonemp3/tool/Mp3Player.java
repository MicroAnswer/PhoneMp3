package answer.android.phonemp3.tool;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import answer.android.phonemp3.bean.Music;

/**
 * 音乐播放器类
 * Created by Micro on 2017/6/16.
 */

public class Mp3Player implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
  public static final int STATE_NEVERPLAYING = 0; // 从未播放过任何音乐
  public static final int STATE_PLAYING = 1; // 正在播放
  public static final int STATE_PAUSE = 2; // 已暂停
  public static final int STATE_STOP = 3; // 已停止
  public static final int STATE_PREPAREING = 4; // 正在准备资源

  private MediaPlayer mediaPlayer;
  private Music playinMusic;
  private int state = STATE_NEVERPLAYING;
  private boolean autoPlay = true;

  public Mp3Player() {
    this.mediaPlayer = new MediaPlayer();
    this.mediaPlayer.setOnCompletionListener(this);
    this.mediaPlayer.setOnPreparedListener(this);
    this.mediaPlayer.setOnErrorListener(this);
    this.state = STATE_NEVERPLAYING;
  }

  public Music getPlayinMusic() {
    return playinMusic;
  }


  /**
   * 设置一首要播放的歌曲,但是不会播放.
   *
   * @param music
   */
  public void setMusic(Music music) {
    if (music != null) {
      this.autoPlay = false;
      this.state = STATE_PREPAREING;
      this.playinMusic = music;
      this.mediaPlayer.reset();
      try {
        this.mediaPlayer.setDataSource(music.get_data());
        this.mediaPlayer.prepare();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void playMusic(Music music) {
    // Log.i("Logger", "开始播放:" + music);
    if (music != null) {
      this.autoPlay = true;
      this.state = STATE_PREPAREING;
      this.playinMusic = music;
      this.mediaPlayer.reset();
      try {
        this.mediaPlayer.setDataSource(music.get_data());
        this.mediaPlayer.prepare();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public int getAudioSessionId(){
    return mediaPlayer.getAudioSessionId();
  }


  public void release() {
    this.mediaPlayer.release();
  }

  /**
   * 从暂停/停止状态变为播放状态
   */
  public void play() {

    // Log.i("Mp3Player", "play:" + state);
    if (state == STATE_PAUSE || state == STATE_STOP) {
      this.autoPlay = true;
      if (mp3PlayerListeners != null) {
        for (int i = 0; i < mp3PlayerListeners.size(); i++) {
          try {
            mp3PlayerListeners.get(i).beforePlaying(this);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      if (state == STATE_STOP) {
        playMusic(playinMusic);
        return;
      }
      this.mediaPlayer.start();
      state = STATE_PLAYING;
    }
  }

  /**
   * 暂停
   */
  public void pause() {
    if (state == STATE_PLAYING) {
      this.autoPlay = true;
      this.mediaPlayer.pause();
      state = STATE_PAUSE;
      if (mp3PlayerListeners != null) {
        for (int i = 0; i < mp3PlayerListeners.size(); i++) {
          mp3PlayerListeners.get(i).onPause(this);
        }
      }
    }
  }

  public boolean isPause() {
    return state == STATE_PAUSE;
  }

  public boolean isStop() {
    return state == STATE_STOP;
  }

  /**
   * 移动到某个位置播放
   *
   * @param position 要移动到的位置
   */
  public void seekTo(long position) {
    if (state == STATE_PLAYING || state == STATE_PAUSE || state == STATE_STOP) {
      this.autoPlay = true;
      this.mediaPlayer.seekTo((int) position);
    }
  }

  /**
   * 停止播放
   */
  public void stop() {
    if (state == STATE_PLAYING || state == STATE_PAUSE || STATE_STOP == state) {
      this.autoPlay = true;
      this.mediaPlayer.stop();
    }
    this.state = STATE_STOP;
    if (mp3PlayerListeners != null) {
      for (int i = 0; i < mp3PlayerListeners.size(); i++) {
        mp3PlayerListeners.get(i).afterStop(this, true);
      }
    }
  }

  public void empty() {
    mediaPlayer.stop();
    mediaPlayer.reset();
    playinMusic = null;
    state = STATE_NEVERPLAYING;
    if (mp3PlayerListeners != null) {
      for (int i = 0; i < mp3PlayerListeners.size(); i++) {
        mp3PlayerListeners.get(i).onEmpty(this);
      }
    }
  }

  public long getCurrentPosition() {
    return this.mediaPlayer.getCurrentPosition();
  }

  /**
   * 是否正在播放
   *
   * @return true正在播放, 否则未播放
   */
  public boolean isPlaying() {
    return state == STATE_PLAYING;
  }

  public int getState() {
    return state;
  }

  // 一首歌曲播放完毕的时候回调
  @Override
  public void onCompletion(MediaPlayer mp) {
    this.state = STATE_STOP;
    if (mp3PlayerListeners != null) {
      for (int i = 0; i < mp3PlayerListeners.size(); i++) {
        mp3PlayerListeners.get(i).afterStop(this, false);
      }
    }
  }

  // 资源准备完毕的时候回调
  @Override
  public void onPrepared(MediaPlayer mp) {
    // Log.i("Mp3Player", "准备完成, 是否自动播放:" + autoPlay);
    if (autoPlay) {
      if (mp3PlayerListeners != null) {
        for (int i = 0; i < mp3PlayerListeners.size(); i++) {
          mp3PlayerListeners.get(i).beforePlaying(this);
        }
      }
      this.mediaPlayer.start();
      this.state = STATE_PLAYING;
    } else {
      this.state = STATE_PAUSE;
    }
  }

  // 播放出错的时候回调
  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    // 尝试再次播放
    playMusic(this.playinMusic);
    return true;
  }

  public void addMp3PlayListener(Mp3PlayerListener mp3PlayerListener) {
    if (mp3PlayerListeners == null) {
      mp3PlayerListeners = new ArrayList<>();
    }
    if (mp3PlayerListener != null) {
      if (!mp3PlayerListeners.contains(mp3PlayerListener))
        mp3PlayerListeners.add(mp3PlayerListener);
    }
  }

  public void removeMp3PlayListener(Mp3PlayerListener mp3PlayerListener) {
    if (mp3PlayerListeners != null) {
      mp3PlayerListeners.remove(mp3PlayerListener);
    }
  }

  private ArrayList<Mp3PlayerListener> mp3PlayerListeners;

  public static interface Mp3PlayerListener {
    void beforePlaying(Mp3Player mp3Player);

    void onPause(Mp3Player mp3Player);

    void afterStop(Mp3Player mp3Player, boolean fromuser);

    void onEmpty(Mp3Player mp3Player);
  }
}
