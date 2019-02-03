package answer.android.phonemp3.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Artist;

/**
 * 歌手界面适配器
 * Created by Microanswer on 2017/7/5.
 */
@Deprecated
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistItemViewHolder> implements View.OnClickListener {

  private ArrayList<Artist> artists;

  public ArtistAdapter() {
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
  public void onBindViewHolder(ArtistItemViewHolder holder, int position) {
    Artist artist = artists.get(position);
    holder.itemView.setTag(artist);
    holder.itemView.setOnClickListener(this);

    holder.getTextView().setText(artist.getArtist());

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
    return View.inflate(context, R.layout.view_artistitem, null);
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
