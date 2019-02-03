package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class ReNamePlayListDialog extends BaseDialog implements View.OnClickListener {

    private TextInputLayout mTextInputLayout;
    private TextInputEditText edittextListName;

    private PlayList mPlayList;
    private ReNameListener mReNameListener;

    public ReNamePlayListDialog(@NonNull Context context, PlayList playList) {
        super(context, context.getString(R.string.rename));
        mPlayList = playList;
    }

    public void setReNameListener(ReNameListener mReNameListener) {
        this.mReNameListener = mReNameListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextInputLayout = findViewById(R.id.mTextInputLayout);
        edittextListName = findViewById(R.id.edittextListName);
    }

    public void onShow(DialogInterface dialog) {
        edittextListName.requestFocus();
        Task.TaskHelper
                .getInstance()
                .runAfter(
                        () -> (
                                (InputMethodManager) getContext()
                                        .getSystemService(Context.INPUT_METHOD_SERVICE)
                        ).showSoftInput(edittextListName, 0),
                        200);
    }

    private void saveResult() {
        // 获取输入的新名称
        String newName = edittextListName.getText().toString().trim();
        try {
            Utils.COMMON.nullThrow(newName, "请输入新歌单名");
        } catch (Exception e) {
            mTextInputLayout.setError(e.getMessage());
            Task.TaskHelper.getInstance().runAfter(() -> mTextInputLayout.setError(null), 3000);
            return;
        }
        boolean result = true;
        // 新名称和旧名称相同。直接不处理，提示成功
        if (!newName.equals(mPlayList.getRamark())) {
            // 保存。
            mPlayList.setRamark(newName);
            result = mPlayList.update();
            if (mReNameListener != null) {
                mReNameListener.onReNamed(newName);
            }
        }
        Toast.makeText(getContext(), "操作" + (result ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    protected boolean onBtnSureClick() {
        saveResult();
        return false;
    }

    @Override
    protected View getContentView(FrameLayout parent) {
        return getLayoutInflater().inflate(R.layout.dialog_renameplaylist, parent, false);
    }

    public interface ReNameListener {
        void onReNamed(String newName);
    }
}
