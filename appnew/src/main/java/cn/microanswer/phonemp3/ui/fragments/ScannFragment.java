package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.ScannLogic;
import cn.microanswer.phonemp3.logic.answer.ScannAnswer;
import cn.microanswer.phonemp3.ui.ScannPage;


public class ScannFragment extends BaseFragment<ScannLogic> implements ScannPage, View.OnClickListener {

    private TextView textViewResult;
    private Button buttonScann;
    private TextView textviewscannHint;
    private ProgressBar progressbar;

    @Override
    ScannLogic newLogic() {
        return new ScannAnswer(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scann, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewResult = findViewById(R.id.textViewResult);
        buttonScann = findViewById(R.id.buttonScann);
        textviewscannHint = findViewById(R.id.textviewscannHint);
        progressbar = findViewById(R.id.progressbar);

        buttonScann.setOnClickListener(this);

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    @Override
    public void onClick(View view) {
        getLogic().onScannClick();
    }

    @Override
    public void showLoading() {
        progressbar.setVisibility(View.VISIBLE);
        buttonScann.setVisibility(View.GONE);
    }

    @Override
    public void setScannHint(String txt) {
        if (TextUtils.isEmpty(txt)) {
            txt = "";
        }
        textviewscannHint.setText(txt);
    }

    @Override
    public void showResult(String txt) {
        progressbar.setVisibility(View.GONE);
        buttonScann.setVisibility(View.GONE);
        textviewscannHint.setVisibility(View.GONE);
        textViewResult.setVisibility(View.VISIBLE);
        textViewResult.setText(txt);
    }
}
