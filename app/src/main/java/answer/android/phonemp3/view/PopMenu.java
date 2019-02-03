package answer.android.phonemp3.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.xutils.x;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import answer.android.phonemp3.R;

/**
 * 点击主界面的菜单按钮显示这个菜单界面
 * Created by Microanswer on 2017/6/13.
 */

public class PopMenu extends PopupWindow implements View.OnClickListener {
  private Context context;
  private View mContainerView;
  private ViewPropertyAnimatorCompat viewPropertyAnimatorCompat;
  private int mWidth, mHeight;
  private ImageView imageView; // 头像
  private TextView textView; // 用户名

  public PopMenu(@NonNull Context context) {
    super(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    mContainerView = View.inflate(context, R.layout.popmenu_main, null);
    setContentView(mContainerView);
    // 计算大小
    mWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    mHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    mContainerView.measure(mWidth, mHeight);
    mWidth = mContainerView.getMeasuredWidth();
    mHeight = mContainerView.getMeasuredHeight();

    this.context = context;
    setOutsideTouchable(true);
    setFocusable(true);
    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    setAnimationStyle(R.style.popmenu_anim);
    CardView c = ((CardView) ((LinearLayout) mContainerView).getChildAt(0));
    c.setCardElevation(20f);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      c.setElevation(15f);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      setAttachedInDecor(true);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        Method setLayoutInsetDecor = PopupWindow.class.getDeclaredMethod("setLayoutInsetDecor", boolean.class);
        setLayoutInsetDecor.setAccessible(true);
        setLayoutInsetDecor.invoke(this, true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      Field[] declaredFields = PopupWindow.class.getDeclaredFields();
      for (Field e : declaredFields) {
        e.setAccessible(true);
        // Log.i("aaaa", e.getType().getName() + " " + e.getName() + " = " + e.get(this));
        if (e.getName().toUpperCase().contains("DECOR")) {
          e.set(this, true);
          // Log.i("aaaa", e.getType().getName() + " " + e.getName() + " = " + e.get(this));
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 设置监听
    mContainerView.findViewById(R.id.popmenu_main_about).setOnClickListener(this);
    mContainerView.findViewById(R.id.popmenu_main_exit).setOnClickListener(this);
    mContainerView.findViewById(R.id.popmenu_main_head).setOnClickListener(this);
    mContainerView.findViewById(R.id.popmenu_main_setting).setOnClickListener(this);
    mContainerView.findViewById(R.id.popmenu_main_ptalk).setOnClickListener(this);
    mContainerView.findViewById(R.id.popmenu_main_tool).setOnClickListener(this);

    imageView = (ImageView) mContainerView.findViewById(R.id.popmenu_main_headimg);
    textView = (TextView) mContainerView.findViewById(R.id.popmenu_main_username);
  }

  @Override
  public void showAtLocation(View parent, int gravity, int x, int y) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      setAttachedInDecor(true);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        Method setLayoutInsetDecor = PopupWindow.class.getDeclaredMethod("setLayoutInsetDecor", boolean.class);
        setLayoutInsetDecor.setAccessible(true);
        setLayoutInsetDecor.invoke(this, true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      Field[] declaredFields = PopupWindow.class.getDeclaredFields();
      for (Field e : declaredFields) {
        e.setAccessible(true);
        // Log.i("aaaa", e.getType().getName() + " " + e.getName() + " = " + e.get(this));
        if (e.getName().toUpperCase().contains("DECOR")) {
          e.set(this, true);
          // Log.i("aaaa", e.getType().getName() + " " + e.getName() + " = " + e.get(this));
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.showAtLocation(parent, gravity, x, y);
    if (viewPropertyAnimatorCompat != null) {
      viewPropertyAnimatorCompat.cancel();
    }
    ViewCompat.setTranslationY(mContainerView, -mHeight);
//     ViewCompat.setTranslationX(mContainerView, -mWidth);
    ViewCompat.setScaleX(mContainerView, 0.2f);
    ViewCompat.setPivotX(mContainerView, 0);
    viewPropertyAnimatorCompat = ViewCompat.animate(mContainerView).translationY(0f).scaleX(1f);
    viewPropertyAnimatorCompat.setDuration(250);
    viewPropertyAnimatorCompat.start();
  }

  @Override
  public void onClick(final View v) {
    if (onClickListener != null) {
      dismiss();
      x.task().postDelayed(new Runnable() {
        @Override
        public void run() {
          onClickListener.onClick(v);
        }
      }, 250);
    }
  }

  public ImageView getHeadImgView() {
    return imageView;
  }

  public TextView getUserNameTextView() {
    return textView;
  }

  private View.OnClickListener onClickListener;

  public void setMenuItemClickListener(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  public View.OnClickListener getOnClickListener() {
    return this.onClickListener;
  }
}
