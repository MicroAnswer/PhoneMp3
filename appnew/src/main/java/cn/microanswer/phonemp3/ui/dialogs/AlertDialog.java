package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import cn.microanswer.phonemp3.util.Utils;

public class AlertDialog extends BaseDialog {
    private String msg;

    public AlertDialog(@NonNull Context context, String msg) {
        super(context);
        this.msg = msg;
    }

    public AlertDialog(@NonNull Context context, String dialogtitle, String msg) {
        super(context, dialogtitle);
        this.msg = msg;
    }

    @Override
    protected View getContentView(FrameLayout parent) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F);
        textView.setTextColor(Color.BLACK);
        int i = Utils.UI.dp2px(getContext(), 5F);
        int lf = Utils.UI.dp2px(getContext(), 15F);
        textView.setPadding(lf, 0, lf, i);
        textView.setText(msg);

        return textView;
    }

    @Override
    protected boolean hasCancelBtn() {
        return false;
    }
}
