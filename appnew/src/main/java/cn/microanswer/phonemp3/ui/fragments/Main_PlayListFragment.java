package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.logic.Main_PlayListLogic;
import cn.microanswer.phonemp3.logic.answer.Main_PlayListAnswer;
import cn.microanswer.phonemp3.ui.Main_PlayListPage;
import cn.microanswer.phonemp3.ui.dialogs.AddPlayListDialog;
import cn.microanswer.phonemp3.ui.dialogs.DeletePlayListDialog;
import cn.microanswer.phonemp3.ui.dialogs.ReNamePlayListDialog;
import cn.microanswer.phonemp3.ui.fragments.adapter.MyPlayListRecyclerViewAdapter;

public class Main_PlayListFragment extends BaseFragment<Main_PlayListLogic> implements Main_PlayListPage, View.OnClickListener, MyPlayListRecyclerViewAdapter.onMyPlayListItemClick {

    // 空数据视图
    private LinearLayout emptyView;
    // 加载视图
    private LinearLayout loadingView;
    // 数据展示视图
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.addSubMenu(0, R.id.switchview, 0, getString(R.string.switch_view));
        menu.addSubMenu(0, R.id.editplaylist, 1, getString(R.string.manage));
        menu.addSubMenu(0, R.id.createnewplaylist, 2, getString(R.string.create_new_playlist));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.createnewplaylist) {
            displayAddPlayListDialog();
            return true;
        } else if (item.getItemId() == R.id.switchview) {
            Toast.makeText(getPhoneMp3Activity(), "开发...", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.editplaylist) {
            Toast.makeText(getPhoneMp3Activity(), "开发...", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        getPhoneMp3Activity().setSupportActionBar(toolbar);

        emptyView = findViewById(R.id.linearLayoutEmptyView);
        loadingView = findViewById(R.id.linearLayoutLoadingView);
        recyclerView = findViewById(R.id.recyclerViewPlayList);

        // 监听添加播放列表的按钮。 这个按钮在没有任何播放列表的时候显示。
        findViewById(R.id.buttonAddPlayList).setOnClickListener(this);


        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    @Override
    Main_PlayListLogic newLogic() {
        return new Main_PlayListAnswer(this);
    }

    @Override
    public void showLoading() {
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showData(List<PlayList> lists) {
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (null == layoutManager) {
            layoutManager = new LinearLayoutManager(getPhoneMp3Activity(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        MyPlayListRecyclerViewAdapter adapter = getRecyclerViewAdapter();
        if (lists != null) {
            adapter.setPlayLists(lists);
        }
    }

    private MyPlayListRecyclerViewAdapter getRecyclerViewAdapter() {
        MyPlayListRecyclerViewAdapter adapter = (MyPlayListRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new MyPlayListRecyclerViewAdapter();
            adapter.setOnMyPlayListItemClick(this);
            recyclerView.setAdapter(adapter);
        }
        return adapter;
    }

    @Override
    public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonAddPlayList) {
            displayAddPlayListDialog();
        }

    }

    // 弹出添加列表弹出框
    private void displayAddPlayListDialog() {
        new AddPlayListDialog(getPhoneMp3Activity()).setOnSureCreatePlayList(new AddPlayListDialog.OnSureCreatePlayList() {
            @Override
            public void save(String name, final AddPlayListDialog dialog) {
                getLogic().onSaveMyPlayList(name, new Main_PlayListLogic.OnSaveMyPlayListListener() {
                    @Override
                    public void onComp(int status, PlayList playList, String msg) {
                        if (status == 0) {
                            Toast.makeText(getPhoneMp3Activity(), "已存在同名的歌单", Toast.LENGTH_SHORT).show();
                        } else if (status == 1) {
                            MyPlayListRecyclerViewAdapter adapter = getRecyclerViewAdapter();
                            adapter.add(playList);
                            showData(null);
                            Toast.makeText(getPhoneMp3Activity(), "创建成功", Toast.LENGTH_SHORT).show();
                            dialog.hide();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getPhoneMp3Activity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).show();
    }

    @Override
    public void onClickPlayListItem(int position, PlayList playList, View v) {
        Toast.makeText(getPhoneMp3Activity(), playList.getRamark(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClickPlayListItem(int position, PlayList playList, View v) {
        PopupMenu popupMenu = new PopupMenu(getPhoneMp3Activity(), v, Gravity.CENTER);
        __initPopupMenu(popupMenu, position, playList, v);
        popupMenu.show();
        return true;
    }

    /**
     * 歌单条目长安后会弹出操作菜单，此方法对该菜单进行初始化。
     * 包括： 菜单内容，菜单事件监听。
     *
     * @param popupMenu 要初始化的弹出菜单
     * @param position  长按的歌单条目
     * @param playList  长按的歌单
     * @param v         长按的View
     */
    private void __initPopupMenu(PopupMenu popupMenu, final int position, final PlayList playList, View v) {
        popupMenu.inflate(R.menu.menu_playlist);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.rename) {
                    // 重命名歌单
                    renamePlayList(position, playList);
                } else if (id == R.id.detail) {
                    // 查看歌单详情
                } else {
                    // 删除歌单
                    deletePlayList(position, playList);
                }
                return true;
            }
        });
    }

    /**
     * 重命名歌单
     *
     * @param position 此歌单在列表中的位置。
     * @param playList 歌单。
     */
    private void renamePlayList(final int position, PlayList playList) {
        ReNamePlayListDialog reNamePlayListDialog = new ReNamePlayListDialog(getPhoneMp3Activity(), playList);
        reNamePlayListDialog.setReNameListener(new ReNamePlayListDialog.ReNameListener() {
            @Override
            public void onReNamed(String newName) {
                getRecyclerViewAdapter().notifyItemChanged(position);
            }
        });
        reNamePlayListDialog.show();
    }

    private void deletePlayList(final int position, PlayList playList) {
        DeletePlayListDialog deletePlayListDialog = new DeletePlayListDialog(getPhoneMp3Activity(), playList);
        deletePlayListDialog.setDeleteListener(new DeletePlayListDialog.DeleteListener() {
            @Override
            public void onDeleted() {
                MyPlayListRecyclerViewAdapter recyclerViewAdapter = getRecyclerViewAdapter();
                recyclerViewAdapter.remove(position);
                if (recyclerViewAdapter.getItemCount() <= 0) {
                    showEmpty();
                }
            }
        });
        deletePlayListDialog.show();
    }


    @Override
    public void onDestroy() {
        getPhoneMp3Activity().setSupportActionBar(null);
        setHasOptionsMenu(false);
        super.onDestroy();
    }
}
