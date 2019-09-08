package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.Nullable;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.IndexLogic;
import cn.microanswer.phonemp3.logic.answer.IndexAnswer;
import cn.microanswer.phonemp3.ui.IndexPage;

/**
 * 显示 LOGO 和 封面图片的 界面。<br/>
 * <p>
 * 没有封面的时候，就只显示logo
 * 有封面的时候，显示封面。
 * </p>
 */
public class IndexFragment extends BaseFragment<IndexLogic> implements IndexPage, View.OnClickListener {

    private TextView textViewVersionInfo;
    private ImageView imageViewCover;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    IndexLogic newLogic() {
        return new IndexAnswer(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewVersionInfo = findViewById(R.id.textViewVersionInfo);
        imageViewCover = findViewById(R.id.imageViewCover);
        imageViewCover.setOnClickListener(this);
        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    @Override
    public void displayCover(String url) {
        try {
            Glide.with(this).load(url).into(imageViewCover);
        }catch (Exception ignore) {}
    }

    @Override
    public void displVersionInfo(String versionInfo) {
        textViewVersionInfo.setText(versionInfo);
    }

    @Override
    public void onClick(View v) {
        getLogic().jumpToAdLink();
    }
}
