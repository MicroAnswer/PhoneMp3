package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.text.TextUtils;

import cn.microanswer.phonemp3.logic.WebLogic;
import cn.microanswer.phonemp3.ui.WebPage;

public class WebAnswer extends BaseAnswer<WebPage> implements WebLogic {
    public WebAnswer(WebPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle argments) {
        String url = argments.getString("url");
        if (!TextUtils.isEmpty(url)) {
            getPage().loadUrl(url);
        }
    }
}
