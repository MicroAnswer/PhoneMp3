package answer.android.phonemp3.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.db.DataBaseManager;
import answer.android.phonemp3.fragment.LovesFragment;

/**
 * 音乐条目里面的菜单点击后弹出
 * Created by Micro on 2017/7/9.
 */
@Deprecated
public class MusicItemMenuDialog2 extends Dialog implements View.OnClickListener {
  private Music music;
  private int position[];

  private LinearLayout dialog_musicitemmenu_play;
  private LinearLayout dialog_musicitemmenu_playNext;
  private LinearLayout dialog_musicitemmenu_love;
  private LinearLayout dialog_musicitemmenu_share;
  private LinearLayout dialog_musicitemmenu_delete;
  private LinearLayout dialog_musicitemmenu_info;
  private ImageView loveIcon;
  private TextView loveText;
  private DataBaseManager dataBaseManager;
  private boolean isLoveing;

  public MusicItemMenuDialog2(@NonNull Context context, @NonNull Music music, View positionView, DataBaseManager dataBaseManager) {
    super(context);
    this.music = music;
    this.dataBaseManager = dataBaseManager;
    position = new int[2];
    positionView.getLocationInWindow(position);
    position[0] = 0;
    position[1] = position[1] - positionView.getMeasuredHeight();
    // Log.i("MusicItemMenuDialog", Arrays.toString(position));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_musicitemmenu);
    setCancelable(true);
    setCanceledOnTouchOutside(true);

    dialog_musicitemmenu_play = (LinearLayout) findViewById(R.id.dialog_musicitemmenu_play);
    dialog_musicitemmenu_playNext = (LinearLayout) findViewById(R.id.dialog_musicitemmenu_playnext);
    dialog_musicitemmenu_love = (LinearLayout) findViewById(R.id.dialog_musicitemmenu_love);
    dialog_musicitemmenu_share = (LinearLayout) findViewById(R.id.dialog_musicitemmenu_share);
    dialog_musicitemmenu_info = (LinearLayout) findViewById(R.id.dialog_musicitemmenu_info);
    dialog_musicitemmenu_delete = (LinearLayout) findViewById(R.id.dialog_musicitemmenu_delete);

    loveIcon = (ImageView) dialog_musicitemmenu_love.getChildAt(0);
    loveText = (TextView) dialog_musicitemmenu_love.getChildAt(1);

    dialog_musicitemmenu_play.setOnClickListener(this);
    dialog_musicitemmenu_playNext.setOnClickListener(this);
    dialog_musicitemmenu_love.setOnClickListener(this);
    dialog_musicitemmenu_share.setOnClickListener(this);
    dialog_musicitemmenu_info.setOnClickListener(this);
    dialog_musicitemmenu_delete.setOnClickListener(this);

    x.task().run(new Runnable() {
      @Override
      public void run() {
        //查询是否是收藏的歌曲
        isLoveing = dataBaseManager.isInPlayList(music, LovesFragment.LOVELISTID);
        // Log.i("inPlaylist", inPlayList + "");
        x.task().post(new Runnable() {
          @Override
          public void run() {
            if (isLoveing) {
              loveIcon.setImageResource(R.drawable.ic_unlove);
              loveText.setText(R.string.unlove);
            } else {
              loveIcon.setImageResource(R.drawable.ic_love);
              loveText.setText(R.string.love);
            }
          }
        });
      }
    });

  }

  @Override
  protected void onStart() {
    super.onStart();
    Window window = getWindow();
    WindowManager.LayoutParams attributes = window.getAttributes();
    attributes.width = Math.round(DensityUtil.getScreenWidth() * 0.5f);
    attributes.gravity = Gravity.RIGHT | Gravity.TOP;
    attributes.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
    attributes.x = position[0];
    attributes.y = position[1];
    // attributes.verticalMargin =0.2f;
    // attributes.format = PixelFormat.TRANSPARENT;
    // attributes.height = getHeight();
    attributes.windowAnimations = R.style.musicitemmenudialog_anim;
    window.setAttributes(attributes);
  }

  @Override
  public void onClick(View v) {
    dismiss();
    if (musicItemMenuDialogListener != null) {
      int id = v.getId();
      if (id == R.id.dialog_musicitemmenu_delete) {
        // 删除
        musicItemMenuDialogListener.onDelete(music);
      } else if (id == R.id.dialog_musicitemmenu_play) {
        // 播放
        musicItemMenuDialogListener.onPlay(music);
      } else if (id == R.id.dialog_musicitemmenu_playnext) {
        // 下一曲播放
        musicItemMenuDialogListener.onPlayNext(music);
      } else if (id == R.id.dialog_musicitemmenu_info) {
        // 信息
        musicItemMenuDialogListener.onInfo(music);
      } else if (id == R.id.dialog_musicitemmenu_share) {
        // 分享
        musicItemMenuDialogListener.onShare(music);
      } else if (id == R.id.dialog_musicitemmenu_love) {
        // 收藏
        musicItemMenuDialogListener.onLove(music, isLoveing);
      }
    }
  }

  private MusicItemMenuDialogListener musicItemMenuDialogListener;

  public MusicItemMenuDialogListener getMusicItemMenuDialogListener() {
    return musicItemMenuDialogListener;
  }

  public MusicItemMenuDialog2 setMusicItemMenuDialogListener(MusicItemMenuDialogListener musicItemMenuDialogListener) {
    this.musicItemMenuDialogListener = musicItemMenuDialogListener;
    return this;
  }

  public static interface MusicItemMenuDialogListener {
    void onDelete(Music music);

    void onPlay(Music music);

    void onPlayNext(Music music);

    void onShare(Music music);

    void onInfo(Music music);

    void onLove(Music music, boolean isLoveing);
  }
}
