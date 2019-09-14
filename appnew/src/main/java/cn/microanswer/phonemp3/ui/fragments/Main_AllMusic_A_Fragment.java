package cn.microanswer.phonemp3.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Ablum;
import cn.microanswer.phonemp3.logic.Main_AllMusic_A_Logic;
import cn.microanswer.phonemp3.logic.answer.Main_AllMusic_A_Answer;
import cn.microanswer.phonemp3.ui.Main_AllMucis_A_Page;
import cn.microanswer.phonemp3.ui.fragments.adapter.AlbumRecyclerViewAdapter;
import cn.microanswer.phonemp3.util.Utils;

public class Main_AllMusic_A_Fragment extends BaseFragment<Main_AllMusic_A_Logic> implements Main_AllMucis_A_Page {

    private RecyclerView recyclerViewAlbums;
    private GridLayoutManager gridLayoutManager;
    private AlbumRecyclerViewAdapter albumRecyclerViewAdapter;

    @Override
    Main_AllMusic_A_Logic newLogic() {
        return new Main_AllMusic_A_Answer(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_allmusic_a, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            recyclerViewAlbums = findViewById(R.id.recyclerViewAlbums);
            recyclerViewAlbums.addItemDecoration(new SpaceItemDecoration(Utils.UI.dp2px(getPhoneMp3Activity(), 8f)));
            gridLayoutManager = new GridLayoutManager(getPhoneMp3Activity(), 2, GridLayoutManager.VERTICAL, false);
            recyclerViewAlbums.setLayoutManager(gridLayoutManager);

            albumRecyclerViewAdapter = new AlbumRecyclerViewAdapter();
            recyclerViewAlbums.setAdapter(albumRecyclerViewAdapter);
        // Log.i("Microanswer", "Main_AllMusic_A_Fragment：onViewCreated");
        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.right = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) %2==0) {
                outRect.left = space;
            }
            if (parent.getChildLayoutPosition(view) <= 2) {
                outRect.top = space;
            }
        }

    }


    @Override
    public void displayAblums(List<Ablum> ablums) {
        albumRecyclerViewAdapter.setAblumList(ablums);
    }
}
