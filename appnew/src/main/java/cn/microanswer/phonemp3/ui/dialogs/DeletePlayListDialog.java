package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import androidx.annotation.NonNull;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.Music_Table;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.util.Task;

public class DeletePlayListDialog extends BaseDialog implements View.OnClickListener {

    private PlayList mPlayList;
    private LayoutInflater inflater;

    private TextView mTextView;
    private CheckBox mCheckBox;

    private DeleteListener mDeleteListener;

    public DeletePlayListDialog(@NonNull Context context, PlayList playList) {
        super(context);
        this.inflater = LayoutInflater.from(context);
        this.mPlayList = playList;
    }

    public void setDeleteListener(DeleteListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    private void doDelete() {

        final boolean deleteMusicInPlayList = mCheckBox.isChecked();

        Task.TaskHelper.getInstance().run(new Task.ITask<PlayList, Long>() {
            @Override
            public PlayList getParam() {
                return mPlayList;
            }

            @Override
            public Long run(PlayList param) throws Exception {

                if (deleteMusicInPlayList) {
                    long i = SQLite.delete(Music.class)
                            .where(Music_Table.list_id.eq(param.getId()))
                            .executeUpdateDelete();
                    param.delete();
                    return i;
                } else {
                    param.delete();
                    return 1L;
                }
            }

            @Override
            public void afterRun(Long value) {
                super.afterRun(value);
                dismiss();
                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                if (mDeleteListener != null) {
                    mDeleteListener.onDeleted();
                }
            }
        });

    }

    @Override
    protected View getContentView(FrameLayout parent) {
        View v = inflater.inflate(R.layout.dialog_deleteplaylist, parent, false);
        mTextView = v.findViewById(R.id.textview);
        mCheckBox = v.findViewById(R.id.checkbox);

        String s = mTextView.getText().toString();
        mTextView.setText(String.format(s, mPlayList.getRamark()));

        return v;
    }

    @Override
    protected boolean onBtnSureClick() {
        doDelete();
        return super.onBtnSureClick();
    }

    public interface DeleteListener {
        void onDeleted();
    }
}
