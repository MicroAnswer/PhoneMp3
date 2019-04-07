package cn.microanswer.phonemp3.logic.answer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import cn.microanswer.phonemp3.logic.CodeUseLogic;
import cn.microanswer.phonemp3.ui.CodeUsePage;
import cn.microanswer.phonemp3.util.Task;

public class CodeUseAnswer extends BaseAnswer<CodeUsePage> implements CodeUseLogic {

    private String links[];

    public CodeUseAnswer(CodeUsePage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        getPage().showLoading();
        Task.TaskHelper.getInstance().run(new Task.ITask<Object, String[][]>() {
            @Override
            public String[][] run(Object param) throws Exception {
                return new String[][]{
                        {
                                "androidx.appcompat",
                                "com.android.support-v4",
                                "com.google.android.material",
                                "com.squareup.okhttp3",
                                "com.github.bumptech.glide",
                                "com.alibaba.fastjson",
                                "com.github.Raizlabs.DBFlow"
                        },
                        {
                                "New appcompat to replace support or more.",
                                "Make application more powerful.",
                                "Material make view possible.",
                                "Used for network.",
                                "Load img in most-bast-easy way.",
                                "From alibaba's json jar.",
                                "For bean to database."
                        },
                        {
                                "https://developer.android.google.cn/topic/libraries/support-library/revisions",
                                "https://developer.android.com/reference/android/support/v4/app/package-summary.html",
                                "https://developer.android.google.cn/reference/com/google/android/material/package-summary",
                                "https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp",
                                "https://mvnrepository.com/artifact/com.github.bumptech.glide/glide",
                                "https://mvnrepository.com/artifact/com.alibaba/fastjson",
                                "https://github.com/agrosner/DBFlow"
                        }
                };
            }

            @Override
            public void afterRun(String[][] value) {
                super.afterRun(value);
                getPage().showCodes(value[0], value[1]);
                links = value[2];
            }
        });
    }

    @Override
    public void onClickCodeUseItem(int position, String name, String desc) {
        Uri uri = Uri.parse(links[position]);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getPhoneMp3Activity().startActivity(intent);
    }
}
