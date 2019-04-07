package cn.microanswer.phonemp3.ui.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

// 用于显示文本的界面。
public class TxtActivity extends AppCompatActivity {

    private File txtFile;

    private TextView textview;

    private Dialog loadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 根据设置中保存的夜间模式和日间模式将界面变更为对应模式。
        if (SettingHolder.getSettingHolder().isDayMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // 在 android 5.0+ 设备上，当状态栏和导航栏都设置为不透明属性，然后设置其绘制颜色为透明颜色时
        // 没有任何效果[我将这个设置方式叫做A]，将状态栏设置为不透明属性，将导航栏设置为透明属性，
        // 然后设置状态栏的绘制颜色为透明 此时可以达到状态栏全透明，但是导航栏半透明的效果[我将这个设置方式叫做B]。
        // 为了达到导航栏也全透明，将设置方式重新改为A方式。然后下面的代码将让A设置全部生效。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_txt);

        textview = findViewById(R.id.textview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadDialog = ProgressDialog.show(this, null, "文件读取中...");


        Task.TaskHelper.getInstance().run(new Task.ITask<Object, String>() {
            @Override
            public String run(Object param) throws Exception {
                String filePath = getIntent().getStringExtra("filePath");
                txtFile = new File(filePath);
                return Utils.COMMON.file2String(txtFile);
            }

            @Override
            public void afterRun(String value) {
                super.afterRun(value);
                toolbar.setTitle(txtFile.getName());
                textview.setText(value);
                loadDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                Utils.UI.alert(TxtActivity.this, "出错", e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_txtactivity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 调用此方法快速打开文本文件。
    public static void open(String filePath, Context context) {
        Intent intent = new Intent();
        intent.putExtra("filePath", filePath);
        intent.setClass(context, TxtActivity.class);
        context.startActivity(intent);
    }
}
