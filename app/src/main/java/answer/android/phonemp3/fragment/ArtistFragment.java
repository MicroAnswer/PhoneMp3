package answer.android.phonemp3.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.BaseActivity;
import answer.android.phonemp3.adapter.ArtistAdapter;
import answer.android.phonemp3.bean.Artist;
import answer.android.phonemp3.dialog.ArtistAblumsDialog;
import answer.android.phonemp3.interfaces.BroadListener;
import answer.android.phonemp3.tool.RecyclerViewDecoration;
import answer.android.phonemp3.tool.Tool;
import answer.android.phonemp3.view.LetterView;

/**
 * 所有音乐界面
 * Created by Microanswer on 2017/6/14.
 */

public class ArtistFragment extends BaseFragment implements ArtistAdapter.OnItemClickListener, LetterView.OnLetterSelectListener, BroadListener, View.OnTouchListener {

    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private LetterView letterView;
    private static ArrayList<Artist> allArtists;
    private int scrollState;
    private RecyclerViewDecoration recyclerViewDecoration;


    public ArtistFragment() {
        setName("歌手");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == getFragmentView()) {
            setFragmentView(inflater.inflate(R.layout.fragment_artist, container, false));
        }
        return getFragmentView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) findViewById(R.id.fragment_artist_recyclerview);
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
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
        letterView = (LetterView) findViewById(R.id.fragment_artist_letterview);
        letterView.setOnLetterSelectListener(this);
        letterView.setOnTouchListener(this);

        if (artistAdapter == null) {
            artistAdapter = new ArtistAdapter();
            artistAdapter.setOnItemClickListener(this);
        }

        recyclerView.setAdapter(artistAdapter);
        if (null == recyclerViewDecoration) {
            recyclerViewDecoration = new RecyclerViewDecoration(getContext());
            recyclerView.addItemDecoration(recyclerViewDecoration);
        }
        artistAdapter.setOnItemClickListener(this);

        // 隐藏拼音
        ViewCompat.setTranslationX(letterView, 0);
        ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
        animatorCompat.setDuration(300);
        animatorCompat.start();
        isLetterViewShowing = false;

        if (!getBaseActivity().hasBroadListener(this)) {
            getBaseActivity().addBroadListener(this);
        }

        preLoadData();
    }

    private void preLoadData() {
        if (allArtists != null && allArtists.size() > 0) {
            // 缓存有数据，不用加载
            allMusicDataLoadOk.run();
        } else {
            loadAllMusic();
        }
    }

    // 加载所有歌曲
    private void loadAllMusic() {
        showLoadingView();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            x.task().run(allMusicLoader);
        } else {
            // 没有权限,显示空
            showEmptyView();
            letterView.setVisibility(View.GONE);
        }
    }

    private Runnable allMusicLoader = new Runnable() {
        @Override
        public void run() {
            Cursor query = getContext().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null, null);
            allArtists = new ArrayList<>();
            if (query != null && query.getCount() > 0 && query.moveToFirst()) {
                do {
                    Field[] declaredFields = Artist.class.getDeclaredFields();
                    Artist artist = new Artist();
                    for (Field f : declaredFields) {
                        f.setAccessible(true);
                        int columnIndex = query.getColumnIndex(f.getName());
                        if (columnIndex > -1) {
                            try {
                                f.set(artist, query.getString(columnIndex));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String pinYin = Tool.getPinYin(artist.getArtist());
                    artist.setLeeter(pinYin);
                    allArtists.add(artist);
                } while (query.moveToNext());
            }
            Collections.sort(allArtists);
            x.task().post(allMusicDataLoadOk);
        }
    };

    private Runnable allMusicDataLoadOk = new Runnable() {
        @Override
        public void run() {
            if (allArtists.size() < 1) {
                showEmptyView();
                letterView.setVisibility(View.GONE);
            } else {
                hideLoad_EmptyView();
                letterView.setVisibility(View.VISIBLE);
                artistAdapter.setArtists(allArtists);
            }
        }
    };

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
    public void onClick(Artist artist) {
        new ArtistAblumsDialog(getBaseActivity(), artist).show();
    }

    @Override
    public void onLetterSelected(int indsex, char letter) {
        if (allArtists != null && allArtists.size() > 0) {
            for (int index = 0; index < allArtists.size(); index++) {
                Artist artist = allArtists.get(index);
                if (artist.getLeeter().charAt(0) == letter) {
                    recyclerView.scrollToPosition(index);
                    return;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerViewDecoration = null;
    }

    @Override
    public BaseFragment afterNew(BaseActivity baseActivity) {
        artistAdapter = new ArtistAdapter();
        artistAdapter.setOnItemClickListener(this);
        if (!baseActivity.hasBroadListener(this)) {
            baseActivity.addBroadListener(this);
        }
        return super.afterNew(baseActivity);
    }

    @Override
    public void onGetBroad(Intent intent) {
        if (ACTION.DELETE.equals(intent.getAction())) {
            loadAllMusic();
        }
    }
}

//package answer.android.phonemp3.fragment;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.ViewCompat;
//import android.support.v4.view.ViewPropertyAnimatorCompat;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//
//import org.xutils.common.util.DensityUtil;
//import org.xutils.x;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//
//import answer.android.phonemp3.ACTION;
//import answer.android.phonemp3.R;
//import answer.android.phonemp3.activity.BaseActivity;
//import answer.android.phonemp3.adapter.ArtistAdapter;
//import answer.android.phonemp3.adapter.ArtistAdapter2;
//import answer.android.phonemp3.api.API;
//import answer.android.phonemp3.bean.Artist;
//import answer.android.phonemp3.bean.Music;
//import answer.android.phonemp3.dialog.ArtistAblumsDialog;
//import answer.android.phonemp3.interfaces.BroadListener;
//import answer.android.phonemp3.tool.RecyclerViewDecoration;
//import answer.android.phonemp3.tool.Tool;
//import answer.android.phonemp3.view.LetterView;
//
///**
// * 所有音乐界面
// * Created by Microanswer on 2017/6/14.
// */
//
//public class ArtistFragment extends BaseFragment implements ArtistAdapter2.OnItemClickListener, LetterView.OnLetterSelectListener, BroadListener, View.OnTouchListener {
//
//  private RecyclerView recyclerView;
//  private ArtistAdapter2 artistAdapter;
//  private LetterView letterView;
//  private static ArrayList<Artist> allArtists;
//  private int scrollState;
//  // private RecyclerViewDecoration recyclerViewDecoration;
//
//
//  public ArtistFragment() {
//    setName("歌手");
//  }
//
//  @Nullable
//  @Override
//  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//    if (null == getFragmentView()) {
//      setFragmentView(inflater.inflate(R.layout.fragment_artist, container, false));
//    }
//    return getFragmentView();
//  }
//
//  @Override
//  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//    super.onViewCreated(view, savedInstanceState);
//    recyclerView = (RecyclerView) findViewById(R.id.fragment_artist_recyclerview);
//    if (recyclerView.getLayoutManager() == null) {
//      recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//    }
//    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//      @Override
//      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//        // newState : [1 = 手指拖动, 2 = 惯性滚动, 0 = 停止滚动]
//        scrollState = newState;
//        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//          showLeeterview();
//        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//          hideLeeterview();
//        }
//      }
//
//      @Override
//      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//      }
//    });
//    letterView = (LetterView) findViewById(R.id.fragment_artist_letterview);
//    letterView.setOnLetterSelectListener(this);
//    letterView.setOnTouchListener(this);
//
//    if (artistAdapter == null) {
//      artistAdapter = new ArtistAdapter2();
//      artistAdapter.setOnItemClickListener(this);
//    }
//
//    recyclerView.setAdapter(artistAdapter);
//    // if (null == recyclerViewDecoration) {
//    //   recyclerViewDecoration = new RecyclerViewDecoration(getContext());
//    //   recyclerView.addItemDecoration(recyclerViewDecoration);
//    // }
//    artistAdapter.setOnItemClickListener(this);
//
//    // 隐藏拼音
//    ViewCompat.setTranslationX(letterView, 0);
//    ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
//    animatorCompat.setDuration(300);
//    animatorCompat.start();
//    isLetterViewShowing = false;
//
//    if (!getBaseActivity().hasBroadListener(this)) {
//      getBaseActivity().addBroadListener(this);
//    }
//
//    preLoadData();
//  }
//
//  private void preLoadData() {
//    if (allArtists != null && allArtists.size() > 0) {
//      // 缓存有数据，不用加载
//      allMusicDataLoadOk.run();
//    } else {
//      loadAllMusic();
//    }
//  }
//
//  // 加载所有歌曲
//  private void loadAllMusic() {
//    showLoadingView();
//    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//      x.task().run(allMusicLoader);
//    } else {
//      // 没有权限,显示空
//      showEmptyView();
//      letterView.setVisibility(View.GONE);
//    }
//  }
//
//  private Runnable allMusicLoader = new Runnable() {
//    @Override
//    public void run() {
//      Cursor query = getContext().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null, null);
//      allArtists = new ArrayList<>();
//      if (query != null && query.getCount() > 0 && query.moveToFirst()) {
//        do {
//          Field[] declaredFields = Artist.class.getDeclaredFields();
//          Artist artist = new Artist();
//          for (Field f : declaredFields) {
//            f.setAccessible(true);
//            int columnIndex = query.getColumnIndex(f.getName());
//            if (columnIndex > -1) {
//              try {
//                f.set(artist, query.getString(columnIndex));
//              } catch (IllegalAccessException e) {
//                e.printStackTrace();
//              }
//            }
//          }
//          String pinYin = Tool.getPinYin(artist.getArtist());
//          artist.setLeeter(pinYin);
//          allArtists.add(artist);
//        } while (query.moveToNext());
//      }
//      Collections.sort(allArtists);
//      x.task().post(allMusicDataLoadOk);
//    }
//  };
//
//  private Runnable allMusicDataLoadOk = new Runnable() {
//    @Override
//    public void run() {
//      if (allArtists.size() < 1) {
//        showEmptyView();
//        letterView.setVisibility(View.GONE);
//      } else {
//        hideLoad_EmptyView();
//        letterView.setVisibility(View.VISIBLE);
//        artistAdapter.setArtists(allArtists);
//      }
//    }
//  };
//
//  private boolean isLetterViewShowing = false; // 标记字母导航是否显示状态
//  private boolean isLetterViewTouching = false; // 标记拼音导航条是否触摸
//
//  // 显示拼音导航
//  private void showLeeterview() {
//    if (!isLetterViewShowing) {
//      ViewCompat.setTranslationX(letterView, letterView.getWidth());
//      ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(0);
//      animatorCompat.setDuration(300);
//      animatorCompat.start();
//      isLetterViewShowing = true;
//    }
//  }
//
//  private Runnable LetterViewHider = new Runnable() {
//    @Override
//    public void run() {
//      if (scrollState == RecyclerView.SCROLL_STATE_IDLE && isLetterViewShowing && !isLetterViewTouching) {
//        ViewCompat.setTranslationX(letterView, 0);
//        ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
//        animatorCompat.setDuration(300);
//        animatorCompat.start();
//        isLetterViewShowing = false;
//      }
//    }
//  };
//
//  // 隐藏拼音导航
//  private void hideLeeterview() {
//    x.task().removeCallbacks(LetterViewHider);
//    x.task().postDelayed(LetterViewHider, 4000);
//  }
//
//  /**
//   * 拼音导航条触摸监听
//   *
//   * @param v
//   * @param event
//   * @return
//   */
//  @Override
//  public boolean onTouch(View v, MotionEvent event) {
//    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//      isLetterViewTouching = true;
//      x.task().removeCallbacks(LetterViewHider);
//    } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
//      isLetterViewTouching = false;
//      hideLeeterview();
//    }
//    return false;
//  }
//
//  @Override
//  public void onClick(final Artist artist) {
//    // new ArtistAblumsDialog(getBaseActivity(), artist).show();
//    x.task().run(new Runnable() {
//      @Override
//      public void run() {
//        final String getinfo = API.LAST_FM_API.ARTIST.GETINFO(getActivity(), artist.getArtist());
//        x.task().post(new Runnable() {
//          @Override
//          public void run() {
//            getBaseActivity().alert(getinfo);
//          }
//        });
//      }
//    });
//  }
//
//  @Override
//  public void onLetterSelected(int indsex, char letter) {
//    if (allArtists != null && allArtists.size() > 0) {
//      for (int index = 0; index < allArtists.size(); index++) {
//        Artist artist = allArtists.get(index);
//        if (artist.getLeeter().charAt(0) == letter) {
//          recyclerView.scrollToPosition(index);
//          return;
//        }
//      }
//    }
//  }
//
//  @Override
//  public void onDestroy() {
//    super.onDestroy();
//    // recyclerViewDecoration = null;
//  }
//
//  @Override
//  public BaseFragment afterNew(BaseActivity baseActivity) {
//    artistAdapter = new ArtistAdapter2();
//    artistAdapter.setOnItemClickListener(this);
//    if (!baseActivity.hasBroadListener(this)) {
//      baseActivity.addBroadListener(this);
//    }
//    return super.afterNew(baseActivity);
//  }
//
//  @Override
//  public void onGetBroad(Intent intent) {
//    if (ACTION.DELETE.equals(intent.getAction())) {
//      if (getActivity() != null && getContext() != null)
//        loadAllMusic();
//    }
//  }
//}
