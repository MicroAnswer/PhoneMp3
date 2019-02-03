package answer.android.phonemp3.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.common.util.DensityUtil;

/**
 * RecyclerView分割线
 * Created by Microanswer on 2017/7/5.
 */

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {
  private Context context;
  private Paint paint;
  private int width; // 线条粗细

  public RecyclerViewDecoration(Context context) {
    this.context = context;
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.parseColor("#e0e0e0"));
    paint.setStyle(Paint.Style.STROKE);
    width = DensityUtil.dip2px(0.5f);
    paint.setStrokeWidth(width);
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    for (int index = 1; index <= state.getItemCount(); index++) {
      View childAt = parent.getChildAt(index - 1);
      if (null == childAt) {
        continue;
      }
      int left = childAt.getLeft();
      int right = childAt.getRight();
      int top = childAt.getTop();
      int bottom = childAt.getBottom();
      if (index % 3 == 0) {
        // 最右边的item
        c.drawLine(left, bottom + (width / 2), right, bottom + (width / 2), paint); // 下边线条
      } else if (index % 3 == 2) {
        // 中间
        c.drawLine(right + (width / 2), top, right + (width / 2), bottom + (width / 2), paint); // 右边线条
        c.drawLine(left, bottom + (width / 2), right, bottom + (width / 2), paint); // 下边线条
      } else if (index % 3 == 1) {
        // 左边的
        c.drawLine(right + (width / 2), top, right + (width / 2), bottom + (width / 2), paint); // 右边线条
        c.drawLine(left, bottom + (width / 2), right, bottom + (width / 2), paint); // 下边线条
      }
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    // Log.i("RecyclerViewDecoration", state+"");
    int childAdapterPosition = parent.getChildAdapterPosition(view);
    if ((childAdapterPosition + 1) % 3 == 0) {
      // 最右边的item
      // 只绘制下边
      outRect.set(0, 0, 0, width);
    } else if ((childAdapterPosition + 1) % 3 == 2) {
      // 中间
      // 绘制右线条+下线条
      outRect.set(0, 0, width, width);
    } else if ((childAdapterPosition + 1) % 3 == 1) {
      // 左边的
      // 绘制右边+下边的线条
      outRect.set(0, 0, width, width);
    }
  }
}
