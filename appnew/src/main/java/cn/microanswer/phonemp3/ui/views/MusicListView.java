package cn.microanswer.phonemp3.ui.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.ui.fragments.adapter.MusicItemRecyclerViewAdapter;

public class MusicListView extends FrameLayout implements MusicItemRecyclerViewAdapter.OnItemClickListener {
    private final int SHOW_LOAD = 1, SHOW_EMPTY = 2, SHOW_DATA = 3;

    // 加载完成数据后，数据不是猛的闪现出来的，而是渐变显示出来的，此字段保存渐变时长。单位毫秒
    private int mAnimTime = 200;
    private int current = SHOW_LOAD;

    private LinearLayout linearLayoutLoadingView;
    private LinearLayout linearLayoutEmptyView;
    private RecyclerView recyclerView;

    private MusicItemRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public MusicListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 使用系统的动画时长。
        mAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        layoutInflater.inflate(R.layout.view_music_listview, this, true);

        linearLayoutEmptyView = findViewById(R.id.linearLayoutEmptyView);
        linearLayoutLoadingView = findViewById(R.id.linearLayoutLoadingView);
        recyclerView = findViewById(R.id.recyclerViewMusics);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MusicListView);

        int emptyViewLayoutId = typedArray.getResourceId(R.styleable.MusicListView_emptyView, -1);
        if (-1 != emptyViewLayoutId) {
            linearLayoutEmptyView.addView(layoutInflater.inflate(emptyViewLayoutId, linearLayoutEmptyView, false));
        }

        int loadingViewLayoutID = typedArray.getResourceId(R.styleable.MusicListView_loadingView, -1);
        if (-1 != loadingViewLayoutID) {
            linearLayoutLoadingView.addView(layoutInflater.inflate(loadingViewLayoutID, linearLayoutLoadingView, false));
        }

        typedArray.recycle();
    }

    /**
     * 显示加载容器
     */
    public void showLoading(boolean anim) {
        if (current == SHOW_LOAD) {
            return;
        }
        if (!anim) {
            linearLayoutLoadingView.setVisibility(View.VISIBLE);
            linearLayoutLoadingView.setAlpha(1F);
            linearLayoutEmptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            current = SHOW_LOAD;
            return;
        }
        linearLayoutLoadingView.setAlpha(0F);
        linearLayoutLoadingView.setVisibility(VISIBLE);
        linearLayoutLoadingView.animate().alpha(1F).setDuration(mAnimTime).setListener(null);

        linearLayoutEmptyView.animate().alpha(0F).setDuration(mAnimTime).setListener(null);
        recyclerView.animate().alpha(0F).setDuration(mAnimTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                linearLayoutEmptyView.setVisibility(GONE);
                recyclerView.setVisibility(GONE);
                current = SHOW_LOAD;
            }
        });
    }

    /**
     * 显示空数据容器
     */
    public void showEmpty(boolean anim) {
        if (current == SHOW_EMPTY) {
            return;
        }

        if (!anim) {
            linearLayoutEmptyView.setAlpha(1F);
            linearLayoutEmptyView.setVisibility(VISIBLE);
            linearLayoutLoadingView.setVisibility(GONE);
            recyclerView.setVisibility(GONE);
            current = SHOW_EMPTY;
            return;
        }

        linearLayoutEmptyView.setAlpha(0F);
        linearLayoutEmptyView.setVisibility(VISIBLE);
        linearLayoutEmptyView.animate().alpha(1F).setDuration(mAnimTime).setListener(null);

        recyclerView.animate().alpha(0F).setDuration(mAnimTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                linearLayoutLoadingView.setVisibility(GONE);
                recyclerView.setVisibility(GONE);
                current = SHOW_EMPTY;
            }
        });
    }

    /**
     * 显示数据容器
     */
    private void showData(boolean anim) {
        if (current == SHOW_DATA) {
            return;
        }
        if (!anim) {
            recyclerView.setAlpha(1F);
            recyclerView.setVisibility(VISIBLE);
            linearLayoutLoadingView.setVisibility(GONE);
            linearLayoutEmptyView.setVisibility(GONE);
            current = SHOW_DATA;
            return;
        }

        // 影藏加载容器，显示数据容器。
        recyclerView.setAlpha(0F);
        recyclerView.setVisibility(VISIBLE);
        recyclerView.animate().alpha(1F).setDuration(mAnimTime).setListener(null);

        linearLayoutLoadingView.animate().alpha(0F).setDuration(mAnimTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayoutLoadingView.setVisibility(GONE);
                        linearLayoutEmptyView.setVisibility(GONE);
                        current = SHOW_DATA;
                    }
                });

    }


    /**
     * 显示数据
     *
     * @param data
     */
    public void showData(boolean anim, List<Music> data) {
        if (data == null || data.size() < 1) {
            showEmpty(anim);
            return;
        }

        MusicItemRecyclerViewAdapter adapter = getAdapter();
        adapter.setMusicList(data);
        showData(anim);
    }

    private MusicItemRecyclerViewAdapter getAdapter() {
        MusicItemRecyclerViewAdapter adapter = (MusicItemRecyclerViewAdapter) recyclerView.getAdapter();
        if (null == adapter) {
            adapter = new MusicItemRecyclerViewAdapter();
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        return adapter;
    }

    public void setOnItemClickListener(MusicItemRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MusicItemRecyclerViewAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public void onItemClick(View v, Music music, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, music, position);
        }
    }

    @Override
    public void onItemMenuClick(View v, Music music, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemMenuClick(v, music, position);
        }
    }

    public void move(int from, final int i) {
        MusicItemRecyclerViewAdapter adapter = getAdapter();
        adapter.move(from, i);
    }

    public void add(Music music) {
        showData(false);
        getAdapter().addMusic(music);
    }

    public void add(int i, Music object) {
        showData(false);
        MusicItemRecyclerViewAdapter adapter = getAdapter();
        adapter.addMusic(i, object);
    }

    public void addOrMoveMusic(Music object) {
        getAdapter().addOrMoveMusic(object);
    }

    public void remove(Music value) {
        MusicItemRecyclerViewAdapter adapter = getAdapter();
        adapter.remove(value);
    }

    public void remove(int position) {
        MusicItemRecyclerViewAdapter adapter = getAdapter();
        adapter.remove(position);
    }
}
