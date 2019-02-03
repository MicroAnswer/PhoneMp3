package answer.android.phonemp3.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import answer.android.phonemp3.R;

/**
 * webView界面,
 * Created by Micro on 2017/6/17.
 */

public class WebViewActivity extends BaseActivity {

  private WebView webView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webview);
    //suitStatusBar();
    drawStateBarcolor(getResources().getColor(R.color.colorPrimary));
    drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    setSupportActionBar(getToolBar());

    String title = getIntent().getStringExtra("title");

    if (TextUtils.isEmpty(title)) {
      title = getString(R.string.app_name);
    }

    getSupportActionBar().setTitle(title);
    setActionBarcontentShadow(8);

    webView = (WebView) findViewById(R.id.activity_about_webview);
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setAllowContentAccess(true);
    settings.setAllowFileAccess(true);
    settings.setAllowFileAccessFromFileURLs(true);
    settings.setAllowUniversalAccessFromFileURLs(true);
    settings.setAppCacheEnabled(true);
    settings.setGeolocationEnabled(true);
    webView.setWebChromeClient(webchromclient);
    webView.setWebViewClient(webviewclient);


    String url = getIntent().getStringExtra("url");

    if (!TextUtils.isEmpty(url)) {
      webView.loadUrl(url);
    } else {
      // 没有指定url,显示空
      LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_about_webviewcontent);
      linearLayout.removeAllViews();
      linearLayout.addView(View.inflate(this, R.layout.view_emptyview, null));
    }
  }

  private WebViewClient webviewclient = new WebViewClient() {
  };

  private WebChromeClient webchromclient = new WebChromeClient() {
    // 页面图标
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
      // if (icon != null) {
      //   getSupportActionBar().setIcon(new BitmapDrawable(icon));
      // }
      // 不显示页面图标
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
      if(TextUtils.isEmpty(title)) {
        title = "";
      }
      getSupportActionBar().setTitle(title);
    }
  };

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();// 返回前一个页面
      return;
    }
    super.onBackPressed();
  }
}
