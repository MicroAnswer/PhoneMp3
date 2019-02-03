package answer.android.phonemp3.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import answer.android.phonemp3.R;
import answer.android.phonemp3.api.API;
import answer.android.phonemp3.tool.Tool;
import uk.co.senab.photoview.PhotoView;

/**
 * 显示今日封面的activity
 * Created by Microanswer on 2017/7/14.
 */

public class CoverActivity extends BaseActivity implements View.OnClickListener, Runnable {

  private String url;
  private File mCoverFile;
  private PhotoView mPhotoView;
  private ProgressDialog mProgressDialog;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cover);
    drawStateBarcolor(getResources().getColor(R.color.colorPrimary));
    drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));

    Toolbar toolBar = getToolBar();
    setSupportActionBar(toolBar);
    setActionBarcontentShadow(8);

    // mCoverFile = Tool.downloadCover(null);
    mPhotoView = (PhotoView) findViewById(R.id.activity_cover_img);
    findViewById(R.id.activity_cover_btn).setOnClickListener(this);

    downloadCover();
  }

  private void downloadCover() {
    // 下载封面
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setMessage(getResources().getString(R.string.downloading));
      mProgressDialog.setCancelable(true);
      mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.hide), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          mProgressDialog.hide();
        }
      });
      mProgressDialog.show();
    } else {
      mProgressDialog.setMessage(getResources().getString(R.string.downloading));
      mProgressDialog.show();
    }

    // 请求接口获取最新的封面信息

    API.useApi(CoverActivity.this, R.string.GETCOVERINFO, new API.Config() {
      @Override
      public void success(JSONObject data) {
        try {
          if (data.getInt("code") == 200) {

            // 获取封面信息成功， 保存封面信息
            JSONObject data1 = data.getJSONObject("data");
            data1.put("coverinfodate", System.currentTimeMillis());
            getSharedPreferences().edit().putString("keylogoinfo", data1.toString()).apply();

            // 下载信息中的图片
            url = data1.getString("url");
            x.task().run(CoverActivity.this);

          } else {
            alert(data.toString());
          }
        } catch (Exception e) {
          // json错误
        }
      }

      @Override
      public void fail(Throwable e) {
        Tool.alert(CoverActivity.this, e.getMessage());
      }
    });

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
  }

  /**
   * 保存图片
   *
   * @param v
   */
  public void asdasdasd(View v) {
    if (mCoverFile == null) {
      Tool.alert(this, "刚才图片下载失败了，你要保存图片的话需要重新下载。", new Tool.OnClick() {
        @Override
        public String getBtnTxt() {
          return "下载";
        }

        @Override
        public void d0() {
          downloadCover();
        }
      }, new Tool.OnClick() {
        @Override
        public String getBtnTxt() {
          return "算了";
        }

        @Override
        public void d0() {
          tip("取消下载");
        }
      });
    } else {

      if (mProgressDialog == null) {
        mProgressDialog = ProgressDialog.show(this, null, (getString(R.string.saving)));
      } else {
        mProgressDialog.setMessage(getString(R.string.saving));
        mProgressDialog.show();
      }

      x.task().run(new Runnable() {
        @Override
        public void run() {
          Exception ee = null;
          File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
          Calendar calendar = Calendar.getInstance();
          final File f = new File(externalStoragePublicDirectory,
                  getString(R.string.app_name) +
                          "_cover_" + calendar.get(Calendar.YEAR) +
                          "-" + (calendar.get(Calendar.MONTH) + 1) +
                          "-" + calendar.get(Calendar.DAY_OF_MONTH) + ".jpg");
          FileInputStream fin = null;
          FileOutputStream fou = null;
          try {
            fin = new FileInputStream(mCoverFile);
            fou = new FileOutputStream(f);

            byte[] data = new byte[512];
            int datasize;

            while ((datasize = fin.read(data)) != -1) {
              fou.write(data, 0, datasize);
            }

            fou.flush();

          } catch (Exception e) {
            ee = e;
          } finally {
            try {
              if (fin != null) {
                fin.close();
              }
              if (fou != null) {
                fou.close();
              }
            } catch (IOException e) {
              e.printStackTrace();
            }

            if (ee != null) {
              final Exception finalEe = ee;
              x.task().post(new Runnable() {
                @Override
                public void run() {
                  mProgressDialog.hide();
                  alert(Tool.Exception2String(finalEe));
                }
              });
            } else {
              x.task().post(new Runnable() {
                @Override
                public void run() {
                  mProgressDialog.hide();
                  alert("已保存到：" + f.getAbsolutePath());
                }
              });
            }
          }
        }
      });
    }
  }

  @Override
  public void onClick(View v) {
    asdasdasd(v);
  }

  private Runnable downloadCoverFileOk = new Runnable() {
    @Override
    public void run() {
      if (mCoverFile != null) {
        if (mProgressDialog != null) {
          mProgressDialog.hide();
          Glide.with(getApplicationContext()).load(mCoverFile).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
        }
      }
    }
  };

  @Override
  public void run() {
    if (!TextUtils.isEmpty(url)) {
      mCoverFile = Tool.downloadCover(url);
      x.task().post(downloadCoverFileOk);
    }
  }
}
