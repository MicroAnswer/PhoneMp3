package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;

import cn.microanswer.phonemp3.logic.IntroductionLogic;
import cn.microanswer.phonemp3.ui.IntroductionPage;
import cn.microanswer.phonemp3.ui.fragments.MainFragment;
import cn.microanswer.phonemp3.util.SettingHolder;

public class IntroductionAnswer extends BaseAnswer<IntroductionPage> implements IntroductionLogic {
    public IntroductionAnswer(IntroductionPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle argments) {

    }

    @Override
    public void jumpToMain() {
        getPhoneMp3Activity().replace(MainFragment.class);

        SettingHolder.getSettingHolder().setDisplayIntroduction(false); // 点击了引导界面的确认， 下次不再显示引导界面
    }
}
