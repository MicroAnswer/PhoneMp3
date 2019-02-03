package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import cn.microanswer.phonemp3.logic.ScannLogic;
import cn.microanswer.phonemp3.ui.ScannPage;

public class ScannAnswer extends BaseAnswer<ScannPage> implements ScannLogic {

    private Handler handler;

    public ScannAnswer(ScannPage page) {
        super(page);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

    }

    @Override
    public void onScannClick() {

    }

    private void refresh() {


    }
}
