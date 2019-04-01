package cn.microanswer.phonemp3.ui.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.entity.PlayList_Table;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class AddMusicToPlayListDialog extends BaseDialog implements AdapterView.OnItemClickListener {

    // 要添加的歌曲
    private Music mMusic;

    // 所有播放列表
    private List<PlayList> playLists;

    // 加载视图
    private LinearLayout linearLayoutLoadingView;
    // 空数据视图
    private LinearLayout emptyView;
    // 播放列表视图
    private ListView listView;

    private int animTime;

    public AddMusicToPlayListDialog(@NonNull Context context, Music music) {
        super(context, context.getResources().getString(R.string.addToPlayList));
        mMusic = music;

        // 使用系统的动画时长。
        animTime = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
    }

    @Override
    protected View getContentView(FrameLayout parent) {
        View view = getLayoutInflater().inflate(R.layout.dialog_addmusic_to_playlist, parent, false);

        linearLayoutLoadingView = view.findViewById(R.id.linearLayoutLoadingView);
        emptyView = view.findViewById(R.id.emptyview);
        listView = view.findViewById(R.id.listview);
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 加载播放列表。
        loadPlayList();
    }


    private void loadPlayList() {
        Task.TaskHelper.getInstance().run(new Task.ITask<Void, List<PlayList>>() {
            @Override
            public List<PlayList> run(Void param) throws Exception {
                return SQLite.select().from(PlayList.class)
                        .where(PlayList_Table.name.eq(Database.PLAYLIST_NAME_USEROWN))
                        .queryList();
            }

            @Override
            public void afterRun(List value) {
                super.afterRun(value);
                if (Utils.COMMON.isNull(value)) {
                    // 空数据
                    showEmpty();
                } else {
                    // 有数据
                    showData(value);
                }
            }
        });
    }

    private void showData(List<PlayList> value) {
        this.playLists = value;
        listView.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, value) {
            @Nullable
            @Override
            public Object getItem(int position) {
                PlayList p = (PlayList) super.getItem(position);
                return p.getRamark();
            }
        });

        listView.setAlpha(0f);
        listView.setVisibility(View.VISIBLE);
        linearLayoutLoadingView.animate().alpha(0f).setDuration(animTime);
        listView.animate()
                .alpha(1f)
                .setDuration(animTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayoutLoadingView.setVisibility(View.GONE);
                    }
                });

    }

    private void showEmpty() {
        emptyView.setAlpha(0f);
        emptyView.setVisibility(View.VISIBLE);
        linearLayoutLoadingView.animate()
                .alpha(0f)
                .setDuration(animTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayoutLoadingView.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Task.TaskHelper.getInstance().run(new Task.ITask<Object, Object>() {


            @Override
            public Object run(Object param) throws Exception {

                PlayList playList = playLists.get(i);
                boolean has = playList.has(mMusic);
                if (!has) {
                    mMusic = Music.from(mMusic);
                    mMusic.setId(0L);
                    mMusic.setListId(playList.getId());
                    return mMusic.save();
                } else {
                    return true; // 播放列表已经有这首歌曲，还是返回true，但是不会再进行保存。
                }
            }

            @Override
            public void afterRun(Object value) {
                super.afterRun(value);
                Boolean v = (Boolean) value;
                Toast.makeText(getContext(), v ? "添加成功" : "添加失败", Toast.LENGTH_SHORT).show();
            }
        });
        dismiss();
    }
}
