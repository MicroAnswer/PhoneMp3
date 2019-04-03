package cn.microanswer.phonemp3.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.WebLogic;
import cn.microanswer.phonemp3.logic.answer.WebAnswer;
import cn.microanswer.phonemp3.ui.WebPage;

public class WebFragment extends BaseFragment<WebLogic> implements WebPage {

    private WebView webView;
    private LinearLayout linearLayoutLoadingView;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    WebLogic newLogic() {
        return new WebAnswer(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = findViewById(R.id.webView);
        linearLayoutLoadingView = findViewById(R.id.linearLayoutLoadingView);
        toolbar = findViewById(R.id.toolbar);

        getPhoneMp3Activity().setSupportActionBar(toolbar);
        ActionBar actionBar = getPhoneMp3Activity().getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initWebView();

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    // 初始化 webView
    private void initWebView() {

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add = menu.add(0, R.id.refresh, 0, R.string.refresh);
        add.setIcon(R.drawable.icon_refresh);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getPhoneMp3Activity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.refresh) {
            webView.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        getPhoneMp3Activity().setSupportActionBar(null);
        setHasOptionsMenu(false);
        super.onDestroy();
    }

    @Override
    public boolean handleOnBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            linearLayoutLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            String title = view.getTitle();
            if (TextUtils.isEmpty(title)) {
                title = getString(R.string.app_name);
            }
            toolbar.setTitle(title);

            linearLayoutLoadingView.setVisibility(View.GONE);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

    }
}
