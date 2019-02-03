package answer.android.phonemp3.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.view.PlayAnimationIcon;

/**
 * 正在播放列表弹出框
 * Created by Micro on 2017/6/24.
 */

public class PlayingListDialog extends BaseDailog implements View.OnClickListener {

  private RecyclerView recyclerView;
  private List<Music> musics;
  private DialogPlayingListAdapter adapter;
  private int currentPosition;

  public PlayingListDialog(Context context, List<Music> music, int currentPosition) {
    super(context);
    this.musics = music;
    this.currentPosition = currentPosition;
    adapter = new DialogPlayingListAdapter();
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_playinglist);
    findViewById(R.id.dialog_playing_close).setOnClickListener(this);
    findViewById(R.id.dialog_playing_clear).setOnClickListener(this);
    recyclerView = (RecyclerView) findViewById(R.id.dialog_playing_list);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    recyclerView.setAdapter(adapter);
  }

  public void notifyItemRemoved(int position, int size) {
    adapter.notifyItemRemoved(position);
    adapter.notifyItemRangeChanged(position, size);
  }

  private int lastposition;

  // 定位到正在播放的歌曲位置
  public void point2PlayingPosition(int position) {
    this.lastposition = this.currentPosition;
    this.currentPosition = position;
    if (adapter != null) {
      adapter.notifyItemChanged(currentPosition);
      adapter.notifyItemChanged(lastposition);
    }
    if (recyclerView != null) {
      recyclerView.scrollToPosition(position);
    }
  }

  public void show(int position) {
    point2PlayingPosition(position);
    show();
  }

  @Override
  protected void onStart() {
    super.onStart();
    point2PlayingPosition(currentPosition);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.dialog_playing_clear) {
      // 清空播放列表
      if (onItemClickListener != null) {
        onItemClickListener.onclear();
      }
    } else if (v.getId() == R.id.dialog_playing_close) {
      dismiss();
    } else if (v.getId() == R.id.view_dialog_music_item_close) {
      if (onItemClickListener != null) {
        int position = recyclerView.getChildAdapterPosition((ViewGroup) v.getParent());
        onItemClickListener.onDelete(position);
      }
    } else {
      if (onItemClickListener != null) {
        int position = recyclerView.getChildAdapterPosition(v);
        onItemClickListener.onClick(position);
      }
    }
  }

  private OnItemClickListener onItemClickListener;

  public OnItemClickListener getOnItemClickListener() {
    return onItemClickListener;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  class DialogPlayingListAdapter extends RecyclerView.Adapter<Holder> {
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new Holder(View.inflate(getContext(), R.layout.view_dialog_music_item, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
      Music m = musics.get(position);
      holder.itemView.setOnClickListener(PlayingListDialog.this);
      holder.close.setOnClickListener(PlayingListDialog.this);
      holder.index.setText(String.valueOf(position + 1));
      holder.title.setText(m.getTitle());
      if (currentPosition == position) {
        holder.playAnimationIcon.setVisibility(View.VISIBLE);
        if (!holder.playAnimationIcon.isAnimationing()) {
          holder.playAnimationIcon.start();
        }
      } else {
        holder.playAnimationIcon.setVisibility(View.GONE);
        holder.playAnimationIcon.pause();
      }

    }

    @Override
    public int getItemCount() {
      return musics.size();
    }
  }

  class Holder extends RecyclerView.ViewHolder {
    private TextView index, title;
    private ImageView close;
    private PlayAnimationIcon playAnimationIcon;

    Holder(View itemView) {
      super(itemView);
      index = (TextView) itemView.findViewById(R.id.view_dialog_music_item_index);
      title = (TextView) itemView.findViewById(R.id.view_dialog_music_item_title);
      close = (ImageView) itemView.findViewById(R.id.view_dialog_music_item_close);
      playAnimationIcon = (PlayAnimationIcon) itemView.findViewById(R.id.view_dialog_music_item_playicon);
    }
  }

  public interface OnItemClickListener {
    void onClick(int position);

    void onDelete(int position);

    void onclear();
  }


  // class DialogPlayingListAdapter extends BaseAdapter {
  //   @Override
  //   public int getCount() {
  //     return musics.size();
  //   }
//
  //   @Override
  //   public Music getItem(int position) {
  //     return musics.get(position);
  //   }
//
  //   @Override
  //   public long getItemId(int position) {
  //     return 0;
  //   }
//
  //   @Override
  //   public View getView(int position, View convertView, ViewGroup parent) {
  //     Holder holder = null;
  //     if (convertView == null) {
  //       holder = new Holder();
  //       convertView = View.inflate(getContext(), R.layout.view_dialog_music_item, null);
  //       holder.close = (ImageView) convertView.findViewById(R.id.view_dialog_music_item_close);
  //       holder.index = (TextView) convertView.findViewById(R.id.view_dialog_music_item_index);
  //       holder.title = (TextView) convertView.findViewById(R.id.view_dialog_music_item_title);
  //       convertView.setTag(holder);
  //     } else {
  //       holder = (Holder) convertView.getTag();
  //     }
//
  //     Music m = getItem(position);
//
  //     holder.title.setText(m.getTitle());
  //     holder.index.setText(String.valueOf(position + 1));
//
//
  //     return convertView;
  //   }
  // }

  // class Holder {
  //   private TextView index, title;
  //   ImageView close;
  // }
}
