package cn.microanswer.phonemp3.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.util.Utils;

public abstract class BaseDialog extends Dialog implements View.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    protected static int width = -1;

    /**
     * 标题
     */
    private String mDialogTitle;

    /**
     * 标题控件
     */
    protected TextView mTextViewTitle;

    /**
     * 取消按钮控件
     */
    private MaterialButton mButtonCancel;

    /**
     * 确定按钮控件
     */
    private MaterialButton mButtonSure;

    /**
     * 内容容器控件
     */
    protected FrameLayout mDialogContent;

    public BaseDialog(@NonNull Context context) {
        this(context, context.getString(R.string.tip));
    }


    public BaseDialog(@NonNull Context context, String dialogtitle) {
        super(context);
        if (TextUtils.isEmpty(dialogtitle)) {
            dialogtitle = context.getString(R.string.tip);
        }
        if (width <= 0) {
            width = Utils.UI.getScreenWidth(context);
            width = Math.round(width * (9f / 10f));
        }
        mDialogTitle = dialogtitle;
        setTitle(dialogtitle);
        setOnDismissListener(this);
        setOnCancelListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_base);

        mTextViewTitle = findViewById(R.id.mTextViewTitle);
        mDialogContent = findViewById(R.id.mDialogContent);
        mButtonCancel  = findViewById(R.id.buttonCancel);
        mButtonSure    = findViewById(R.id.buttonSure);

        if (!hasCancelBtn()) {
            mButtonCancel.setVisibility(View.INVISIBLE);
            mButtonCancel.setEnabled(false);
        }

        String btnSureTxt = getBtnSureTxt();
        if (!TextUtils.isEmpty(btnSureTxt)) {
            mButtonSure.setText(btnSureTxt);
        }

        String btnCancelTxt = getBtnCancelTxt();
        if (!TextUtils.isEmpty(btnCancelTxt)) {
            mButtonCancel.setText(btnCancelTxt);
        }

        mTextViewTitle.setText(mDialogTitle);
        mButtonSure.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mDialogContent.addView(getContentView(mDialogContent));
        setOnShowListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = BaseDialog.width;
            layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        }
    }


    @Override
    public final void onClick(View v) {
        boolean dis = true;
        if (v == mButtonCancel) {
            dis = onBtnCancelClick();
        } else if (v == mButtonSure) {
            dis = onBtnSureClick();
        } else {
            // 没有这个按钮
        }
        if (dis) {
            hide();
            cancel();
        }
    }

    /**
     * 返回弹出框内容的 view
     * @param parent 父容器
     * @return
     */
    protected abstract View getContentView(FrameLayout parent);

    /**
     * 返回true 则显示取消按钮
     * @return
     */
    protected boolean hasCancelBtn() {return true;}

    /**
     * 返回确认按钮文案
     * @return
     */
    protected String getBtnSureTxt() {
        return null;
    }

    /**
     * 返回取消按钮的文案。
     * @return
     */
    protected String getBtnCancelTxt() {
        return null;
    }


    // 返回 true 弹出框会消失，返回 false 则不会消失。
    protected boolean onBtnCancelClick() {
        return true;
    }
    // 返回 true 弹出框会消失，返回 false 则不会消失。
    protected boolean onBtnSureClick() {
        return true;
    }

    @Override
    public void onShow(DialogInterface dialog) { }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
        Log.i("onDismiss", this.toString());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dismiss();
        Log.i("onCancel", this.toString());
    }
}
