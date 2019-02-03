package answer.android.phonemp3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.xutils.x;

import java.net.URLEncoder;
import java.util.ArrayList;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Artist;

/**
 * 歌手界面适配器
 * Created by Microanswer on 2017/7/5.
 */

public class ArtistAdapter2 extends RecyclerView.Adapter<ArtistAdapter2.ArtistItemViewHolder> implements View.OnClickListener {

    private ArrayList<Artist> artists;

    public ArtistAdapter2() {
        artists = new ArrayList<>();
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists.clear();
        this.artists.addAll(artists);
        this.notifyDataSetChanged();
    }

    @Override
    public ArtistItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArtistItemViewHolder(buildItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final ArtistItemViewHolder holder, int position) {
        final Artist artist = artists.get(position);
        holder.itemView.setTag(artist);
        holder.itemView.setOnClickListener(this);

        holder.getTextView().setText(artist.getArtist());
        try {
            // Glide.with(holder.itemView.getContext().getApplicationContext())
            //         .load("http://49.4.144.239/api/phonemp3.php?method=getArtImg&artist=" + URLEncoder.encode(artist.getArtist(), "UTF-8"))
            //         .placeholder(R.drawable.picload)
            //         .error(R.drawable.picload)
            //         .into(holder.getImageview());
            x.image().bind(holder.getImageview(), "http://49.4.144.239/api/phonemp3.php?method=getArtImg&artist=" + URLEncoder.encode(artist.getArtist(), "UTF-8"));
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public void onClick(View v) {
        if (null != onItemClickListener) {
            onItemClickListener.onClick((Artist) v.getTag());
        }
    }

    public static class ArtistItemViewHolder extends RecyclerView.ViewHolder {

        public ArtistItemViewHolder(View itemView) {
            super(itemView);
        }

        private ImageView getImageview() {
            return (ImageView) ((LinearLayout) itemView).getChildAt(0);
        }


        private TextView getTextView() {
            return (TextView) ((LinearLayout) itemView).getChildAt(1);
        }

    }

    private static View buildItemView(Context context) {
        return View.inflate(context, R.layout.view_artistitem2, null);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onClick(Artist artist);
    }
}
