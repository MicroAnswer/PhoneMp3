package cn.microanswer.phonemp3.ui.fragments.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.PlayList;

public class MyPlayListRecyclerViewAdapter extends RecyclerView.Adapter<MyPlayListRecyclerViewAdapter.PlayListItem> {

    private List<PlayList> playLists;
    private LayoutInflater layoutInflater;
    private onMyPlayListItemClick onMyPlayListItemClick;

    public void setPlayLists(List<PlayList> playLists) {
        if (this.playLists == null) {
            this.playLists = new ArrayList<>();
        }
        this.playLists.clear();

        this.playLists.addAll(playLists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayListItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new PlayListItem(layoutInflater.inflate(R.layout.view_playlist_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListItem holder, int position) {
        holder.bind(position, playLists.get(position));
    }

    @Override
    public int getItemCount() {
        return playLists == null ? 0 : playLists.size();
    }

    public void add(PlayList playList) {
        if (this.playLists == null) {
            this.playLists = new ArrayList<>();
        }
        int size = playLists.size();
        playLists.add(playList);
        notifyItemInserted(size);
    }

    public void setOnMyPlayListItemClick(MyPlayListRecyclerViewAdapter.onMyPlayListItemClick onMyPlayListItemClick) {
        this.onMyPlayListItemClick = onMyPlayListItemClick;
    }

    public void remove(int position) {
        if (playLists != null && playLists.size() > position) {
            playLists.remove(position);
            notifyItemRemoved(position);
        }
    }

    public class PlayListItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name;
        private ImageView imageView;

        private int position;
        private PlayList playList;

        public PlayListItem(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        private void bind(int position, PlayList playList) {
            this.playList = playList;
            this.position = position;
            name.setText(playList.getRamark());
        }

        @Override
        public void onClick(View v) {
            if (onMyPlayListItemClick != null) {
                onMyPlayListItemClick.onClickPlayListItem(position, playList, v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onMyPlayListItemClick != null) {
                return onMyPlayListItemClick.onLongClickPlayListItem(position, playList, v);
            }
            return true;
        }
    }

    public interface onMyPlayListItemClick {
        void onClickPlayListItem(int position, PlayList playList, View view);

        boolean onLongClickPlayListItem(int position, PlayList playList, View view);
    }
}
