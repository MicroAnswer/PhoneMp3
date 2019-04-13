package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.AboutLogic;
import cn.microanswer.phonemp3.logic.answer.AboutAnswer;
import cn.microanswer.phonemp3.ui.AboutPage;

public class AboutFragment extends BaseFragment<AboutLogic> implements AboutPage, View.OnClickListener {

    private TextView textViewVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
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

        findViewById(R.id.cellCheckUpdate).setOnClickListener(this);
        findViewById(R.id.cellFeed).setOnClickListener(this);
    }

    @Override
    public void displayVersion(String version) {
        textViewVersion.setText(getString(R.string.versionwarp, version));
    }

    @Override
    public void onResume() {
        super.onResume();
        getLogic().onResume();
    }

    @Override
    AboutLogic newLogic() {
        return new AboutAnswer(this);
    }

    @Override
    public void onClick(View v) {
        getLogic().onItemClick(v.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }
}
