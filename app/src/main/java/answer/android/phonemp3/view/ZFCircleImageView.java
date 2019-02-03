package answer.android.phonemp3.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * // 正方形的ImageView[仅仅用于Girdview]
 * Created by Micro on 2017-7-20.
 */

public class ZFCircleImageView extends CircleImageView {
  public ZFCircleImageView(Context context) {
    super(context);
  }

  public ZFCircleImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ZFCircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
  }
}
