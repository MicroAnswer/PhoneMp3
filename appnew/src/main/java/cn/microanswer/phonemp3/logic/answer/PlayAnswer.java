package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;

import cn.microanswer.phonemp3.logic.PlayLogic;
import cn.microanswer.phonemp3.ui.PlayPage;

public class PlayAnswer extends BaseAnswer<PlayPage> implements PlayLogic {
    public PlayAnswer(PlayPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

    }

    @Override
    public void onPause$Playclick() {
    }
}
