package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

public class ConfirmDialog extends AlertDialog {

    private DialogInterface.OnClickListener onClickListener;
    private String btnSureTxt;

    public ConfirmDialog(@NonNull Context context, String msg) {
        super(context, msg);
    }

    public ConfirmDialog(@NonNull Context context, String msg, String btnSureTxt) {
        super(context, null, msg);
        this.btnSureTxt = btnSureTxt;
    }

    /**
     * 设置点击确定按钮时的回调。
     *
     * @param onClickListener
     * @return
     */
    public ConfirmDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    @Override
    public String getBtnSureTxt() {
        return btnSureTxt;
    }

    @Override
    protected boolean hasCancelBtn() {
        return true;
    }

    @Override
    protected boolean onBtnSureClick() {
        if (onClickListener != null) {
            onClickListener.onClick(this, BUTTON_POSITIVE);
        }
        return super.onBtnSureClick();
    }
}
