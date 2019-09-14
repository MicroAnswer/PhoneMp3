package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_allmusic, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_allmusic, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewpager);


        Toolbar toolbar = findViewById(R.id.toolbar);
        getPhoneMp3Activity().setSupportActionBar(toolbar);

        tabLayout.setupWithViewPager(viewPager, true);
        // AllMusicViewPageAdapter adapter;
        viewPager.setOffscreenPageLimit(4);
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(new AllMusicViewPageAdapter(getChildFragmentManager()));
        }

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.scan) {
            getPhoneMp3Activity().push(ScannFragment.class);
            return true;
        } else if (item.getItemId() == R.id.search) {
            getPhoneMp3Activity().push(SearchFragment.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }
}
