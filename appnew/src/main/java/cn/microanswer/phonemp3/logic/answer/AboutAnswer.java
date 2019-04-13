package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;

import answer.android.phonemp3.BuildConfig;
import cn.microanswer.phonemp3.logic.AboutLogic;
import cn.microanswer.phonemp3.ui.AboutPage;

public class AboutAnswer extends BaseAnswer<AboutPage> implements AboutLogic {
    public AboutAnswer(AboutPage page) {
        super(page);
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public void onItemClick(int id) {

    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getPage().displayVersion(getVersion());
    }
}
