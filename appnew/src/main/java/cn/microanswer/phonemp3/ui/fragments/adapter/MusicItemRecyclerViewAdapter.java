package cn.microanswer.phonemp3.ui.fragments.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.util.Task;

public class MusicItemRecyclerViewAdapter extends RecyclerView.Adapter<MusicItemRecyclerViewAdapter.MusicItem> {
    public static final RequestOptions REQUEST_OPTIONS = new RequestOptions()
            .placeholder(R.drawable.icon_ablem)
            .error(R.drawable.icon_ablem)
            .fitCenter();

    private List<Music> musicList;
    private LayoutInflater layoutInflater;

    public MusicItemRecyclerViewAdapter() {
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        this.notifyDataSetChanged();
    }

    public void addMusic(Music music) {
        if (this.musicList == null) {
            this.musicList = new ArrayList<>();
        }

        // 已经有这条数据，就不用再添加了
        if (this.musicList.contains(music)) {
            return;
        }

        this.musicList.add(music);
        this.notifyItemInserted(this.musicList.size() - 1);
    }

    public Music remove(int index) {
        if (this.musicList == null) {
            return null;
        }
        Music remove = this.musicList.remove(index);

        this.notifyItemRemoved(index);
        return remove;
    }

    public void move(int from, int to) {

        Music remove = this.musicList.remove(from);
        this.musicList.add(to, remove);

        // this.notifyItemMoved(from, to);
        this.notifyDataSetChanged();
    }

    public void addMusic(int i, Music object) {
        if (this.musicList == null) {
            this.musicList = new ArrayList<>();
        }
        this.musicList.add(i, object);
        this.notifyItemInserted(i);
    }

    public void addMusics(List<Music> musics) {
        if (this.musicList == null) {
            this.setMusicList(musics);
        } else {
            int oldSize = this.musicList.size();
            this.musicList.addAll(musics);
            this.notifyItemRangeInserted(oldSize, musics.size());
        }
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    @NonNull
    @Override
    public MusicItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new MusicItem(layoutInflater.inflate(R.layout.view_music_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItem musicItem, int i) {
        musicItem.bind(i, musicList.get(i));
    }

    @Override
    public int getItemCount() {
        return this.musicList == null ? 0 : this.musicList.size();
    }

    public void addOrMoveMusic(Music object) {
        if (musicList == null) {
            this.addMusic(object);
        } else {
            int i = musicList.indexOf(object);
            if (i == -1) {
                this.addMusic(0, object);
            } else {
                move(i, 0);
            }
        }
    }

    public void remove(Music value) {

        if (musicList == null) {
            return;
        }

        int i = musicList.indexOf(value);
        if (i > -1) {
            remove(i);
        }
    }


    public class MusicItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Music music;
        private int position;

        private ImageView imageviewMusicImg;
        private ImageView mMusicItemMenu;
        private TextView textviewMusicTitle;
        private TextView textviewMusicMore;


        public MusicItem(View itemView) {
            super(itemView);

            imageviewMusicImg = itemView.findViewById(R.id.imageviewMusicImg);
            textviewMusicMore = itemView.findViewById(R.id.textviewMusicMore);
            textviewMusicTitle = itemView.findViewById(R.id.textviewMusicTitle);
            mMusicItemMenu = itemView.findViewById(R.id.musicItemMenu);

            itemView.setClickable(true);
            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            mMusicItemMenu.setOnClickListener(this);
        }

        public void bind(int position, Music music) {
            this.music = music;
            this.position = position;

            textviewMusicTitle.setText(music.getTitle());
            textviewMusicMore.setText(String.format("%s - %s", music.getArtist(), music.getAlbum()));

            Glide
                    .with(imageviewMusicImg.getContext())
                    .load(music.getCoverPath())
                    .apply(REQUEST_OPTIONS)
                    .into(imageviewMusicImg);

        }

        @Override
        public void onClick(View v) {

            // 将这首歌曲的【isNew】标志取消
            if (music != null && "yes".equals(music.getIsNew())) {
                Task.TaskHelper.getInstance().run(new Task.ITask<Music, Void>() {
                    @Override
                    public Music getParam() {
                        return music;
                    }

                    @Override
                    public Void run(Music param) throws Exception {
                        param.setIsNew("");
                        param.update();
                        return null;
                    }
                });
            }
            if (v == mMusicItemMenu) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemMenuClick(v, music, position);
                }
                return;
            }

            if (getOnItemClickListener() != null) {
                getOnItemClickListener().onItemClick(this.itemView, music, position);
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View v, Music music, int position);

        void onItemMenuClick(View v, Music music, int position);
    }
}
