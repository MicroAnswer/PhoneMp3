package answer.android.phonemp3.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

import answer.android.phonemp3.R;
import answer.android.phonemp3.tool.Tool;
import answer.android.phonemp3.view.DownloadProgressButton;

/**
 * 新版app下载activity
 * Created by Micro on 2017-7-16.
 */

public class NewVersionDownloadActivity extends BaseActivity implements View.OnClickListener {
  private TextView mVersionTextView;
  private TextView mNewFunctionTextView;
  private DownloadProgressButton mDownloadProgressButton;
  private BaseDownloadTask mFileDownloader;
  private MFileDownloadListener mFileDownloadListener;
  private File f;

  private String size, name, url, newfunction, createat, updatedat, verson;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_newversion);
    Toolbar toolBar = getToolBar();
    setSupportActionBar(toolBar);
    drawStateBarcolor(getResources().getColor(R.color.colorPrimary));
    drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    setActionBarcontentShadow(8);

    Intent intent = getIntent();
    if (intent != null) {
      url = intent.getStringExtra("url");
      name = intent.getStringExtra("name");
      verson = intent.getStringExtra("version");
      newfunction = intent.getStringExtra("newfunction");
      size = intent.getStringExtra("size");
      updatedat = intent.getStringExtra("updateat");
      createat = intent.getStringExtra("createdat");
    } else {
      toast("no data.", DUR_SHORT);
      finish();
      return;
    }

    setTitle(name);
    mNewFunctionTextView = (TextView) findViewById(R.id.activity_newversiondownloadcontent_newfuncion);
    mVersionTextView = (TextView) findViewById(R.id.activity_newversiondownloadcontent_size);
    mDownloadProgressButton = (DownloadProgressButton) findViewById(R.id.progressbutton);
    mDownloadProgressButton.setOnClickListener(this);
    mDownloadProgressButton.setCurrentText("下载中");
    mDownloadProgressButton.setShowBorder(true);
    mDownloadProgressButton.setState(DownloadProgressButton.STATE_DOWNLOADING);

    f = new File(getNewVersionApkDir(), "phonemp3-" + verson + "-release.apk");

    // logger.info("下载链接:" + url);
    FileDownloader.setup(getApplicationContext());
    mFileDownloader = FileDownloader.getImpl().create(url);
    mFileDownloader.setPath(f.getAbsolutePath());
    mFileDownloadListener = new MFileDownloadListener();
    mFileDownloader.setListener(mFileDownloadListener);
    mFileDownloader.start();

    mVersionTextView.setText(getString(R.string.version) + verson + " - " + getString(R.string.size) + Tool.getNiceFileSize(Long.parseLong(size)));
    mNewFunctionTextView.setText(newfunction);

  }

  @Override
  public void onClick(View v) {
    if (mDownloadProgressButton.getState() == DownloadProgressButton.STATE_FINISH) {
      // 下载完成, 进行安装
      openApk(f);
    } else if (mDownloadProgressButton.getState() == DownloadProgressButton.STATE_PAUSE) {
      // 已暂停,恢复下澡
      mFileDownloader.reuse();
      mFileDownloader.start();
      mDownloadProgressButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
    } else if (mDownloadProgressButton.getState() == DownloadProgressButton.STATE_DOWNLOADING) {
      // 正在下载,点击暂停
      mFileDownloader.pause();
    }
  }

  // 打开apk
  private void openApk(File f) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri data;
    // 判断版本大于等于7.0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      data = FileProvider.getUriForFile(this, "answer.android.phonemp3", f);
      // 给目标应用一个临时授权
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    } else {
      data = Uri.fromFile(f);
    }
    intent.setDataAndType(data, "application/vnd.android.package-archive");
    startActivity(intent);
  }


  private class MFileDownloadListener extends FileDownloadListener {

    @Override
    protected void pending(BaseDownloadTask baseDownloadTask, int i, int i1) {
      // logger.info("pending:" + i + ", " + i1);
    }

    @Override
    protected void progress(BaseDownloadTask baseDownloadTask, int i, int i1) {
      //logger.info("progress:" + i + ", " + i1 + ", ismain = " + Tool.isMainThread());
      float percent = ((float) i) / i1;
      mDownloadProgressButton.setProgressText("下载中", percent * 100);

    }

    @Override
    protected void completed(BaseDownloadTask baseDownloadTask) {
      // logger.info("completed:");
      tip("下载完成");
      mDownloadProgressButton.setCurrentText("安装");
      mDownloadProgressButton.setState(DownloadProgressButton.STATE_FINISH);
    }

    @Override
    protected void paused(BaseDownloadTask baseDownloadTask, int i, int i1) {
      mDownloadProgressButton.setState(DownloadProgressButton.STATE_PAUSE);
      mDownloadProgressButton.setCurrentText(getString(R.string.paused));
      // logger.info("paused:" + i + ", " + i1);
    }

    @Override
    protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
      // logger.info("error:" + throwable.getMessage());
      Tool.alert(NewVersionDownloadActivity.this, "出错", throwable.getMessage());
    }

    @Override
    protected void warn(BaseDownloadTask baseDownloadTask) {
      // logger.info("warn:");
    }
  }
}
