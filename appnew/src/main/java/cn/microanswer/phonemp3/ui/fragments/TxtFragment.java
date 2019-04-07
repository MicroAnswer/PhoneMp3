package cn.microanswer.phonemp3.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.TxtLogic;
import cn.microanswer.phonemp3.logic.answer.TxtAnswer;
import cn.microanswer.phonemp3.ui.TxtPage;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;

public class TxtFragment extends BaseFragment<TxtLogic> implements TxtPage {

    @Override
    TxtLogic newLogic() {
        return new TxtAnswer(this);
    }

    private TextView textview;

    private Dialog loadDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_txt, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textview = findViewById(R.id.textview);


        Toolbar toolbar = findViewById(R.id.toolbar);
        getPhoneMp3Activity().setSupportActionBar(toolbar);

        ActionBar supportActionBar = getPhoneMp3Activity().getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_txtactivity, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getPhoneMp3Activity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 调用此方法快速打开文本文件。
    public static void open(String filePath, PhoneMp3Activity phoneMp3Activity) {

        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        phoneMp3Activity.push(TxtFragment.class, bundle);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }

    @Override
    public void showLoading() {
        loadDialog = ProgressDialog.show(getPhoneMp3Activity(), null, "文件读取中...");
    }

    @Override
    public void showTxt(String txt) {
        textview.setText(TextUtils.isEmpty(txt) ? "无内容" : txt);
        if (loadDialog != null) loadDialog.dismiss();
    }
}
