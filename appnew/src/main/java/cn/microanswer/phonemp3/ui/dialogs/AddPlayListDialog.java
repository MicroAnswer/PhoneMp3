package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.Task;

public class AddPlayListDialog extends BaseDialog {

    Logger logger = Logger.getLogger(AddPlayListDialog.class);

    private LayoutInflater inflater;
    private TextInputEditText editText;
    private TextInputLayout mTextInputLayout;
    private OnSureCreatePlayList onSureCreatePlayList;

    public AddPlayListDialog(@NonNull Context context) {
        super(context, "创建新歌单");
        inflater = LayoutInflater.from(context);
        setCanceledOnTouchOutside(true);
    }

    public AddPlayListDialog setOnSureCreatePlayList(OnSureCreatePlayList onSureCreatePlayList) {
        this.onSureCreatePlayList = onSureCreatePlayList;
        return this;
    }

    @Override
    protected View getContentView(FrameLayout parent) {
        return inflater.inflate(R.layout.dialog_addplaylist, parent, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editText = findViewById(R.id.edittextListName);
        mTextInputLayout = findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setCounterMaxLength(10);
    }

    public void onShow(DialogInterface dialog) {
        editText.requestFocus();
        Task.TaskHelper.getInstance().runAfter(() -> {
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean b = manager.showSoftInput(editText, 0);
            // logger.i("打开软键盘结果：" + b);
        }, 200);
    }

    @Override
    protected boolean onBtnSureClick() {
        String s = editText.getText().toString();
        if (TextUtils.isEmpty(s)) {
            // Toast.makeText(getContext(), "请输入歌单名", Toast.LENGTH_SHORT).show();
            // mTextInputLayout.setHelperText("请输入歌单名");
            mTextInputLayout.setError("请输入歌单名");
            Task.TaskHelper.getInstance().runAfter(() -> mTextInputLayout.setError(null), 3000);
            return false;
        } else {
            if (onSureCreatePlayList != null) {
                onSureCreatePlayList.save(s, this);
            }
        }
        return true;
    }

    /**
     * 当点击确定创建时，这个回调函数吊起。
     */
    public interface OnSureCreatePlayList {
        void save(String name, AddPlayListDialog dialog);
    }
}
