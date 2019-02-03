package answer.android.phonemp3.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.db.DataBaseManager;
import answer.android.phonemp3.fragment.LovesFragment;

/**
 * 所有歌曲的listview的适配器
 * Created by Microanswer on 2017/6/15.
 */

public class AllMusicListAdapter extends RecyclerView.Adapter {

  private List<Music> musicList;
  private DataBaseManager dataBaseManager;
  private LayoutInflater inflater;

  public AllMusicListAdapter(Context context) {
    inflater = LayoutInflater.from(context);
  }

  public void setDataBaseManager(DataBaseManager dataBaseManager) {
    this.dataBaseManager = dataBaseManager;
  }

  public void setMusicList(List<Music> musicList) {
    this.musicList = new ArrayList<>();
    this.musicList.addAll(musicList);
    notifyDataSetChanged();
  }

  public List<Music> getMusicList() {
    return musicList;
  }

  public void addMusic(Music m) {
    if (musicList == null) {
      musicList = new ArrayList<>();
    }
    musicList.add(m);
    notifyDataSetChanged();
  }

  public void addAll(List<Music> musics) {
    if (musicList == null) {
      this.musicList = new ArrayList<>();
    }
    musicList.addAll(musics);
    notifyDataSetChanged();
  }

  public void remove(Music music) {
    musicList.remove(music);
    notifyDataSetChanged();
  }

  @Override
  public int getItemViewType(int position) {
    if (0 == position) {
      return R.layout.item_playall;
    } else {
      return R.layout.item_allmusic;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    RecyclerView.ViewHolder holder;

    if (R.layout.item_playall == viewType) {
      // 创建播放所有的Holder
      holder = new PlayAllMusicViewHolder(inflater.inflate(viewType, parent, false));
    } else {
      // 音乐条目
      holder = new AllMusicItemViewHolder(inflater.inflate(viewType, parent, false));
    }

    return holder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof PlayAllMusicViewHolder) {
      PlayAllMusicViewHolder playAllMusicViewHolder = (PlayAllMusicViewHolder) holder;
      playAllMusicViewHolder.bind(getItemCount() - 1);
    } else {
      AllMusicItemViewHolder allMusicItemViewHolder = (AllMusicItemViewHolder) holder;
      Music music = musicList.get(position - 1);
      allMusicItemViewHolder.bind(music, position);
    }
  }

  @Override
  public int getItemCount() {
    if (musicList == null) {
      return 0;
    } else {
      if (musicList.size() > 0) {
        return musicList.size() + 1;
      } else {
        return 0;
      }
    }
  }

  private class PlayAllMusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView textView, ref;
    private CardView cardView;

    public PlayAllMusicViewHolder(View itemView) {
      super(itemView);
      textView = ((TextView) itemView.findViewById(R.id.item_playall_musiccount));
      cardView = (CardView) itemView.findViewById(R.id.cardview);
      ref = (TextView) itemView.findViewById(R.id.item_allmusic_refresh);
    }

    void bind(final int allMusicCount) {
      String format = String.format(textView.getResources().getString(R.string.allmusiccount), allMusicCount);
      textView.setText(format);
      cardView.setOnClickListener(this);
      ref.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (onItemMenuClickListener != null) {
        if (v == ref) {
          onItemMenuClickListener.onRefreshAll();
          return;
        }
        onItemMenuClickListener.onItemClick(v, null);
      }
    }
  }

  private class AllMusicItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView itemMenu;
    private TextView title, subtitle, index;
    private LinearLayout subcontext;
    private CardView cardView;
    private int postion;
    private Music music;

    public AllMusicItemViewHolder(View itemView) {
      super(itemView);
      itemMenu = (ImageView) itemView.findViewById(R.id.item_allmusic_menu);
      subcontext = (LinearLayout) itemView.findViewById(R.id.item_allmusic_subcontent);
      subtitle = (TextView) itemView.findViewById(R.id.item_allmusic_subtitle);
      index = (TextView) itemView.findViewById(R.id.item_allmusic_index);
      title = (TextView) itemView.findViewById(R.id.item_allmusic_title);
      cardView = (CardView) itemView.findViewById(R.id.cardview);
    }


    void bind(final Music music, final int position) {
      this.music = music;
      this.postion = position;
      cardView.setOnClickListener(this);
      if (music.isLoved) {
        subcontext.getChildAt(0).setVisibility(View.VISIBLE);
      } else {
        subcontext.getChildAt(0).setVisibility(View.GONE);
      }
      if (dataBaseManager != null) {
        subcontext.setTag(music.get_id());
        x.task().run(new Runnable() {
          @Override
          public void run() {
            final Music music1 = music;
            final String id = music.get_id();
            boolean inPlayList = dataBaseManager.isInPlayList(music, LovesFragment.LOVELISTID);
            music1.isLoved = inPlayList;
            if (inPlayList) {
              x.task().post(new Runnable() {
                @Override
                public void run() {
                  if (id.equals(subcontext.getTag()))
                    subcontext.getChildAt(0).setVisibility(View.VISIBLE);
                }
              });
            }
          }
        });
      }
      index.setText(String.valueOf(position));
      itemMenu.setOnClickListener(this);
      title.setText(String.valueOf(music.getTitle()));
      subtitle.setText(String.valueOf(music.getArtist() + "-" + music.getAlbum()));
    }

    @Override
    public void onClick(View v) {
      if (v == itemMenu) {
        if (onItemMenuClickListener != null) {
          onItemMenuClickListener.onItemMenuClick(v, music);
        }
        return;
      }
      if (onItemMenuClickListener != null) {
        onItemMenuClickListener.onItemClick(v, music);
      }
    }
  }

  private OnItemMenuClickListener onItemMenuClickListener;

  public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
    this.onItemMenuClickListener = onItemMenuClickListener;
  }

  public OnItemMenuClickListener getOnItemMenuClickListener() {
    return onItemMenuClickListener;
  }

  public interface OnItemMenuClickListener {
    void onItemMenuClick(View v, Music music);

    void onItemClick(View v, Music music);

    void onRefreshAll();
  }

}
