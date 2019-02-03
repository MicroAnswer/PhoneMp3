package answer.android.phonemp3.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;
import org.xutils.x;

import answer.android.phonemp3.R;
import answer.android.phonemp3.api.API;

/**
 * 程序一打开就会显示这个activity.
 * 该activity设定要完成以下功能:
 * 1. 检查更新app
 * <p>
 * Created by Micro on 2017/6/12.
 */

public class LogoActivity extends BaseActivity implements Runnable {

  public static final String KEY_LOGOINFO = "keylogoinfo";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_logo);

    drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    ImageView ccoovveerr = (ImageView) findViewById(R.id.ccoovveerr);


    if (shouldShowCover()) {

      // 显示封面, 先不管有没有封面.
      showcover(ccoovveerr);

      // 验证并下载最新的封面信息
      checkAndDownloadNewCoverInfo();


      // 停留在Logo界面4秒钟
      x.task().postDelayed(new Runnable() {
        @Override
        public void run() {
          finish();
          go2Activity(MainActivity.class);
        }
      }, getResources().getInteger(R.integer.logotime));

    } else {
      // 直接跳转
      go2Activity(MainActivity.class);
      finish();
    }
  }

  @Override
  public void onBackPressed() {
    //  super.onBackPressed();
    //　屏蔽返回按钮
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    API.close();
  }

  // 验证并下载最新的封面信息
  private void checkAndDownloadNewCoverInfo() {
    x.task().run(this);
  }

  //
  private void showcover(ImageView ccoovv) {
    SharedPreferences sharedPreferences = getSharedPreferences();
    String string = sharedPreferences.getString(KEY_LOGOINFO, "");
    if (!TextUtils.isEmpty(string)) {
      // 有封面信息
      try {
        JSONObject jo = new JSONObject(string);
        Glide.with(getApplicationContext())
                .load(jo.getString("url"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ccoovv);
      } catch (Exception e) {
        toast(e.getMessage(), DUR_SHORT);
      }
    }
  }

  @Override
  public void run() {

    final String COVER_INFO_DATE = "coverinfodate"; // 封面信息中上一次的保存封面的时间

    boolean shouldDownloadNewInfo = false; // 标记是否应该下载新的封面信息

    SharedPreferences sharedPreferences = getSharedPreferences();
    String string = sharedPreferences.getString(KEY_LOGOINFO, "");
    if (TextUtils.isEmpty(string)) {
      // 封面信息为空，这个时候憋憋要下载新的封面信息了。
      shouldDownloadNewInfo = true;
    } else {
      // 封面信息不为空， 判断保存封面信息的日期
      try {
        JSONObject infoJson = new JSONObject(string);
        if (infoJson.has(COVER_INFO_DATE)) {
          long lastCoverInfoDate = infoJson.getLong(COVER_INFO_DATE);
          long currentDateTime = System.currentTimeMillis();
          // 上次下载封面的时间已经超过了1天了。尝试再次下载新的封面信息
          shouldDownloadNewInfo = currentDateTime - lastCoverInfoDate >= 24 * 3600 * 1000;
        } else {
          shouldDownloadNewInfo = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
        // 如果构建json对象出错了，那么也视为没有封面信息，要下载新的封面信息。
        shouldDownloadNewInfo = true;
      }
    }

    if (shouldDownloadNewInfo) {
      API.useApi(this, R.string.GETCOVERINFO, new API.Config() {
        @Override
        public void success(JSONObject data) {
          try {
            if (data.getInt("code") == 200) {

              // 获取封面信息成功， 保存封面信息
              JSONObject data1 = data.getJSONObject("data");
              data1.put(COVER_INFO_DATE, System.currentTimeMillis());
              getSharedPreferences().edit().putString(KEY_LOGOINFO, data1.toString()).apply();
            } else {
              toast(data.getString("msg"), DUR_SHORT);
            }
          } catch (Exception e) {
            // json错误
          }
        }

        @Override
        public void fail(Throwable e) {
          toast(e.getMessage(), DUR_SHORT);
        }
      });

    }
  }


  //　是否应该显示封面
  private boolean shouldShowCover() {
    // 取到上次退出app的时间
    long lastExit = getSharedPreferences().getLong("lastExit", 0L);
    boolean isplaying = getSharedPreferences().getBoolean("isplaying", false);

    // 如果正在播放,不应该显示封面,直接跳转到主界面
    if (isplaying) {
      return false;
    }

    // 上次退出时间距离现在有5分钟之久,要求显示cover
    return System.currentTimeMillis() - lastExit > 5 * 60 * 1000;
  }
}
