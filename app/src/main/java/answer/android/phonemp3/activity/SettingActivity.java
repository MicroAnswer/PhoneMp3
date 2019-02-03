package answer.android.phonemp3.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import answer.android.phonemp3.R;

/**
 * 设置界面
 * Created by Microanswer on 2017/7/13.
 */

public class SettingActivity extends BaseActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    drawStateBarcolor(getResources().getColor(R.color.colorPrimary));
    drawNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    setContentView(R.layout.activity_set);
    Toolbar toolBar = getToolBar();
    setSupportActionBar(toolBar);
    setActionBarcontentShadow(8);
  }
}
