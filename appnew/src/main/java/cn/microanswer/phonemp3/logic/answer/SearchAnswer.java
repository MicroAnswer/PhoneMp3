package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;

import cn.microanswer.phonemp3.logic.SearchLogic;
import cn.microanswer.phonemp3.ui.SearchPage;

public class SearchAnswer extends BaseAnswer<SearchPage> implements SearchLogic {
    public SearchAnswer(SearchPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {

    }
}
