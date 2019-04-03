package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.Main_MyLoveLogic;
import cn.microanswer.phonemp3.logic.answer.Main_MyLoveAnswer;
import cn.microanswer.phonemp3.ui.Main_MyLovePage;
import cn.microanswer.phonemp3.ui.fragments.adapter.MusicItemRecyclerViewAdapter;
import cn.microanswer.phonemp3.ui.views.MusicListView;

public class Main_MyLoveFragment extends BaseFragment<Main_MyLoveLogic> implements Main_MyLovePage, MusicItemRecyclerViewAdapter.OnItemClickListener {

    private MusicListView musicListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_mylove, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicListView = findViewById(R.id.musicListView);
        musicListView.setOnItemClickListener(this);
        getLogic().onPageCreated(savedInstanceState, getArguments());
    }


    @Override
    public void showLoading(boolean anim) {
        musicListView.showLoading(anim);
    }

    @Override
    public void showEmpty(boolean anim) {
        musicListView.showEmpty(anim);
    }

    @Override
    public void showData(boolean anim, List<Music> data) {
        musicListView.showData(anim, data);
    }

    @Override
    public void addLoved(Music value) {
        musicListView.add(value);
    }

    @Override
    public void removeLoved(Music value) {
        musicListView.remove(value);
    }

    @Override
    Main_MyLoveLogic newLogic() {
        return new Main_MyLoveAnswer(this);
    }

    @Override
    public void onItemClick(View v, Music music, int position) {
        getLogic().onItemClick(v, music, position);
    }

    @Override
    public void onItemMenuClick(View v, Music music, int position) {

    }
}
