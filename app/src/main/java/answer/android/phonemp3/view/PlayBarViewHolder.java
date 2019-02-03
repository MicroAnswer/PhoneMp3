package answer.android.phonemp3.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import answer.android.phonemp3.R;

/**
 * 主界面的播放面板控件引用对象
 * Created by Micro on 2017/6/16.
 */

public class PlayBarViewHolder {
  public View root;
  public ImageView playBarImg, play$pause, next;
  public TextView title, subtitle;

  public PlayBarViewHolder(View view){
    playBarImg = (ImageView) view.findViewById(R.id.activity_main_playbar_img);
    play$pause = (ImageView) view.findViewById(R.id.activity_main_playbar_play$pause);
    next = (ImageView) view.findViewById(R.id.activity_main_playbar_next);
    title = (TextView) view.findViewById(R.id.activity_main_playbar_title);
    subtitle = (TextView) view.findViewById(R.id.activity_main_playbar_subtitle);
    root = view;
  }

  public View getRoot() {
    return root;
  }

  public void setRoot(View root) {
    this.root = root;
  }

  public ImageView getPlayBarImg() {
    return playBarImg;
  }

  public void setPlayBarImg(ImageView playBarImg) {
    this.playBarImg = playBarImg;
  }

  public ImageView getPlay$pause() {
    return play$pause;
  }

  public void setPlay$pause(ImageView play$pause) {
    this.play$pause = play$pause;
  }

  public ImageView getNext() {
    return next;
  }

  public void setNext(ImageView next) {
    this.next = next;
  }

  public TextView getTitle() {
    return title;
  }

  public void setTitle(TextView title) {
    this.title = title;
  }

  public TextView getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(TextView subtitle) {
    this.subtitle = subtitle;
  }
}
