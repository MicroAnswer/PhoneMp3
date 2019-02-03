package answer.android.phonemp3.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.adapter.AllMusicListAdapter;
import answer.android.phonemp3.bean.Ablum;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.dialog.OneMusicInfoDialog;
import answer.android.phonemp3.fragment.LovesFragment;
import answer.android.phonemp3.tool.Tool;

public class AblumActivity extends BaseActivity implements AllMusicListAdapter.OnItemMenuClickListener {

  private RecyclerView recyclerView;
  private ArrayList<Music> ablummusics;
  private AllMusicListAdapter adapter;
  private Ablum ablum;
  private ActionBar actionBar;
  private CollapsingToolbarLayout layout;
  private ImageView activity_ablum_img;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ablum);
    ablum = getIntent().getParcelableExtra("ablum");
    Toolbar toolBar = getToolBar();
    drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    setSupportActionBar(toolBar);
    actionBar = getSupportActionBar();
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(ablum.getAlbum());
    recyclerView = (RecyclerView) findViewById(R.id.ablumactivity_musicslist);
    if (recyclerView.getLayoutManager() == null) {
      recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
    activity_ablum_img = (ImageView) findViewById(R.id.activity_ablum_img);
    ViewCompat.setTransitionName(activity_ablum_img, ablum.getAlbum());
    adapter = new AllMusicListAdapter(this);
    adapter.setOnItemMenuClickListener(this);
    adapter.setDataBaseManager(getDataBaseManager());
    layout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
    recyclerView.setAdapter(adapter);
    Glide.with(getApplicationContext())
            .load(ablum.getAlbum_art())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.picload)
            .error(R.drawable.defaablum)
            .into(activity_ablum_img);
    layout.setTitle(ablum.getAlbum());
//    layout.setExpandedTitleColor(Color.WHITE);
    layout.setCollapsedTitleTextColor(Color.WHITE);
//    setTitle(ablum.getAlbum());
//    toolBar.setTitle(ablum.getAlbum());
    load();
  }

  private void load() {
    x.task().run(new Runnable() {
      @Override
      public void run() {
        // 加载专辑里面的歌曲:
        Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "album_id=?",
                new String[]{ablum.get_id()}, null);
        ablummusics = new ArrayList<>();
        if (query != null && query.moveToFirst()) {
          do {
            Music music = new Music();
            Field[] declaredFields = Music.class.getDeclaredFields();
            for (Field field : declaredFields) {
              int columnIndex = query.getColumnIndex(field.getName());
              if (columnIndex > -1) {
                String value = query.getString(columnIndex);
                try {
                  // 使用反射将对象的值全部填充
                  field.setAccessible(true);
                  field.set(music, value);
                } catch (Exception e) {
                  getLogger().info("值填充出错：");
                  getLogger().err(e);
                }
              }
            }
            if ("1".equals(music.getIs_music())) {
              String s = Tool.getPinYin(music.getTitle());
              music.setLetter(s);
              ablummusics.add(music);
            }
          } while (query.moveToNext());
          query.close();
          Collections.sort(ablummusics);
        }
        x.task().post(new Runnable() {
          @Override
          public void run() {
            adapter.setMusicList(ablummusics);
            if (ablummusics == null || ablummusics.size() < 1) {
              showEmptyView();
            } else {
              hideLoad_EmptyView();
            }
          }
        });
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (android.R.id.home == item.getItemId()) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onItemMenuClick(View v, final Music music) {
    PopupMenu popupMenu = new PopupMenu(AblumActivity.this, v);
    try {
      Field mpopup = PopupMenu.class.getDeclaredField("mPopup");
      mpopup.setAccessible(true);
      MenuPopupHelper mPopup = (MenuPopupHelper) mpopup.get(popupMenu);
      mPopup.setForceShowIcon(true);
    } catch (Exception e) {

    }
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dialog_musicitemmenu_delete) {
          // 删除
          confirm(getString(R.string.deletetip), new BaseActivity.Click() {
            @Override
            public void d0() {
              // 确定删除
              File f = new File(music.get_data());
              if (f.exists()) {
                if (f.delete()) {
                  toast("删除成功", BaseActivity.DUR_SHORT);

                  Intent innn = new Intent(ACTION.DELETE);
                  innn.putExtra("music", music);
                  innn.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, music);
                  sendBroadcast(innn);

                  // 从数据库全部移除这首歌
                  getDataBaseManager().removeMusic(music.get_id());

                  broadRemoveMusic(music);
                  adapter.remove(music);
                  if (adapter.getMusicList() != null && adapter.getMusicList().size() < 1) {
                    finish();
                  }
                }
              }
            }
          });
        } else if (id == R.id.dialog_musicitemmenu_play) {
          // 播放
          broadPlay(music);
        } else if (id == R.id.dialog_musicitemmenu_playnext) {
          // 下一曲播放
          broadNextPlay(music);
          toast(getString(R.string.nextwillplay) + music.getTitle(), BaseActivity.DUR_SHORT);
        } else if (id == R.id.dialog_musicitemmenu_info) {
          // 信息
          new OneMusicInfoDialog(AblumActivity.this, music).show();
        } else if (id == R.id.dialog_musicitemmenu_share) {
          // 分享
          Tool.share(AblumActivity.this, new File(music.get_data()));
        } else if (id == R.id.dialog_musicitemmenu_love) {
          // 收藏
          Intent intent = new Intent();
          intent.putExtra("music", music);
          intent.setAction(ACTION.LOVE_CHANGE);
          if (music.isLoved) {
            // 已经是收藏的歌曲,进行取消收藏
            getDataBaseManager().removeMusicFromPlayList(LovesFragment.LOVELISTID, music);
            intent.putExtra("love", false);
          } else {
            // 没有收藏的歌曲,进行收藏
            getDataBaseManager().addMusicToPlayList(LovesFragment.LOVELISTID, music);
            intent.putExtra("love", true);
            tip(getString(R.string.loveok));
          }
          sendBroadcast(intent);
          adapter.notifyItemChanged(adapter.getMusicList().indexOf(music) + 1);
//          adapter.notifyDataSetChanged();
        }
        return true;
      }
    });
    popupMenu.inflate(R.menu.music_item_menu);
    MenuItem item = popupMenu.getMenu().findItem(R.id.dialog_musicitemmenu_love);
    if (music.isLoved) {
      item.setIcon(R.drawable.ic_unlove);
      item.setTitle(R.string.unlove);
    } else {
      item.setIcon(R.drawable.ic_love);
      item.setTitle(R.string.love);
    }
    popupMenu.show();
  }

  @Override
  public void onItemClick(View v, Music music) {
    if (music == null) {
      broadPlay(ablummusics, ablum.get_id());
      go2Activity(PlayActivity.class);
      return;
    }
    broadPlay(music);
    go2Activity(PlayActivity.class);
  }

  @Override
  public void onRefreshAll() {
    load();
  }

  protected void showLoadingView() {
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.emptyview);
    linearLayout.setVisibility(View.VISIBLE);
  }

  protected void showEmptyView() {
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.emptyview);
    linearLayout.removeAllViews();
    linearLayout.addView(View.inflate(this, R.layout.view_emptyview, null));
    linearLayout.setVisibility(View.VISIBLE);
  }

  protected void hideLoad_EmptyView() {
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.emptyview);
    linearLayout.setVisibility(View.GONE);
  }
}
