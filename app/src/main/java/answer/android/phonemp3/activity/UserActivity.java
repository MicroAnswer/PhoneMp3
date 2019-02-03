package answer.android.phonemp3.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import net.qiujuer.genius.blur.StackBlur;

import answer.android.phonemp3.R;

/**
 * 个人中心activity
 * Created by Microanswer on 2017/7/18.
 */

public class UserActivity extends BaseActivity implements View.OnClickListener {

  private View mRoot;
  private ImageView mUserHead, mUserBg;
  private TextView mUserName;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user);
    Toolbar toolBar = getToolBar();
    setSupportActionBar(toolBar);
    int c = getResources().getColor(R.color.colorPrimary);
    drawStateBarcolor(c);
    drawNavigationBarColor(c);

    mRoot = findViewById(R.id.activity_user_content);
    mUserBg = (ImageView) findViewById(R.id.activity_user_bg);
    mUserHead = (ImageView) findViewById(R.id.activity_user_head);
    mUserName = (TextView) findViewById(R.id.activity_user_name);
    findViewById(R.id.activity_user_setting).setOnClickListener(this);

    Glide.with(getApplicationContext())
            .load("file:///android_asset/userbg.jpg")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .bitmapTransform(new BitmapTransformation(this) {
              @Override
              protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
                return StackBlur.blurNatively(bitmap, 100, true);
              }

              @Override
              public String getId() {
                return UserActivity.class.getSimpleName();
              }
            })
            .into(mUserBg);

    // 加载背景
    // x.task().run(new Runnable() {
    //   @Override
    //   public void run() {
//
    //   }
    // });
  }


  /**
   * 设置按钮点击
   *
   * @param v
   */
  @Override
  public void onClick(View v) {

  }
}
