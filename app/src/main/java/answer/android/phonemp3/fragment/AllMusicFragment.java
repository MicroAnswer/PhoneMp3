package answer.android.phonemp3.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
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
 * 所有音乐界面
 * Created by Microanswer on 2017/6/14.
 */

public class AllMusicFragment extends BaseFragment implements BroadListener,
        AllMusicListAdapter.OnItemMenuClickListener,
        LetterView.OnLetterSelectListener, View.OnTouchListener {

  private final int REQUEST_CODE = 10;

  // 所有歌曲集合
  private static PlayList allmusicPlayList;

  private LetterView letterView;
  private RecyclerView allMusicList;
  private AllMusicListAdapter allMusicListAdapter;
  private boolean refresh; // 标记加载时使用全加载还是热加载
  private int scrollState;

  public AllMusicFragment() {
    setName("所有音乐");
  }

  @Override
  public BaseFragment afterNew(BaseActivity baseActivity) {
    allMusicListAdapter = new AllMusicListAdapter(baseActivity);
    allMusicListAdapter.setOnItemMenuClickListener(this);
    allMusicListAdapter.setDataBaseManager(baseActivity.getDataBaseManager());
    if (!baseActivity.hasBroadListener(this)) {
      baseActivity.addBroadListener(this);
    }
    return super.afterNew(baseActivity);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (getFragmentView() == null) {
      setFragmentView(inflater.inflate(R.layout.fragment_allmusic, container, false));
    }
    return getFragmentView();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    allMusicList = (RecyclerView) findViewById(R.id.fragment_alllmusic_listview);
    allMusicList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    if (null == allMusicListAdapter) {
      allMusicListAdapter = new AllMusicListAdapter(getActivity());
      allMusicListAdapter.setDataBaseManager(getBaseActivity().getDataBaseManager());
      allMusicListAdapter.setOnItemMenuClickListener(this);
    }

    allMusicList.setAdapter(allMusicListAdapter);
    scrollState = 0;
    allMusicList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    letterView = (LetterView) findViewById(R.id.fragment_alllmusic_letterview);
    letterView.setOnTouchListener(this);
    letterView.setOnLetterSelectListener(this);

    if (!getBaseActivity().hasBroadListener(this)) {
      getBaseActivity().addBroadListener(this);
    }

    // 隐藏拼音
    ViewCompat.setTranslationX(letterView, 0);
    ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
    animatorCompat.setDuration(300);
    animatorCompat.start();
    isLetterViewShowing = false;

    perpLoadData();

  }

  // 预备加载数据,
  private void perpLoadData() {
    if (allmusicPlayList != null &&
            allmusicPlayList.getMusics() != null &&
            allmusicPlayList.getMusics().size() > 0) {
      // 有数据,无需再次加载,直接显示
      allMusicLoaded.run();
    } else {
      // 需要加载数据
      showLoadingView();
      loadAllMusic();
    }
  }


  // 所有歌曲加载完成回调
  private Runnable allMusicLoaded = new Runnable() {
    @Override
    public void run() {
      if (refresh) {
        getBaseActivity().tip("刷新完成");
        refresh = false;
      }

      if (null == allmusicPlayList || allmusicPlayList.getMusics() == null || allmusicPlayList.getMusics().size() < 1) {
        showEmptyView();
      } else {
        // 有歌曲,显示歌曲
        hideLoad_EmptyView();
        letterView.setVisibility(View.VISIBLE);
        allmusicPlayList.setId("allmusic");
        allMusicListAdapter.setMusicList(allmusicPlayList.getMusics());
      }
    }
  };

  // 加载手机中的所有音乐文件
  private void loadAllMusic() {
    // 首先检查权限
    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      // 没有读取权限,申请权限
      requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    } else {

      // 有读取权限
      // 继续读取
      x.task().run(dataLoader);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // 是来自本类的权限请求
    if (requestCode == REQUEST_CODE) {
      if (grantResults != null && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // 授权成功
        loadAllMusic();
      } else {
        // 授权失败
        AlertDialog.Builder tipdialog = new AlertDialog.Builder(getContext());
        tipdialog.setTitle(R.string.tip);
        tipdialog.setMessage(R.string.musicpermisiondenied);
        tipdialog.setPositiveButton(R.string.repermission, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            loadAllMusic();
          }
        });
        tipdialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            getActivity().finish();
          }
        }).show();
      }
    }
  }

  private Runnable dataLoader = new Runnable() {

    @Override
    public void run() {
      List<Music> allMusics = new ArrayList<>();
      allmusicPlayList = new PlayList();
      if (!refresh) {
        if (getBaseActivity() instanceof MainActivity) {
          if ("allmusic".equals("" + ((MainActivity) getBaseActivity()).getPlayingListId())) {
            PlayList playList = getBaseActivity().getDataBaseManager().getPlayList(DataBaseManager.CURRENT_PLAYLIST_ID);
            if (playList != null && playList.getMusics() != null) {
              allMusics = playList.getMusics();
            }
          }
          if (allMusics != null && allMusics.size() > 0) {
            allmusicPlayList.setMusics(allMusics);
            x.task().post(allMusicLoaded);
            return;
          }
        }
      }
      if (allMusics == null) {
        allMusics = new ArrayList<>();
      }
      // 加载所有歌曲
      ContentResolver contentResolver = getContext().getContentResolver();
      Cursor query = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
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
                getBaseActivity().getLogger().info("值填充出错：");
                getBaseActivity().getLogger().err(e);
              }
            }
          }
          if (isMusic(music)) {
            String s = Tool.getPinYin(music.getTitle());
            music.setLetter(s);
            allMusics.add(music);
          }
        } while (query.moveToNext());
        query.close();
        Collections.sort(allMusics);
      }
      allmusicPlayList.setMusics(allMusics);
      x.task().post(allMusicLoaded);
    }
  };

  private boolean isMusic(Music music) {

    if (null == music) {
      return false;
    }

    String duration = music.getDuration();
    if (TextUtils.isEmpty(duration) || "null".equals(duration.toLowerCase())) {
      return false;
    }
    Exception ee = null;
    try {
      long l = Long.parseLong(duration);
      if (l < 10 * 1000) {
        return false; // 不要小于10秒的歌曲
      }
    } catch (Exception e) {
      ee = e;
      e.printStackTrace();
    }
    if (ee != null) {
      return false;
    }

    String data = music.get_data();
    if (TextUtils.isEmpty(data) || "null".equals(data.toLowerCase())) {
      return false; // 没有的数据
    }
    if (data.toLowerCase().endsWith("mp3")) {
      File n = new File(data);
      if (n.isFile() && n.exists()) {
        return true;
      }
    }

    return false;
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

  // listview中每隔条目右边的菜单点击
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
                  allmusicPlayList.getMusics().remove(music);
                  mainActivity.broadRemoveMusic(music);
                  allMusicListAdapter.remove(music);
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
            music.isLoved = false;
            // 已经是收藏的歌曲,进行取消收藏
            getBaseActivity().getDataBaseManager().removeMusicFromPlayList(LovesFragment.LOVELISTID, music);
            intent.putExtra("love", false);
          } else {
            music.isLoved = true;
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


  // 歌曲点击
  @Override
  public void onItemClick(View v, Music music) {
    // 播放全部点击了,
    if (music == null) {
      getBaseActivity().broadPlay(allmusicPlayList.getMusics(), allmusicPlayList.getId());
    } else {
      getBaseActivity().broadPlay(music);
    }
  }

  @Override
  public void onRefreshAll() {
    getBaseActivity().tip("开始刷新");
    refresh = true;
    loadAllMusic();
  }

  // 拼音列表变化监听
  @Override
  public void onLetterSelected(int index, char letter) {
    if (letter == '*') {
      allMusicList.scrollToPosition(0);
    } else if (letter == '#') {
      allMusicList.scrollToPosition(allmusicPlayList.getMusics().size());
    } else {
      Music m = new Music();
      m.setLetter(String.valueOf(letter));
      if (allmusicPlayList.getMusics() != null && allmusicPlayList.getMusics().size() > 0) {
        int i1 = 0;
        for (int i = 0; i < allmusicPlayList.getMusics().size(); i++) {
          if (allmusicPlayList.getMusics().get(i).getLetter().charAt(0) == letter) {
            i1 = i;
            break;
          }
        }
        allMusicList.scrollToPosition(i1 + 1);
      }
    }
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onGetBroad(Intent intent) {
    if (ACTION.LOVE_CHANGE.equals(intent.getAction())) {
      // boolean love = intent.getBooleanExtra("love", false);
      Music music = intent.getParcelableExtra("music");
      if(music == null) {
        Toast.makeText(getBaseActivity(), "收藏出了点意外", Toast.LENGTH_SHORT).show();
        return;
      }
      if (allmusicPlayList != null && allmusicPlayList.getMusics() != null && allmusicPlayList.getMusics().size() > 0) {
        int i = allmusicPlayList.getMusics().indexOf(music);
        allmusicPlayList.getMusics().get(i).isLoved = intent.getBooleanExtra("love", !allmusicPlayList.getMusics().get(i).isLoved);
        allMusicListAdapter.notifyItemChanged(i + 1);
      }
//      allMusicListAdapter.notifyDataSetChanged();
    } else if (ACTION.DELETE.equals(intent.getAction())) {
      Music music = intent.getParcelableExtra("music");
      if (allMusicListAdapter != null)
        allMusicListAdapter.remove(music);
    }
  }
}
