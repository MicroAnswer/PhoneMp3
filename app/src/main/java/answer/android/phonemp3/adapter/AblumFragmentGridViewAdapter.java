package answer.android.phonemp3.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import answer.android.phonemp3.R;
import answer.android.phonemp3.bean.Ablum;

/**
 * 专辑界面适配器
 * Created by Micro on 2017/7/2.
 */

public class AblumFragmentGridViewAdapter extends RecyclerView.Adapter<AblumFragmentGridViewAdapter.AblumItemViewHolder> {

  private ArrayList<Ablum> ablums;
  private LayoutInflater inflater;

  public AblumFragmentGridViewAdapter() {
    ablums = new ArrayList<>();
  }

  @Override
  public AblumItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (inflater == null) {
      inflater = LayoutInflater.from(parent.getContext());
    }
    return new AblumItemViewHolder(inflater.inflate(R.layout.view_ablumitem, null));
  }

  @Override
  public void onBindViewHolder(AblumItemViewHolder viewHolder, int position) {
    viewHolder.bind(viewHolder, position);
  }

  public void setAblums(ArrayList<Ablum> ablums) {
    if (null == ablums) {
      this.ablums.clear();
      notifyDataSetChanged();
      return;
    }
    this.ablums.clear();
    this.ablums.addAll(ablums);
    notifyDataSetChanged();
  }

  public ArrayList<Ablum> getAblums() {
    return ablums;
  }

  @Override
  public long getItemId(int position) {
    return -1;
  }

  @Override
  public int getItemCount() {
    return ablums == null ? 0 : ablums.size();
  }

  class AblumItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView ablum, count, art;
    private ImageView imageView;
    private RelativeLayout view_ablumitem_info_vontent_backgd;
    private CardView cardView;
    private int position;

    private Ablum clickablum;

    public AblumItemViewHolder(View convertView) {
      super(convertView);
      ablum = (TextView) convertView.findViewById(R.id.view_ablumitem_ablum);
      count = (TextView) convertView.findViewById(R.id.view_ablumitem_count);
      art = (TextView) convertView.findViewById(R.id.view_ablumitem_art);
      imageView = (ImageView) convertView.findViewById(R.id.view_ablumitem_ablum_art);
      cardView = (CardView) convertView.findViewById(R.id.cardview);
      view_ablumitem_info_vontent_backgd = (RelativeLayout) convertView.findViewById(R.id.view_ablumitem_info_vontent_backgd);
      cardView.setOnClickListener(this);
    }

    public void bind(AblumItemViewHolder viewHolder, int position) {
      Ablum ablum = ablums.get(position);
      clickablum = ablum;
      this.position = position;
      if (!TextUtils.isEmpty(ablum.getAlbum_art())) {
        // x.image().bind(viewHolder.imageView, ablum.getAlbum_art(), imageOptions);
        Glide.with(viewHolder.ablum.getContext().getApplicationContext())
                .load(ablum.getAlbum_art())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.picload)
                .error(R.drawable.defaablum)
                .into(viewHolder.imageView);
      } else {
        viewHolder.imageView.setImageResource(R.drawable.picload);
      }

      viewHolder.count.setText(String.format(viewHolder.ablum.getResources().getString(R.string._musiccount), ablum.getNumsongs()));
      viewHolder.ablum.setText(ablum.getAlbum());
      viewHolder.art.setText(ablum.getArtist());
    }

    @Override
    public void onClick(View v) {
      if (onItemClick != null) {
        onItemClick.click(clickablum, position, imageView);
      }
    }
  }

  private OnItemClick onItemClick;

  public void setOnItemClick(OnItemClick onItemClick) {
    this.onItemClick = onItemClick;
  }

  public OnItemClick getOnItemClick() {
    return onItemClick;
  }

  public interface OnItemClick {
    void click(Ablum ablum, int position, ImageView img);
  }

}
