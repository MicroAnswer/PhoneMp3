package answer.android.phonemp3.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.tool.Tool;

/**
 * 单首歌曲信息弹出框展示界面
 * Created by Micro on 2017/6/25.
 */

public class OneMusicInfoDialog extends BaseDailog {

  private TextView title, ablum, dir, size, art, durtaion;
  private Music music;

  public OneMusicInfoDialog(Context context, Music music) {
    super(context);
    this.music = music;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_onmusicinfo);

    title = (TextView) findViewById(R.id.dialog_onmusicinfo_title);
    ablum = (TextView) findViewById(R.id.dialog_onmusicinfo_ablum);
    dir = (TextView) findViewById(R.id.dialog_onmusicinfo_dir);
    size = (TextView) findViewById(R.id.dialog_onmusicinfo_size);
    art = (TextView) findViewById(R.id.dialog_onmusicinfo_art);
    durtaion = (TextView) findViewById(R.id.dialog_onmusicinfo_duration);
    findViewById(R.id.dialog_playing_close).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    bindData(title, music.getTitle(), R.string.musicnameis);
    bindData(ablum, music.getAlbum(), R.string.musicalbumis);
    bindData(dir, new File(music.get_data()).getParent(), R.string.musicdiris);
    bindData(size, Tool.getNiceFileSize(Long.valueOf(music.get_size())), R.string.musicsizeis);
    bindData(art, music.getArtist(), R.string.musicartis);
    bindData(durtaion, Tool.parseTime(Long.valueOf(music.getDuration())), R.string.musicdurationis);
  }

  private void bindData(TextView textView, String value, int Stringid) {
    textView.setText(getContext().getResources().getString(Stringid, value));
  }

}
