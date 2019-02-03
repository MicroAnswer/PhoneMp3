package answer.android.phonemp3.view;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * 弹出框的底层view
 * Created by Micro on 2017/6/24.
 */

public class BaseDialogRootView extends LinearLayout {

  private ViewDragHelper viewDragHelper;
  private View dragView;
  private Dialog dialog;

  public BaseDialogRootView(Context context) {
    super(context);
    init(context);
  }

  public BaseDialogRootView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public void setDialog(Dialog dialog) {
    this.dialog = dialog;
  }

  public Dialog getDialog() {
    return dialog;
  }

  private void init(Context context) {
    setOrientation(VERTICAL);
    viewDragHelper = ViewDragHelper.create(this, 1.0f, callback);
//    viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    dragView = getChildAt(0);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public void computeScroll() {
    super.computeScroll();
    if (viewDragHelper.continueSettling(true)) invalidate();
  }

  ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
    @Override
    public boolean tryCaptureView(View child, int pointerId) {
      return child == dragView;
    }

    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
      return child.getLeft();
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
      // Log.i("onViewReleased", "clampViewPositionVertical, top=" + top + ", dy =" + dy);
      if (top < 0) {
        return 0;
      }
      return top;
    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      // Log.i("onViewReleased", "yvel=" + yvel + ", dragview.getTop=" + dragView.getTop());
      if (yvel >= 1000) {
        //Log.i("onViewReleased", "滑动关闭");
        // 手指松开的时候,速度大于1000开始自动收缩
        if (dialog != null) {
          // 距离
          int distance = getMeasuredHeight() - dragView.getTop();
          // 距离/速度=时间
          int time = Math.round((distance / yvel) * 1000);
          if (viewPropertyAnimatorCompat != null) {
            viewPropertyAnimatorCompat.cancel();
          }
          viewPropertyAnimatorCompat = ViewCompat.animate(dragView)
                  .translationY(getMeasuredHeight())
                  .setDuration(time)
                  .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                      if (dialog != null) {
                        dialog.dismiss();
                      }
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                  });

        }
      } else {
        if (yvel > -1000 && yvel < 1000 && dragView.getTop() > (getMeasuredHeight() / 3)) {
         //  Log.i("onViewReleased", "过低关闭");
          if (dialog != null) {
            dialog.dismiss();
          }
        } else {
          // 回弹
          // Log.i("onViewReleased", "回弹");
          if (viewPropertyAnimatorCompat != null) {
            viewPropertyAnimatorCompat.cancel();
          }
          viewDragHelper.smoothSlideViewTo(dragView,0, 0);
          invalidate();
        }
      }
    }
  };
  private ViewPropertyAnimatorCompat viewPropertyAnimatorCompat;

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return viewDragHelper.shouldInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    viewDragHelper.processTouchEvent(event);
    return true;
  }
}
