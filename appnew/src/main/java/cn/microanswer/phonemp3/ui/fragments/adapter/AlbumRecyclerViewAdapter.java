package cn.microanswer.phonemp3.ui.fragments.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Ablum;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumItemHolder>{
    private LayoutInflater layoutInflater;

    private List<Ablum> ablumList;

    public AlbumRecyclerViewAdapter () {
        ablumList = new ArrayList<>();
    }

    public List<Ablum> getAblumList() {
        return ablumList;
    }

    public void setAblumList(List<Ablum> ablumList) {
        if (ablumList != null) {
            this.ablumList.addAll(ablumList);
            notifyDataSetChanged();
        }
    }

    public void addAblum(Ablum ablum) {
        if (!ablumList.contains(ablum)) {
            ablumList.add(ablum);
            notifyItemInserted(ablumList.size());
        }
    }

    @NonNull
    @Override
    public AlbumItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new AlbumItemHolder(layoutInflater.inflate(R.layout.fragment_main_allmusic_a_albumitem, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumItemHolder holder, int position) {
        holder.bind(position, ablumList.get(position));
    }

    @Override
    public int getItemCount() {
        return ablumList.size();
    }

    public static class AlbumItemHolder extends RecyclerView.ViewHolder {
        private Ablum ablum;
        private int position;

        private ImageView image;
        private TextView titleView, descView;

        public AlbumItemHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            titleView = itemView.findViewById(R.id.title);
            descView = itemView.findViewById(R.id.desc);
        }

        void bind(int position, Ablum ablum) {
            this.position = position;
            this.ablum = ablum;

            image.setImageResource(R.drawable.icon_ablem);
            if (!TextUtils.isEmpty(this.ablum.getAlbum_art())) {
                RequestOptions opt = new RequestOptions().error(R.drawable.icon_ablem);
                Glide.with(itemView).load(this.ablum.getAlbum_art()).apply(opt).into(image);
            }
            titleView.setText(this.ablum.getAlbum());
            descView.setText(this.ablum.getArtist());
        }
    }
}
