package cn.microanswer.phonemp3.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.DrawableCompat;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.util.Utils;

/**
 * 一个Cell
 * Created by Microanswer on 2018/1/11.
 */

public class Cell extends LinearLayout {

    private HeadHelper headHelper;
    private LinearLayout content;

    private Paint paint;
    private int underLine = 1; // 下划线高度
    private int underLineColor = Color.parseColor("#A0A0A0");

    public Cell(Context context) {
        super(context);
        init(context, null);
    }

    public Cell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Cell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setClickable(true);

        headHelper = new HeadHelper(context, attrs).setOngetTypedArray(new _onGetTypedArray() {
            @Override
            public void d0(TypedArray typedArray) {
                underLine = typedArray.getDimensionPixelSize(R.styleable.Cell_underLine, 1);
                underLineColor = typedArray.getColor(R.styleable.Cell_underLineColor, underLineColor);
                // Toast.makeText(getContext(), "下划线高度：" + underLine + ", 颜色：" + underLineColor, Toast.LENGTH_SHORT).show();
            }
        }).init(context, attrs);

        content = new LinearLayout(context);
        content.setOrientation(VERTICAL);
        content.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        content.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        setGravity(Gravity.CENTER_VERTICAL);

        // 如果没有背景的话设置一个透明背景。
        Drawable background = getBackground();
        if (null == background) {
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (attrs == null) {
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            addView(headHelper);
            addView(content);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // xml中的定义全部加载完成后， 取出来
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new RuntimeException("Cell只能容纳1个子View");
        }
        if (childCount == 1) {
            View view = getChildAt(0);
            removeView(view);
            addView(headHelper);
            content.addView(view);
            addView(content);
        } else {
            addView(headHelper);
            addView(content);
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setIcon(Drawable drawable) {
        headHelper.drawableIcon = drawable;
        headHelper.iconView.setImageDrawable(drawable);
    }

    public void setTitle(String title) {
        headHelper.titleView.setText(title);
    }

    public void setDesc(String desc) {
        headHelper.descView.setText(desc);
    }

    public ImageView getIconImgView() {
        return headHelper.iconView;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (underLine > 0) {
            float w = paint.getStrokeWidth();
            int c = paint.getColor();
            paint.setColor(underLineColor);
            paint.setStrokeWidth(underLine);
            int y = getHeight() - Math.round(underLine / 2);
            canvas.drawLine(headHelper.getXOffsetAfterIcon(), y, getWidth() - getPaddingRight(), y, paint);
            paint.setColor(c);
            paint.setStrokeWidth(w);
        }
    }

    public void setIconColor(int iconColor) {
        if (this.headHelper.drawableIcon == null) {
            return;
        }
        Drawable drawable1 = DrawableCompat.wrap(this.headHelper.drawableIcon).mutate();
        DrawableCompat.setTint(drawable1,iconColor);
        this.headHelper.iconView.setImageDrawable(drawable1);
    }

    /**
     * 处理了： icon， 标题， 标题下的描述 这些逻辑
     *
     * @ hide
     */
    private class HeadHelper extends LinearLayout {
        private AppCompatImageView iconView;

        private LinearLayout titleDescView;
        private Drawable drawableIcon;
        private TextView titleView;
        private TextView descView;

        private _onGetTypedArray onGetTypedArray;

        public HeadHelper(Context context) {
            super(context);
        }

        public HeadHelper(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }

        public HeadHelper setOngetTypedArray(_onGetTypedArray ongetTypedArray) {
            this.onGetTypedArray = ongetTypedArray;
            return this;
        }

        private HeadHelper init(Context context, AttributeSet attrs) {
            setBackgroundDrawable(null);
            // 图标
            iconView = new AppCompatImageView(context);
            iconView.setMaxWidth(Utils.UI.dp2px(context, 40));
            iconView.setLayoutParams(new LayoutParams(Utils.UI.dp2px(context, 40), Utils.UI.dp2px(context, 40)));
            addView(iconView, 0);

            // 标题和标题下的文字 容器
            titleDescView = new LinearLayout(context);
            titleDescView.setOrientation(VERTICAL);
            titleDescView.setGravity(Gravity.CENTER_VERTICAL);
            int pd = Utils.UI.dp2px(context, 6);
            titleDescView.setPadding(pd, 0, 0, 0);

            // 标题
            titleView = new AppCompatTextView(context);
            titleView.setMaxLines(1);
            titleView.setSingleLine();
            titleView.setTextSize(16f);
            titleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            titleView.setMarqueeRepeatLimit(-1);
            titleView.setSelected(true);
            titleDescView.addView(titleView);

            // 描述
            descView = new AppCompatTextView(context);
            descView.setMaxLines(2);
            descView.setTextColor(Color.GRAY);
            descView.setTextSize(12f);
            descView.setEllipsize(TextUtils.TruncateAt.END);
            titleDescView.addView(descView);

            addView(titleDescView);


            int padding = Utils.UI.dp2px(context, 6);
            setPadding(0, padding, padding, padding);
            setGravity(Gravity.CENTER_VERTICAL);


            if (attrs != null) {

                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Cell);

                if (typedArray != null) {
                    drawableIcon = typedArray.getDrawable(R.styleable.Cell_icon);
                    if (null == drawableIcon) {
                        iconView.setImageBitmap(null);
                    } else {
                        iconView.setImageDrawable(drawableIcon);
                    }

                    int paddingi = typedArray.getDimensionPixelSize(R.styleable.Cell_iconPadding, Utils.UI.dp2px(context, 5));
                    iconView.setPadding(paddingi, paddingi, paddingi, paddingi);

                    String title = typedArray.getString(R.styleable.Cell_titLe);
                    if (!TextUtils.isEmpty(title)) {
                        titleView.setText(title);
                    }

                    int color = typedArray.getColor(R.styleable.Cell_titLeColor, titleView.getCurrentTextColor());
                    titleView.setTextColor(color);

                    String desc = typedArray.getString(R.styleable.Cell_desc);
                    if (!TextUtils.isEmpty(desc)) {
                        descView.setText(desc);
                    } else {
                        titleDescView.removeView(descView);
                    }
                    if (onGetTypedArray != null) {
                        onGetTypedArray.d0(typedArray);
                    }

                    typedArray.recycle();
                }
            }
            return this;
        }

        public int getXOffsetAfterIcon() {

            if (iconView != null) {
                return getPaddingLeft() + iconView.getWidth() + iconView.getPaddingLeft() + iconView.getPaddingRight() + Utils.UI.dp2px(getContext(), 6);
            } else {
                return getPaddingLeft();
            }
        }


    }

    private interface _onGetTypedArray {
        void d0(TypedArray typedArray);
    }
}
