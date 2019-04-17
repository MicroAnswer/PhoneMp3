package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.Main_AllMusic_M_Logic;
import cn.microanswer.phonemp3.logic.answer.Main_AllMusic_M_Answer;
import cn.microanswer.phonemp3.ui.Main_AllMucis_M_Page;
import cn.microanswer.phonemp3.ui.fragments.adapter.MusicItemRecyclerViewAdapter;
import cn.microanswer.phonemp3.ui.views.MusicListView;

public class Main_AllMusic_M_Fragment extends BaseFragment<Main_AllMusic_M_Logic> implements Main_AllMucis_M_Page, View.OnClickListener, MusicItemRecyclerViewAdapter.OnItemClickListener {
    private final int PERMISION_CODE = 10000; // 本fragment权限请求码


    private MusicListView musicListView;
    private TextView textViewScannHint;

    @Override
    Main_AllMusic_M_Logic newLogic() {
        return new Main_AllMusic_M_Answer(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_allmusic_m, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        musicListView = findViewById(R.id.musicListView);
        musicListView.setOnItemClickListener(this);
        textViewScannHint = findViewById(R.id.textViewScanHint);
        findViewById(R.id.buttonScann).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLogic().onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        getLogic().onPageCreated(null, getArguments());
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
    public void setScanHint(String txt) {
        if (TextUtils.isEmpty(txt)) {
            txt = "";
        }
        textViewScannHint.setText(txt);
    }

    @Override
    public void showData(boolean anim, List<Music> data) {
        musicListView.showData(anim, data);
    }

    @Override
    public void requestPermission(String[] ps) {
        requestPermissions(ps, PERMISION_CODE);
    }

    @Override
    public void dataDeleted(int position) {
        musicListView.remove(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISION_CODE) {
            getLogic().onPermissionsResult(permissions, grantResults);
        }

    }

    @Override
    public void onClick(View v) {
        getLogic().onBtnScannClick();
    }

    @Override
    public void onItemClick(View v, Music music, int position) {
        getLogic().onMusicItemClick(v, music, position);
    }

    @Override
    public void onItemMenuClick(View v, final Music music, final int position) {
        PopupMenu popupMenu = new PopupMenu(getPhoneMp3Activity(), v, Gravity.CENTER);
        popupMenu.inflate(R.menu.menu_musicitem_allmusic);
        popupMenu.setOnMenuItemClickListener(item -> {
            getLogic().doItemMenuClick(music, position, item.getItemId());
            return true;
        });
        popupMenu.show();
    }
}
