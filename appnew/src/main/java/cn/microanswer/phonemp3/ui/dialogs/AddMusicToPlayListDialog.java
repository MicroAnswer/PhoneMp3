package cn.microanswer.phonemp3.ui.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

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

// TODO 完成添加歌曲到歌单的功能。 此弹出框只有在有多个歌单的时候弹出，没有歌单的情况。只有一个歌单默认自动添加到默认歌单。
public class AddMusicToPlayListDialog extends BaseDialog {

    // 要添加的歌曲
    private Music mMusic;

    // 所有的播放列表。
    private List<PlayList> playLists;

    // 加载视图
    private LinearLayout linearLayoutLoadingView;
    // 空数据视图
    private LinearLayout emptyView;
    // 播放列表视图
    private ListView listView;

    private int animTime;

    public AddMusicToPlayListDialog(@NonNull Context context, Music music) {
        super(context);
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

        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 加载播放列表。
        loadPlayList();
    }


    private void loadPlayList() {
        Task.TaskHelper.getInstance().run(new Task.ITask<Void, List>() {
            @Override
            public List<?> run(Void param) throws Exception {
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

    private void showData(List<?> value) {
        listView.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,value){
            @Nullable
            @Override
            public Object getItem(int position) {
                PlayList p = (PlayList) super.getItem(position);
                return p.getRamark();
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
}
