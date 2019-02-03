package answer.android.phonemp3.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.appcompat.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

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
 * 我的收藏界面
 * Created by Micro on 2017/7/2.
 */

public class LovesFragment extends BaseFragment implements Runnable, BroadListener, AllMusicListAdapter.OnItemMenuClickListener, LetterView.OnLetterSelectListener, View.OnTouchListener {
  public static final String LOVELISTID = "loveid";

  private RecyclerView recyclerView;
  private LetterView letterView;
  private DataBaseManager dataBaseManager;
  private static PlayList loveList;
  private AllMusicListAdapter adapter;
  private int scrollState;
  private boolean lovecjange; // 标记是否收藏有变化
//  private ArrayList<Music> tempLovedMusic;

  public LovesFragment() {
    setName("我的收藏");
  }


  @Override
  public BaseFragment afterNew(BaseActivity baseActivity) {
    dataBaseManager = baseActivity.getDataBaseManager();
    adapter = new AllMusicListAdapter(baseActivity);
    adapter.setDataBaseManager(dataBaseManager);
    adapter.setOnItemMenuClickListener(this);
    return super.afterNew(baseActivity);
  }

  @Override
  public void onMainActivityCreate(MainActivity baseActivity) {
    super.onMainActivityCreate(baseActivity);
    if (!baseActivity.hasBroadListener(this)) {
      baseActivity.addBroadListener(this);
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (null == getFragmentView()) {
      setFragmentView(inflater.inflate(R.layout.fragment_lovemusic, container, false));
    }
    return getFragmentView();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    recyclerView = (RecyclerView) findViewById(R.id.fragment_lovemusic_listview);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    if (adapter == null) {
      adapter = new AllMusicListAdapter(getActivity());
      adapter.setDataBaseManager(getBaseActivity().getDataBaseManager());
      adapter.setOnItemMenuClickListener(this);
    }

    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // newState : [1 = 手指拖动, 2 = 惯性滚动, 0 = 停止滚动]
        scrollState = newState;
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
          showLeeterview();
        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          hideLeeterview();
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      }
    });
    letterView = (LetterView) findViewById(R.id.fragment_lovemusic_letterview);
    letterView.setOnLetterSelectListener(this);
    letterView.setOnTouchListener(this);

    // 隐藏拼音
    ViewCompat.setTranslationX(letterView, 0);
    ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
    animatorCompat.setDuration(300);
    animatorCompat.start();
    isLetterViewShowing = false;

    if (!getBaseActivity().hasBroadListener(this)) {
      getBaseActivity().addBroadListener(this);
    }

    preloadData();
  }

  // 预加载数据
  private void preloadData() {
    if (loveList != null && loveList.getMusics() != null && loveList.getMusics().size() > 0 && !lovecjange) {
      // 缓存有数据
      dataLoaded();
    } else {
      loadData();
    }
  }

  private void loadData() {
    showLoadingView();
    x.task().run(this);
  }

  private void dataLoaded() {
    if (loveList == null || loveList.getMusics() == null || loveList.getMusics().size() < 1) {
      // 没有歌曲数据
      showEmptyView();
      letterView.setVisibility(View.GONE);
    } else {
      hideLoad_EmptyView();

      // if (tempLovedMusic != null) {
      //   loveList.getMusics().addAll(tempLovedMusic);
      //   tempLovedMusic.clear();
      //   tempLovedMusic = null;
      // }

      adapter.setMusicList(loveList.getMusics());
      letterView.setVisibility(View.VISIBLE);
      lovecjange = false;
    }
  }

  @Override
  public void run() {
    // 读取数据库获取所有收场的歌曲
    loveList = dataBaseManager.getPlayEasyList(LOVELISTID);
    if (loveList == null) {
      // 没有这个播放列表
      // 添加这个播放列表
      dataBaseManager.addPlayList(LOVELISTID, getName());
    }

    // 获取列表数据
    PlayList playList = dataBaseManager.getPlayList(LOVELISTID);
    if (null != playList) {
      loveList = playList;
    }
    x.task().post(new Runnable() {
      @Override
      public void run() {
        // 数据加载完成
        dataLoaded();
      }
    });
  }

  private boolean isLetterViewShowing = false; // 标记字母导航是否显示状态
  private boolean isLetterViewTouching = false; // 标记拼音导航条是否触摸

  // 显示拼音导航
  private void showLeeterview() {
    if (!isLetterViewShowing) {
      ViewCompat.setTranslationX(letterView, letterView.getWidth());
      ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(0);
      animatorCompat.setDuration(300);
      animatorCompat.start();
      isLetterViewShowing = true;
    }
  }

  private Runnable LetterViewHider = new Runnable() {
    @Override
    public void run() {
      if (scrollState == RecyclerView.SCROLL_STATE_IDLE && isLetterViewShowing && !isLetterViewTouching) {
        ViewCompat.setTranslationX(letterView, 0);
        ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
        animatorCompat.setDuration(300);
        animatorCompat.start();
        isLetterViewShowing = false;
      }
    }
  };

  // 隐藏拼音导航
  private void hideLeeterview() {
    x.task().removeCallbacks(LetterViewHider);
    x.task().postDelayed(LetterViewHider, 4000);
  }

  /**
   * 拼音导航条触摸监听
   *
   * @param v
   * @param event
   * @return
   */
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      isLetterViewTouching = true;
      x.task().removeCallbacks(LetterViewHider);
    } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
      isLetterViewTouching = false;
      hideLeeterview();
    }
    return false;
  }

  @Override
  public void onGetBroad(Intent intent) {
    if (ACTION.LOVE_CHANGE.equals(intent.getAction())) {

      boolean love = intent.getBooleanExtra("love", false);
      Music music = intent.getParcelableExtra("music");
      if (music != null) {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity == null) {
          this.lovecjange = true;
          return;
        }

        if (loveList == null) {
          loveList = new PlayList();
          loveList.setId(LOVELISTID);
          loveList.setName(getName());
        }
        if (loveList.getMusics() == null) {
          loveList.setMusics(new ArrayList<Music>());
        }
        if (love) {
          // 收藏一首歌
          if (!loveList.getMusics().contains(music)) {
            loveList.getMusics().add(music);
            if (adapter.getMusicList() != null) {
              adapter.getMusicList().add(music);
              adapter.notifyItemChanged(0);
              adapter.notifyItemInserted(adapter.getItemCount());
              return;
            }
          }
        } else {
          // 取消收藏一首歌
          if (loveList.getMusics().contains(music)) {
            loveList.getMusics().remove(music);
            if (adapter.getMusicList() != null) {
              int i = adapter.getMusicList().indexOf(music);
              if (i >= 0) {
                adapter.getMusicList().remove(i);
                adapter.notifyItemRemoved(i + 1);
                adapter.notifyItemChanged(0);
                adapter.notifyItemRangeChanged(i + 1, adapter.getItemCount());
                return;
              }
            }
          }
        }
        adapter.setMusicList(loveList.getMusics());
        if (adapter.getItemCount() > 0) {
          if (letterView != null)
            letterView.setVisibility(View.VISIBLE);
        } else {
          if (letterView != null)
            letterView.setVisibility(View.GONE);
        }
      }
    }
  }

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
                  mainActivity.broadRemoveMusic(music);
                  adapter.remove(music);
                  loveList.getMusics().remove(music);
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
    if (music == null) {
      // 播放全部
      getBaseActivity().broadPlay(loveList.getMusics(), loveList.getId());
    } else {
      // 播放单曲
      getBaseActivity().broadPlay(music);
    }
  }

  @Override
  public void onRefreshAll() {
    loadData();
  }

  @Override
  public void onLetterSelected(int index, char letter) {
    if (loveList != null && loveList.getMusics() != null && loveList.getMusics().size() > 0) {
      for (int i = 0; i < loveList.getMusics().size(); i++) {
        Music music = loveList.getMusics().get(i);
        if (letter == music.getLetter().charAt(0)) {
          recyclerView.scrollToPosition(i);
          return;
        }
      }
    }
  }


}
