package answer.android.phonemp3.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.AblumActivity;
import answer.android.phonemp3.bean.Ablum;
import answer.android.phonemp3.bean.Artist;
import answer.android.phonemp3.tool.Tool;

/**
 * 展示歌手的专辑弹出框
 * Created by Microanswer on 2017/7/5.
 */

public class ArtistAblumsDialog extends Dialog implements View.OnClickListener, Runnable, AdapterView.OnItemClickListener {
  private Artist artist;
  private AblumListAdapter adapter;
  private ListView listView;
  private TextView title;
  private ArrayList<Ablum> ablums;

  public ArtistAblumsDialog(Context context, Artist artist) {
    super(context);
    this.artist = artist;
    adapter = new AblumListAdapter();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_artistablum);
    findViewById(R.id.dialog_artistablum_close).setOnClickListener(this);
    listView = (ListView) findViewById(R.id.dialog_artistablum_list);
    listView.setEmptyView(findViewById(R.id.emptyview));
    listView.setOnItemClickListener(this);
    listView.setAdapter(adapter);
    title = (TextView) findViewById(R.id.dialog_artistablum_title);
    title.setText(String.format(getContext().getResources().getString(R.string.artistablumhas), artist.getArtist(), artist.getNumber_of_albums()));
    setCancelable(true);
    setCanceledOnTouchOutside(true);
    x.task().run(this);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.dialog_artistablum_close) {
      dismiss();
    } else {
    }
  }

  @Override
  public void run() {
    ablums = new ArrayList<>();
    // 加载数据
    Cursor query = getContext().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, "artist_id=?", new String[]{artist.get_id()}, null);
    if (query != null && query.getCount() > 0 && query.moveToFirst()) {
      do {
        Ablum ablum = new Ablum();
        Field[] declaredFields = Ablum.class.getDeclaredFields();
        for (Field f : declaredFields) {
          f.setAccessible(true);
          int columnIndex = query.getColumnIndex(f.getName());
          if (columnIndex > -1) {
            try {
              f.set(ablum, query.getString(columnIndex));
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
        ablum.setLetter(Tool.getPinYin(ablum.getAlbum()));
        ablums.add(ablum);
      } while (query.moveToNext());
    }

    Collections.sort(ablums);

    x.task().post(new Runnable() {
      @Override
      public void run() {
        adapter.notifyDataSetChanged();
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Ablum ablum = ablums.get(position);
    Intent intent = new Intent(getContext(), AblumActivity.class);
    intent.putExtra("ablum", ablum);
    getContext().startActivity(intent);
  }

  private class AblumListAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return ablums == null ? 0 : ablums.size();
    }

    @Override
    public Ablum getItem(int position) {
      return ablums == null ? null : ablums.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ArtistAblumItem artistAblumItem;
      if (convertView == null) {
        artistAblumItem = new ArtistAblumItem();
        convertView = View.inflate(getContext(), R.layout.view_artistablumitem, null);
        artistAblumItem.img = (ImageView) convertView.findViewById(R.id.view_artistablum_img);
        artistAblumItem.title = (TextView) convertView.findViewById(R.id.view_artistablum_title);
        artistAblumItem.subtitle = (TextView) convertView.findViewById(R.id.view_artistablum_subtitle);
        convertView.setTag(artistAblumItem);
      } else {
        artistAblumItem = (ArtistAblumItem) convertView.getTag();
      }

      Ablum ablum = getItem(position);

      if (TextUtils.isEmpty(ablum.getAlbum_art()) || "null".equals(ablum.getAlbum_art())) {
        artistAblumItem.img.setImageResource(R.mipmap.ic_launcher);
      } else {
        x.image().bind(artistAblumItem.img, ablum.getAlbum_art());
      }

      artistAblumItem.subtitle.setText(ablum.getArtist());
      artistAblumItem.subtitle.append("-" + String.format(getContext().getResources().getString(R.string.allmusiccount), Integer.parseInt(ablum.getNumsongs())));
      artistAblumItem.title.setText(ablum.getAlbum());

      return convertView;
    }

    private class ArtistAblumItem {
      private ImageView img;
      private TextView title, subtitle;
    }

  }
}
