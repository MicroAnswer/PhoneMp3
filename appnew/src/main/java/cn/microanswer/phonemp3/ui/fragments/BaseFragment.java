package cn.microanswer.phonemp3.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.Logic;
import cn.microanswer.phonemp3.ui.Page;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;
import cn.microanswer.phonemp3.util.Logger;

public abstract class BaseFragment<L extends Logic> extends Fragment implements OnBackPressedCallback,Page<L> {

    protected Logger logger = null;
    private L logic;

    private boolean isPasued = false;
    private boolean isStoped = false;
    private boolean isDestoryed = false;

    public BaseFragment() {
        logger = Logger.getLogger(getClass());
    }

    /**
     * 根据 id 获取对应的View
     *
     * @param id
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        View view = getView();
        if (view == null) {
            throw new RuntimeException("view is null");
        }
        return getView().findViewById(id);
    }

    abstract L newLogic();

    public L getLogic() {
        if (logic == null) {
            logic = newLogic();
        }
        return logic;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPasued = false;
        isStoped = false;
        isDestoryed = false;
        getLogic().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPasued = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isStoped = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestoryed = true;
    }

    public boolean mIsVisible() {
        boolean visible = super.isVisible();
        return visible && !isDestoryed && !isStoped && !isPasued;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View v = getView();
            if (v == null) {
                logger.e("在 onViewCreated 方法中，请求 requestFitSystemWindows 时， getView() 返回 null.");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    v.requestApplyInsets();
                } else {
                    v.requestFitSystemWindows();
                }
            }
        } else {
            logger.w("Android SDK 版本过低，版本号：" + Build.VERSION.SDK_INT + ", 没有方法：View.requestFitSystemWindows()");
        }
    }
    @Override
    public boolean handleOnBackPressed() {
        return false;
    }
    @Override
    public Dialog alert(String msg) {
        if (isDestoryed) {
            return null;
        }

        Dialog dialog = new AlertDialog.Builder(getPhoneMp3Activity())
                .setTitle(R.string.alert)
                .setMessage(msg)
                .setPositiveButton(R.string.sure, null)
                .create();
        Window window = dialog.getWindow();
        if (window != null) {
            // 设置弹出动画
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        }
        dialog.show();
        return dialog;
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(getPhoneMp3Activity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Dialog confirm(String msg, DialogInterface.OnClickListener onConfirmListener, DialogInterface.OnClickListener onCancleListener) {
        if (isDestoryed) {
            return null;
        }
        Dialog dialog = new AlertDialog.Builder(getPhoneMp3Activity())
                .setTitle(R.string.alert)
                .setMessage(msg)
                .setPositiveButton(R.string.sure, onConfirmListener)
                .setNegativeButton(R.string.cancel, onCancleListener)
                .create();
        Window window = dialog.getWindow();
        if (window != null) {
            // 设置弹出动画
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        }
        dialog.show();
        return dialog;
    }

    @Override
    public PhoneMp3Activity getPhoneMp3Activity() {
        return (PhoneMp3Activity) getContext();
    }
}
