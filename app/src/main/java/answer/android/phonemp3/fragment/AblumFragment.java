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
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.appcompat.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.xutils.common.util.DensityUtil;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.AblumActivity;
import answer.android.phonemp3.activity.BaseActivity;
import answer.android.phonemp3.adapter.AblumFragmentGridViewAdapter;
import answer.android.phonemp3.bean.Ablum;
import answer.android.phonemp3.interfaces.BroadListener;
import answer.android.phonemp3.tool.Tool;
import answer.android.phonemp3.view.LetterView;

/**
 * 所有音乐界面
 * Created by Microanswer on 2017/6/14.
 */

public class AblumFragment extends BaseFragment implements LetterView.OnLetterSelectListener, BroadListener, AblumFragmentGridViewAdapter.OnItemClick, View.OnTouchListener {

  private RecyclerView gridView;
  private AblumFragmentGridViewAdapter adapter;
  private LetterView letterView;
  private static ArrayList<Ablum> ablums;
  private int PERMISSIOMREQUESTCODE = 17;
  private int scrollState;


  public AblumFragment() {
    super();
    setName("专辑");
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (getFragmentView() == null) {
      setFragmentView(inflater.inflate(R.layout.fragment_ablum, container, false));
    }
    return getFragmentView();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    letterView = (LetterView) findViewById(R.id.fragment_ablum_letterview);
    letterView.setOnLetterSelectListener(this);
    letterView.setOnTouchListener(this);
    gridView = (RecyclerView) findViewById(R.id.fragment_ablum_gridview);
    gridView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
    gridView.setAdapter(adapter);
    scrollState = 0;
    gridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    // 隐藏拼音
    ViewCompat.setTranslationX(letterView, 0);
    ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(letterView).translationX(letterView.getWidth() == 0 ? DensityUtil.dip2px(25f) : letterView.getWidth());
    animatorCompat.setDuration(300);
    animatorCompat.start();
    isLetterViewShowing = false;

    if (!getBaseActivity().hasBroadListener(this)) {
      getBaseActivity().addBroadListener(this);
    }

    prepLoadData();
  }

  // 预加载数据,
  private void prepLoadData() {
    if (ablums != null && ablums.size() > 0) {
      // 缓存有数据
      onAblumDataLoadSuccess.run();
    } else {
      loadData();
    }
  }

  // 处理了权限问题
  private void loadData() {
    showLoadingView();
    int i = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
    if (i == PackageManager.PERMISSION_GRANTED) {
      // 加载专辑数据
      x.task().run(ablumDataLoader);
    } else {
      // 申请权限
      requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIOMREQUESTCODE);
    }
  }

  // 专辑数据加载完成
  private Runnable onAblumDataLoadSuccess = new Runnable() {
    @Override
    public void run() {
      if (ablums == null || ablums.size() < 1) {
        letterView.setVisibility(View.GONE);
        showEmptyView();
      } else {
        hideLoad_EmptyView();
      }
      adapter.setAblums(ablums);
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


  private Runnable ablumDataLoader = new Runnable() {
    @Override
    public void run() {
      // Log.e("aaaa", "开始加载专辑：" + MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI);
      ContentResolver contentResolver = getContext().getContentResolver();
      Cursor query = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, null);
      if (query != null) {
        if (query.getCount() > 0 && query.moveToFirst()) {
          ablums = new ArrayList<>();
          Field[] declaredFields = Ablum.class.getDeclaredFields();
          do {
            Ablum ablum = new Ablum();
            for (Field field : declaredFields) {
              field.setAccessible(true);
              if (query.getColumnIndex(field.getName()) > -1) {
                try {
                  field.set(ablum, query.getString(query.getColumnIndex(field.getName())));
                } catch (IllegalAccessException e) {
                  e.printStackTrace();
                }
              }
            }
            // Log.i("AblumFragment", ablum.toString());
            if (!ablums.contains(ablum)) {
              ablum.setLetter(Tool.getPinYin(ablum.getAlbum()));
              ablums.add(ablum);
            }
          } while (query.moveToNext());

          // 对结果排序
          Collections.sort(ablums);
        }
        query.close();
      }
      x.task().post(onAblumDataLoadSuccess);
    }
  };

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIOMREQUESTCODE && grantResults.length == 1) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        loadData();
      } else {
        // 授权失败
        AlertDialog.Builder tipdialog = new AlertDialog.Builder(getContext());
        tipdialog.setTitle(R.string.tip);
        tipdialog.setMessage(R.string.musicpermisiondenied);
        tipdialog.setPositiveButton(R.string.repermission, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            loadData();
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

  @Override
  public void onLetterSelected(int index, char letter) {
    if (ablums != null && ablums.size() > 0) {
      for (int i = 0; i < ablums.size(); i++) {
        String letter1 = ablums.get(i).getLetter();
        if (!TextUtils.isEmpty(letter1)) {
          if (letter1.charAt(0) == letter) {
            gridView.scrollToPosition(i);
            return;
          }
        }
      }
    }
  }

  @Override
  public BaseFragment afterNew(BaseActivity baseActivity) {
    adapter = new AblumFragmentGridViewAdapter();
    if (!baseActivity.hasBroadListener(this)) {
      baseActivity.addBroadListener(this);
    }
    adapter.setOnItemClick(this);
    return super.afterNew(baseActivity);
  }

  @Override
  public void onGetBroad(Intent intent) {
    if (ACTION.DELETE.equals(intent.getAction())) {
      // 有歌曲被删除了, 重新加载专辑界面
      if (getBaseActivity() != null && getContext() != null)
        loadData();
    }
  }

  @Override
  public void click(Ablum ablum, int position, ImageView img) {
    Intent intent = new Intent(getBaseActivity(), AblumActivity.class);
    intent.putExtra("ablum", ablum);
    intent.putExtra("tn", ablum.getAlbum());
    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), img, ablum.getAlbum()).toBundle();
    ActivityCompat.startActivity(getActivity(), intent, bundle);
  }
}
