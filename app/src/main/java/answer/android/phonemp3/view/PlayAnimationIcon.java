package answer.android.phonemp3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.view.View;

import org.xutils.common.util.DensityUtil;

import answer.android.phonemp3.R;

/**
 * 播放小动画{类似频率变化的那种帧动画动画效果}
 * Created by Micro on 2017/6/27.
 */

public class PlayAnimationIcon extends View {

  private int minSize;

  private int defaltPadding;
  private float eachW;
  private float eachSpace;
  private float eachH;
  private RectF[] rectfs;
  private boolean[] change2;

  private Paint paint;

  private boolean isAnimationing = false;

  private float minH;

  public PlayAnimationIcon(Context context) {
    super(context);
    init(context, null);
  }

  private void init(Context context, AttributeSet attrs) {
    minSize = DensityUtil.dip2px(40f);
    defaltPadding = DensityUtil.dip2px(10f);
    eachSpace = DensityUtil.dip2px(2.5f);
    rectfs = new RectF[4];
    eachH = minSize - (defaltPadding * 2);
    int e3 = Math.round(eachH / 3f);
    eachW = (eachH - (3 * eachSpace)) / 4;
    minH = (eachH / 3);

    rectfs[0] = new RectF(defaltPadding, defaltPadding + e3 / 2, defaltPadding + eachW, defaltPadding + eachH);
    rectfs[1] = new RectF(rectfs[0].right + eachSpace, defaltPadding + e3, rectfs[0].right + eachSpace + eachW, defaltPadding + eachH);
    rectfs[2] = new RectF(rectfs[1].right + eachSpace, defaltPadding + e3 * 3 / 2, rectfs[1].right + eachSpace + eachW, defaltPadding + eachH);
    rectfs[3] = new RectF(rectfs[2].right + eachSpace, defaltPadding + e3, rectfs[2].right + eachSpace + eachW, defaltPadding + eachH);

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(context.getResources().getColor(R.color.colorPrimary));

    change2 = new boolean[4];
    change2[0] = Math.random() > 0.5f;
    change2[1] = Math.random() > 0.5f;
    change2[2] = Math.random() > 0.5f;
    change2[3] = Math.random() > 0.5f;
  }

  public PlayAnimationIcon(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public void pause() {
    isAnimationing = false;
  }

  public boolean isAnimationing() {
    return isAnimationing;
  }

  public void start() {
    isAnimationing = true;
    invalidate();
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(minSize, minSize);
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (RectF r : rectfs) {
      canvas.drawRoundRect(r, 1, 1, paint);
    }

  }

  @Override
  public void computeScroll() {
    super.computeScroll();
    if (isAnimationing) {
      for (int index = 0; index < rectfs.length; index++) {
        RectF r = rectfs[index];
        boolean change_2 = change2[index];
        if (change_2) {
          // 变矮
          r.set(r.left, r.top + 2, r.right, r.bottom);
          if (r.bottom - r.top <= minH) {
            r.set(r.left, r.bottom - minH, r.right, r.bottom);
            change2[index] = false;
          }
        } else {
          // 变高
          r.set(r.left, r.top - 2, r.right, r.bottom);
          if (r.bottom - r.top >= eachH) {
            r.set(r.left, r.bottom - eachH, r.right, r.bottom);
            change2[index] = true;
          }
        }
      }
      invalidate();
    }
  }
}
