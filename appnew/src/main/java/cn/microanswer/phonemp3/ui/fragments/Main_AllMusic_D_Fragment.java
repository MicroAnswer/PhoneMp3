package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.Main_AllMusic_D_Logic;
import cn.microanswer.phonemp3.logic.answer.Main_AllMusic_D_Answer;
import cn.microanswer.phonemp3.ui.Main_AllMucis_D_Page;

public class Main_AllMusic_D_Fragment extends BaseFragment<Main_AllMusic_D_Logic> implements Main_AllMucis_D_Page {
    @Override
    Main_AllMusic_D_Logic newLogic() {
        return new Main_AllMusic_D_Answer(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_allmusic_d, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }
}
