package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.SetLogic;
import cn.microanswer.phonemp3.logic.answer.SetAnswer;
import cn.microanswer.phonemp3.ui.SetPage;
import cn.microanswer.phonemp3.ui.views.Cell;

public class SetFragment extends BaseFragment<SetLogic> implements SetPage, View.OnClickListener {

    private TextView textViewVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    SetLogic newLogic() {
        return new SetAnswer(this);
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

        textViewVersion = findViewById(R.id.textViewVersion);

        findViewById(R.id.cellSetItemScannMusic).setOnClickListener(this);
        findViewById(R.id.cellSetItemThemeColor).setOnClickListener(this);
        findViewById(R.id.cellCheckUpdate).setOnClickListener(this);
        findViewById(R.id.cellCodeUse).setOnClickListener(this);
        findViewById(R.id.cellFeed).setOnClickListener(this);

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
        getPhoneMp3Activity().setSupportActionBar(null);
        setHasOptionsMenu(false);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Cell) {
            getLogic().onSetItemClick(v.getId());
        }
    }

    @Override
    public void setVersion(String version) {
        textViewVersion.setText(version);
    }
}
