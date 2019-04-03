package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.Main_AllMusicLogic;
import cn.microanswer.phonemp3.logic.answer.Main_AllMusicAnswer;
import cn.microanswer.phonemp3.ui.Main_AllMucisPage;
import cn.microanswer.phonemp3.ui.fragments.adapter.AllMusicViewPageAdapter;

public class Main_AllMusicFragment extends BaseFragment<Main_AllMusicLogic> implements Main_AllMucisPage {

    @Override
    Main_AllMusicAnswer newLogic() {
        return new Main_AllMusicAnswer(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_allmusic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager, true);
        // AllMusicViewPageAdapter adapter;
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(new AllMusicViewPageAdapter(getChildFragmentManager()));
        }

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }
}
