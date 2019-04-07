package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.CodeUseLogic;
import cn.microanswer.phonemp3.logic.answer.CodeUseAnswer;
import cn.microanswer.phonemp3.ui.CodeUsePage;
import cn.microanswer.phonemp3.ui.fragments.adapter.CodeUseListRecyclerViewAdapter;

public class CodeUseFragment extends BaseFragment<CodeUseLogic> implements CodeUsePage, CodeUseListRecyclerViewAdapter.onCodeUseListItemClick {

    private LinearLayout linearLayoutLoadingView;
    private LinearLayout linearLayoutEmptyView;
    private RecyclerView recyclerView;

    @Override
    CodeUseLogic newLogic() {
        return new CodeUseAnswer(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_codeuse, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutEmptyView = findViewById(R.id.linearLayoutEmptyView);
        linearLayoutLoadingView = findViewById(R.id.linearLayoutLoadingView);
        recyclerView = findViewById(R.id.recyclerViewCodeUseList);


        Toolbar toolbar = findViewById(R.id.toolbar);
        getPhoneMp3Activity().setSupportActionBar(toolbar);

        ActionBar supportActionBar = getPhoneMp3Activity().getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }


        getLogic().onPageCreated(savedInstanceState, getArguments());
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
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }

    @Override
    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        linearLayoutEmptyView.setVisibility(View.INVISIBLE);
        linearLayoutLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCodes(String[] titles, String[] descs) {
        recyclerView.setVisibility(View.VISIBLE);
        linearLayoutEmptyView.setVisibility(View.INVISIBLE);
        linearLayoutLoadingView.setVisibility(View.INVISIBLE);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (null == layoutManager) {
            layoutManager = new LinearLayoutManager(getPhoneMp3Activity(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        getAdapter().setData(titles, descs);
    }

    private CodeUseListRecyclerViewAdapter getAdapter() {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new CodeUseListRecyclerViewAdapter();
            ((CodeUseListRecyclerViewAdapter) adapter).setOnClickLogListItem(this);
            recyclerView.setAdapter(adapter);
        }
        return (CodeUseListRecyclerViewAdapter) adapter;
    }

    @Override
    public void onClickCodeUseListItem(int position, String name, String desc, View view) {
        getLogic().onClickCodeUseItem(position, name, desc);
    }

    @Override
    public boolean onLongClickCodeUseListItem(int position, String name, String desc, View view) {
        return false;
    }
}
