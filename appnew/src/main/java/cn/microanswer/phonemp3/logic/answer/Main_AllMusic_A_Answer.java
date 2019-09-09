package cn.microanswer.phonemp3.logic.answer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cn.microanswer.phonemp3.entity.Ablum;
import cn.microanswer.phonemp3.logic.Main_AllMusic_A_Logic;
import cn.microanswer.phonemp3.ui.Main_AllMucis_A_Page;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class Main_AllMusic_A_Answer extends BaseAnswer<Main_AllMucis_A_Page> implements Main_AllMusic_A_Logic {
    private List<Ablum> ablums;

    public Main_AllMusic_A_Answer(Main_AllMucis_A_Page page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        if (ablums == null || ablums.size() == 0) {
            loadAblumsAsync();
        } else {
            // 直接显示数据。
            display(ablums);
        }
    }


    private void loadAblumsAsync() {
        Task.TaskHelper.getInstance().run(new Task.ITask<Context, List<Ablum>>() {
            @Override
            public Context getParam() {
                return getPhoneMp3Activity();
            }

            @Override
            public List<Ablum> run(Context context) throws Exception {
                return Utils.APP.getAllAblum(context);
            }

            @Override
            public void afterRun(List<Ablum> value) {
                super.afterRun(value);
                    display(value);
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                Log.e("Microanswer", "加载专辑出错", e);
            }
        });
    }

    private void display(List<Ablum> ablums) {
        this.ablums = ablums;
        if (this.ablums != null) {
            getPage().displayAblums(this.ablums);
        }

        // Log.i("Microanswer", "显示专辑列表：" + this.ablums);
    }
}
