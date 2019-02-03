package cn.microanswer.phonemp3.ui;

import android.app.Dialog;
import android.content.DialogInterface;

import cn.microanswer.phonemp3.logic.Logic;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;

public interface Page<L extends Logic> {

    /**
     * 返回Context
     *
     * @return
     */
    PhoneMp3Activity getPhoneMp3Activity();

    /**
     * 返回当前界面是否可见
     * @return
     */
    boolean mIsVisible();

    /**
     * 返回处理页面的逻辑器
     *
     * @return
     */
    L getLogic();

    /**
     * 弹出toast消息
     *
     * @param msg
     */
    void toast(String msg);

    /**
     * 弹出警告消息
     *
     * @param msg
     */
    Dialog alert(String msg);

    /**
     * 弹出询问框
     *
     * @param msg
     * @param onConfirmListener
     * @param onCancleListener
     */
    Dialog confirm(String msg, DialogInterface.OnClickListener onConfirmListener, DialogInterface.OnClickListener onCancleListener);
}
