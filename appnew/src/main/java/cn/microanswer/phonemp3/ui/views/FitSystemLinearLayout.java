package cn.microanswer.phonemp3.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import answer.android.phonemp3.R;

/**
 * 该LinearLayout有一下功能：
 * <p>
 * 当fitSystem设置为true时，本控件可选择是否应用fitsystem的效果。
 * ignoreTop=true --> 将不会再上方添一个状态栏高度的padding
 * ignoreBottom=true --> 将不会在下方添加一个导航栏高度的padding
 * topColor:ignoreTop=false的时候，顶部padding区域绘制的颜色
 * bottomColor: 同上
 * </p>
 */
public class FitSystemLinearLayout extends LinearLayout {

    private boolean ignoreTop, ignoreBottom, ignoreLeft, ignoreRight;
    private Rect inset;
    private int topColor, bottomColor, leftColor, rightColor;
    private Paint paint;

    public FitSystemLinearLayout(Context context) {
        super(context);
    }

    public FitSystemLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FitSystemLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attr) {
        Context c = getContext();

        TypedArray typedArray = c.obtainStyledAttributes(attr, R.styleable.FitSystemLinearLayout);

        ignoreTop = typedArray.getBoolean(R.styleable.FitSystemLinearLayout_ignoreTop, false);
        ignoreBottom = typedArray.getBoolean(R.styleable.FitSystemLinearLayout_ignoreBoottom, false);
        ignoreLeft = typedArray.getBoolean(R.styleable.FitSystemLinearLayout_ignoreLeft, false);
        ignoreRight = typedArray.getBoolean(R.styleable.FitSystemLinearLayout_ignoreRight, false);
        topColor = typedArray.getColor(R.styleable.FitSystemLinearLayout_topColor, 0xFFFFFFFF);
        bottomColor = typedArray.getColor(R.styleable.FitSystemLinearLayout_boottomColor, 0xFFFFFFFF);
        leftColor = typedArray.getColor(R.styleable.FitSystemLinearLayout_leftColor, 0xFFFFFFFF);
        rightColor = typedArray.getColor(R.styleable.FitSystemLinearLayout_rightColor, 0xFFFFFFFF);


        typedArray.recycle();

        if ((!ignoreTop) || (!ignoreBottom) || (!ignoreLeft) || (!ignoreRight)) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
        }

        // 没有设置背景的话，就设置一个背景， 没有背景就不会调用 draw 方法， 也不会调用 onDraw方法
        Drawable background = getBackground();
        if (null == background) {
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (insets != null) {
            inset = new Rect(insets);
            setPadding(ignoreLeft ? 0 : inset.left, ignoreTop ? 0 : inset.top, ignoreRight ? 0 : inset.right, ignoreBottom ? 0 : inset.bottom);
            return false;
        }
        return super.fitSystemWindows(null);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // WindowInsets windowInsets = super.onApplyWindowInsets(insets);
            inset = new Rect();
            computeSystemWindowInsets(insets, inset);
            setPadding(ignoreLeft ? 0 : inset.left, ignoreTop ? 0 : inset.top, ignoreRight ? 0 : inset.right, ignoreBottom ? 0 : inset.bottom);
            return insets;
        } else {
            return super.onApplyWindowInsets(insets);
        }
    }

    public void setBottomColor(int bottomColor) {
        this.bottomColor = bottomColor;
    }

    public void setLeftColor(int leftColor) {
        this.leftColor = leftColor;
    }

    public void setTopColor(int topColor) {
        this.topColor = topColor;
    }

    public void setRightColor(int rightColor) {
        this.rightColor = rightColor;
    }

    public void setIgnoreBottom(boolean ignoreBottom) {
        this.ignoreBottom = ignoreBottom;
    }

    public void setIgnoreLeft(boolean ignoreLeft) {
        this.ignoreLeft = ignoreLeft;
    }

    public void setIgnoreRight(boolean ignoreRight) {
        this.ignoreRight = ignoreRight;
    }

    public void setIgnoreTop(boolean ignoreTop) {
        this.ignoreTop = ignoreTop;
    }

    public int getBottomColor() {
        return bottomColor;
    }

    public int getLeftColor() {
        return leftColor;
    }

    public int getRightColor() {
        return rightColor;
    }

    public int getTopColor() {
        return topColor;
    }

    public boolean isIgnoreBottom() {
        return ignoreBottom;
    }

    public boolean isIgnoreLeft() {
        return ignoreLeft;
    }

    public boolean isIgnoreRight() {
        return ignoreRight;
    }

    public boolean isIgnoreTop() {
        return ignoreTop;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (paint != null && inset != null) {
            if (!ignoreTop) {
                int c = paint.getColor();
                paint.setColor(topColor);
                canvas.drawRect(0, 0, getWidth(), inset.top, paint);
                paint.setColor(c);
            }

            if (!ignoreBottom) {
                int c = paint.getColor();
                paint.setColor(bottomColor);
                canvas.drawRect(0, getHeight() - inset.bottom, getWidth(), getHeight(), paint);
                paint.setColor(c);
            }

            if (!ignoreLeft) {
                int c = paint.getColor();
                paint.setColor(leftColor);
                canvas.drawRect(0, 0, inset.left, getHeight(), paint);
                paint.setColor(c);
            }
            if (!ignoreRight) {
                int c = paint.getColor();
                paint.setColor(rightColor);
                canvas.drawRect(getWidth() - inset.right, 0, getWidth(), getWidth(), paint);
                paint.setColor(c);
            }
        }
    }
}
