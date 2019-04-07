package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;

import androidx.appcompat.app.ActionBar;
import cn.microanswer.phonemp3.logic.TxtLogic;
import cn.microanswer.phonemp3.ui.TxtPage;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class TxtAnswer extends BaseAnswer<TxtPage> implements TxtLogic {

    private File txtFile;

    public TxtAnswer(TxtPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        if (txtFile == null) {
            getPage().showLoading();
            Task.TaskHelper.getInstance().run(new Task.ITask<Object, String>() {
                @Override
                public String run(Object param) throws Exception {
                    if (arguments != null) {
                        String filePath = arguments.getString("filePath");
                        if (TextUtils.isEmpty(filePath)) throw new Exception("必须指定要打开的文件：filePath");
                        txtFile = new File(filePath);
                        return Utils.COMMON.file2String(txtFile);
                    }
                    throw new Exception("必须指定要打开的文件：filePath");
                }

                @Override
                public void afterRun(String value) {
                    super.afterRun(value);
                    ActionBar supportActionBar = getPhoneMp3Activity().getSupportActionBar();
                    if (supportActionBar != null) {
                        supportActionBar.setTitle(txtFile.getName());
                    }
                    getPage().showTxt(value);
                }

                @Override
                public void onError(Exception e) {
                    super.onError(e);
                    Utils.UI.alert(getPhoneMp3Activity(), "出错", e.getMessage());
                }
            });
        }
    }

}
