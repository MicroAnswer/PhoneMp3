package answer.android.phonemp3.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.BaseActivity;
import answer.android.phonemp3.activity.MainActivity;
import answer.android.phonemp3.adapter.AllMusicListAdapter;
import answer.android.phonemp3.bean.Music;
import answer.android.phonemp3.bean.PlayList;
import answer.android.phonemp3.db.DataBaseManager;
import answer.android.phonemp3.dialog.OneMusicInfoDialog;
import answer.android.phonemp3.interfaces.BroadListener;
import answer.android.phonemp3.tool.Tool;
import answer.android.phonemp3.view.LetterView;

/**
 * 历史播放记录界面
 * Created by Micro on 2017-8-19.
 */

public class PlayhistoryFragment extends BaseFragment implements BroadListener, AllMusicListAdapter.OnItemMenuClickListener {
  public static int MaxHistoryCount = 100;
  public static final String HISTORY$ID = "history";

  private DataBaseManager dataBaseManager;

  @Override
  public void onMainActivityCreate(MainActivity mainActivity) {
    super.onMainActivityCreate(mainActivity);
    dataBaseManager = mainActivity.getDataBaseManager();
    if (!mainActivity.hasBroadListener(this)) {
      mainActivity.addBroadListener(this);
    }
  }

  private RecyclerView recyclerView;
  private AllMusicListAdapter adapter;

  private static List<Music> historyMusics;

  public PlayhistoryFragment() {
    setName("播放记录");
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (null == getFragmentView()) {
      setFragmentView(inflater.inflate(R.layout.fragment_playhistory, container, false));
    }
    return getFragmentView();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.recyclerView = (RecyclerView) findViewById(R.id.fragment_history_recyclerview);

    adapter = new AllMusicListAdapter(getBaseActivity());
    adapter.setOnItemMenuClickListener(this);
    adapter.setDataBaseManager(dataBaseManager);

    this.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false));
    this.recyclerView.setAdapter(adapter);

    perpLoadData();
  }

  private void perpLoadData() {
    if (historyMusics == null || historyMusics.size() < 1) {
      loadData();
    } else {
      loadDataSuccess();
    }
  }

  private void loadDataSuccess() {
    if (historyMusics == null || historyMusics.size() < 1) {
      showEmptyView();
    } else {
      hideLoad_EmptyView();
      adapter.setMusicList(historyMusics);
    }
  }

  private void loadData() {
    showLoadingView();
    x.task().run(dataLoader);
  }


  private Runnable dataLoader = new Runnable() {
    @Override
    public void run() {
      PlayList playList = dataBaseManager.getPlayEasyList(HISTORY$ID);
      if (playList == null) {
        // 还没有历史记录播放列表,添加..
        dataBaseManager.addPlayList(HISTORY$ID, "历史播放记录");
      }

      playList = dataBaseManager.getPlayList(HISTORY$ID);

      if (playList != null && playList.getMusics() != null && playList.getMusics().size() >= 1) {
        historyMusics = playList.getMusics();
        Collections.sort(historyMusics, new Comparator<Music>() {
          @Override
          public int compare(Music o1, Music o2) {
            return -o1.getUpdateAt().compareTo(o2.getUpdateAt());
          }
        });
      }
      x.task().post(dataLoadSuccess);
    }
  };

  private Runnable dataLoadSuccess = new Runnable() {
    @Override
    public void run() {
      loadDataSuccess();
    }
  };


  @Override
  public void onItemMenuClick(View v, final Music music) {
    PopupMenu popupMenu = new PopupMenu(getActivity(), v);
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
          getBaseActivity().confirm(getString(R.string.deletetip), new BaseActivity.Click() {
            @Override
            public void d0() {
              // 确定删除
              File f = new File(music.get_data());
              if (f.exists()) {
                if (f.delete()) {
                  getBaseActivity().toast("删除成功", BaseActivity.DUR_SHORT);

                  Intent innn = new Intent(ACTION.DELETE);
                  innn.putExtra("music", music);
                  innn.putExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC, music);
                  getBaseActivity().sendBroadcast(innn);

                  // 从数据库全部移除这首歌
                  getBaseActivity().getDataBaseManager().removeMusic(music.get_id());

                  MainActivity mainActivity = (MainActivity) getBaseActivity();
                  historyMusics.remove(music);
                  mainActivity.broadRemoveMusic(music);
                  adapter.remove(music);
                }
              }
            }
          });
        } else if (id == R.id.dialog_musicitemmenu_play) {
          // 播放
          getBaseActivity().broadPlay(music);
        } else if (id == R.id.dialog_musicitemmenu_playnext) {
          // 下一曲播放
          getBaseActivity().broadNextPlay(music);
          getBaseActivity().toast(getString(R.string.nextwillplay) + music.getTitle(), BaseActivity.DUR_SHORT);
        } else if (id == R.id.dialog_musicitemmenu_info) {
          // 信息
          new OneMusicInfoDialog(getContext(), music).show();
        } else if (id == R.id.dialog_musicitemmenu_share) {
          // 分享
          Tool.share(getContext(), new File(music.get_data()));
        } else if (id == R.id.dialog_musicitemmenu_love) {
          // 收藏
          Intent intent = new Intent();
          intent.putExtra("music", music);
          intent.setAction(ACTION.LOVE_CHANGE);
          if (music.isLoved) {
            // 已经是收藏的歌曲,进行取消收藏
            getBaseActivity().getDataBaseManager().removeMusicFromPlayList(LovesFragment.LOVELISTID, music);
            intent.putExtra("love", false);
          } else {
            // 没有收藏的歌曲,进行收藏
            getBaseActivity().getDataBaseManager().addMusicToPlayList(LovesFragment.LOVELISTID, music);
            intent.putExtra("love", true);
            getBaseActivity().tip(getString(R.string.loveok));
          }
          getContext().sendBroadcast(intent);
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
// 播放全部点击了,
    if (music == null) {
      getBaseActivity().broadPlay(historyMusics, "--");
    } else {
      getBaseActivity().broadPlay(music);
    }
  }

  @Override
  public void onRefreshAll() {
    loadData();
  }

  @Override
  public void onGetBroad(Intent intent) {
    if (ACTION.DELETE.equals(intent.getAction())) {
      // 有文件被删除，
      if (historyMusics == null || historyMusics.size() < 1) {
        return; // 根本没有数据，啥都不用做了
      }
      Music music = intent.getParcelableExtra("music");
      if (music == null) {
        music = intent.getParcelableExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC);
      }
      music.setUpdateAt(String.valueOf(System.currentTimeMillis()));
      int i = historyMusics.indexOf(music);
      if (i > -1) {
        historyMusics.remove(music);
        if (adapter != null) {
          adapter.remove(music);
          adapter.notifyItemRemoved(i);
        }
      }
    } else if (ACTION.BEFOR_PLAY.equals(intent.getAction())) {
      // 每一首歌曲播放之前发送这个广播，我这儿获取到，并写入历史纪录
      Music music = intent.getParcelableExtra("music");
      if (music == null) {
        music = intent.getParcelableExtra(ACTION.ASK.CONTROL.EXTRAS_MUSIC);
      }
      if (historyMusics == null) {
        return; // 没有数据时，收到广播也不处理
      }

      if (historyMusics.contains(music)) {
        historyMusics.remove(music);
        historyMusics.add(0, music); // 把这首歌曲移动到最前面
      } else {
        historyMusics.add(0, music);
      }

      if (historyMusics.size() > MaxHistoryCount) {
        historyMusics.remove(historyMusics.size() - 1);
      }

      if (adapter != null) {
        adapter.setMusicList(historyMusics);
      }
    } else if (ACTION.LOVE_CHANGE.equals(intent.getAction())) {
      // boolean love = intent.getBooleanExtra("love", false);
      Music music = intent.getParcelableExtra("music");
      if (historyMusics != null && historyMusics.size() > 0) {
        int i = historyMusics.indexOf(music);
        if (adapter != null) {
          adapter.notifyItemChanged(i + 1);
        }
      }
//      allMusicListAdapter.notifyDataSetChanged();
    }
  }

}
