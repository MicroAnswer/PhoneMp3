package cn.microanswer.phonemp3.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.LogLogic;
import cn.microanswer.phonemp3.logic.answer.LogAnswer;
import cn.microanswer.phonemp3.ui.LogPage;
import cn.microanswer.phonemp3.ui.activitys.TxtActivity;
import cn.microanswer.phonemp3.ui.fragments.adapter.LogListRecyclerViewAdapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LogFragment extends BaseFragment<LogLogic> implements LogPage, View.OnClickListener, LogListRecyclerViewAdapter.onMyLogListItemClick {
    @Override
    LogLogic newLogic() {
        return new LogAnswer(this);
    }
    // 加载完成数据后，数据不是猛的闪现出来的，而是渐变显示出来的，此字段保存渐变时长。单位毫秒
    private int mAnimTime = 200;

    // 数据展示的view
    private RecyclerView recyclerViewLogList;

    // 加载提示的view
    private LinearLayout linearLayoutLoadingView;

    // 空数据的view
    private LinearLayout linearLayoutEmptyView;

    // 刷新按钮
    private Button refreshButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        getPhoneMp3Activity().setSupportActionBar(toolbar);

        ActionBar supportActionBar = getPhoneMp3Activity().getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 使用系统的动画时长。
        mAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        recyclerViewLogList = findViewById(R.id.recyclerViewLogList);
        linearLayoutEmptyView = findViewById(R.id.linearLayoutEmptyView);
        linearLayoutLoadingView = findViewById(R.id.linearLayoutLoadingView);
        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        getLogic().onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getPhoneMp3Activity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLogList(List<File> fileList) {

        RecyclerView.LayoutManager layoutManager = recyclerViewLogList.getLayoutManager();
        if (null == layoutManager) {
            layoutManager = new LinearLayoutManager(getPhoneMp3Activity(), RecyclerView.VERTICAL, false);
            recyclerViewLogList.setLayoutManager(layoutManager);
        }



        // 影藏加载容器，显示数据容器。
        recyclerViewLogList.setAlpha(0F);
        recyclerViewLogList.setVisibility(VISIBLE);
        recyclerViewLogList.animate().alpha(1F).setDuration(mAnimTime).setListener(null);

        linearLayoutLoadingView.animate().alpha(0F).setDuration(mAnimTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayoutLoadingView.setVisibility(GONE);
                        linearLayoutEmptyView.setVisibility(GONE);
                    }
                });

        LogListRecyclerViewAdapter adapter = getAdapter();
        if (fileList != null) {
            adapter.setFileList(fileList);
        }
    }

    @Override
    public void showLoading() {
        linearLayoutEmptyView.setVisibility(GONE);
        linearLayoutLoadingView.setVisibility(VISIBLE);
        recyclerViewLogList.setVisibility(GONE);
    }

    @Override
    public void showEmpty() {
        linearLayoutEmptyView.setVisibility(VISIBLE);
        linearLayoutLoadingView.setVisibility(GONE);
        recyclerViewLogList.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh) {
            getLogic().onRefreshClick();
        }
    }

    private LogListRecyclerViewAdapter getAdapter () {
        RecyclerView.Adapter adapter = recyclerViewLogList.getAdapter();
        if (adapter == null) {
            adapter = new LogListRecyclerViewAdapter();
            ((LogListRecyclerViewAdapter) adapter).setOnMyLogListItemClick(this);
            recyclerViewLogList.setAdapter(adapter);
        }
        return (LogListRecyclerViewAdapter) adapter;
    }

    @Override
    public void onClickLogListItem(int position, File f, View view) {
        TxtActivity.open(f.getAbsolutePath(), getPhoneMp3Activity());
    }

    @Override
    public boolean onLongClickLogListItem(int position, File f, View view) {
        return false;
    }
}
