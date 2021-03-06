package cn.microanswer.phonemp3.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.util.Utils;

/**
 * 分组
 * Created by Microanswer on 2018/1/11.
 */

public class Group extends LinearLayout {

    private TextView titleView; // 展示标题的View
    private int titleColor;

    public Group(Context context) {
        super(context);
        init(context, null);
    }

    public Group(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Group(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context c, AttributeSet attributeSet) {

        // 初始化标题View
        titleView = new TextView(c);
        int paddingTop = Utils.UI.dp2px(c, 10);
        int paddingLeft = Utils.UI.dp2px(c, 15);
        int paddingRight = Utils.UI.dp2px(c, 10);
        int paddingBottom = Utils.UI.dp2px(c, 4);
        titleView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        titleView.setBackgroundColor(Color.parseColor("#F2F2F2"));
        titleView.setMaxLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 添加到 LinearLayout
        addView(titleView, 0);

        // 固定使用竖向布局
        setOrientation(VERTICAL);
        if (attributeSet != null) {

            TypedArray typedArray = c.obtainStyledAttributes(attributeSet, R.styleable.Group);

            if (typedArray != null) {

                String string = typedArray.getString(R.styleable.Group_title);

                if (!TextUtils.isEmpty(string)) {
                    titleView.setText(string);
                }

                Drawable drawable = typedArray.getDrawable(R.styleable.Group_titleBackground);

                if (drawable != null) {
                    titleView.setBackgroundDrawable(drawable);
                }

                int color = typedArray.getColor(R.styleable.Group_titleColor, Color.parseColor("#F2F2F2"));
                titleView.setTextColor(color);

                typedArray.recycle();
            }
        }

    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        titleView.setTextColor(titleColor);
    }
}
