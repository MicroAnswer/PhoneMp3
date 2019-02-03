package answer.android.phonemp3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 可以滑动的时候有翻转效果的圆形imageview.用于播放界面展示专辑图片和左右滑动切换歌曲展示动画效果
 * Created by Micro on 2017/6/28.
 */

public class RollIngOverCircleImageView extends CircleImageView {

  private float deg;
  private boolean isRoation;

  public RollIngOverCircleImageView(Context context) {
    super(context);
  }

  public RollIngOverCircleImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RollIngOverCircleImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void startRotation() {
    isRoation = true;
    invalidate();
  }

  public void pauseRotation() {
    isRoation = false;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.rotate(deg, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
    super.onDraw(canvas);
  }

  @Override
  public void computeScroll() {
    super.computeScroll();
    if (isRoation) {
      deg += 0.35f;
      if (deg >= 360) {
        deg = 0;
      }
      invalidate();
    }
  }
}
