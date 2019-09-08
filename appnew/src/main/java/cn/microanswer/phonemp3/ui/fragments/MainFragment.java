package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.logic.MainLogic;
import cn.microanswer.phonemp3.logic.answer.MainAnswer;
import cn.microanswer.phonemp3.ui.MainPage;
import cn.microanswer.phonemp3.ui.views.Cell;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

/**
 * <pre>
 *     主界面。
 *     此界面中管理了： 最近播放界面、我的收藏界面、我的歌单界面、所有歌曲界面。
 *     同时，此界面中包含自身功能，要在底部保持显示播放条。
 * </pre>
 */
public class MainFragment extends BaseFragment<MainLogic> implements MainPage,
        View.OnClickListener, DrawerLayout.DrawerListener {

    /**
     * <pre>
     *     多个界面的容器。
     * </pre>
     */
    private static final int FRAMENT_CONTAINER_ID = R.id.frameLayoutMainBody;

    // 侧滑菜单控件
    private DrawerLayout drawerLayoutMenu;

    // 侧滑菜单控件中的夜间模式\日间模式切换按钮
    private Cell menuCellNightMode;

    // 底部播放条中显示歌曲封面的
    private ImageView imageViewCover;

    // 歌曲标题
    private TextView textviewMusicTitle;
    // 歌曲描述
    private TextView textviewMusicDesc;

    // 播放暂停按钮
    private ImageView imageviewPausePlay;
    // 是否收藏的小图标
    private ImageView imageViewLove;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    MainLogic newLogic() {
        return new MainAnswer(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawerLayoutMenu = findViewById(R.id.drawerLayoutMenu);
        menuCellNightMode = findViewById(R.id.menuCellNightMode);

        menuCellNightMode.setOnClickListener(this);
        findViewById(R.id.menuCellRecentPlay).setOnClickListener(this);
        findViewById(R.id.menuCellMyLove).setOnClickListener(this);
        findViewById(R.id.menuCellMyPlayList).setOnClickListener(this);
        findViewById(R.id.menuCellAllMusic).setOnClickListener(this);
        findViewById(R.id.menuCellTimeOut).setOnClickListener(this);
        findViewById(R.id.menuCellMoreFunction).setOnClickListener(this);
        findViewById(R.id.menuCellSetting).setOnClickListener(this);
        findViewById(R.id.menuCellAbout).setOnClickListener(this);
        findViewById(R.id.textviewExit).setOnClickListener(this);
        findViewById(R.id.linearLayoutPlayControler).setOnClickListener(this);
        findViewById(R.id.imageviewNext).setOnClickListener(this);
        imageViewLove = findViewById(R.id.imageviewLove);
        imageViewLove.setOnClickListener(this);
        imageviewPausePlay = findViewById(R.id.imageViewPausePlay);
        imageviewPausePlay.setOnClickListener(this);
        imageViewCover = findViewById(R.id.imageViewCover);
        textviewMusicTitle = findViewById(R.id.textviewMusicTitle);
        textviewMusicDesc = findViewById(R.id.textviewMusicdesc);

        getLogic().onPageCreated(savedInstanceState, getArguments());
    }


    private View clickedMenu = null;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageviewLove) {
            getLogic().onBtnLoveClick();
            return;
        }
        if (id == R.id.imageviewNext) {
            getLogic().onBtnNextClick();
            return;
        }
        if (id == R.id.imageViewPausePlay) {
            getLogic().onPausePlayClick();
            return;
        }
        if (id == R.id.textviewExit) {
            getLogic().onExitClick();
            return;
        }
        if (id == R.id.linearLayoutPlayControler) {
            getLogic().onPlayControlerClick();
            return;
        }
        clickedMenu = v;
        drawerLayoutMenu.removeDrawerListener(this);
        drawerLayoutMenu.addDrawerListener(this);
        drawerLayoutMenu.closeDrawers();
    }

    public boolean onSystemMenuClick() {
        if (isVisible()) {
            if (drawerLayoutMenu.isDrawerOpen(Gravity.LEFT)) {
                drawerLayoutMenu.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayoutMenu.openDrawer(Gravity.LEFT);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {

    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        if (clickedMenu != null) {
            getLogic().onMenuClick(clickedMenu.getId());
            clickedMenu = null;
            drawerLayoutMenu.removeDrawerListener(this);
        }
    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    public Fragment getFragment(@NonNull String name) {

        FragmentManager supportFragmentManager = getChildFragmentManager();
        Fragment fragment1 = supportFragmentManager.findFragmentByTag(name);
        if (fragment1 != null) {
            return fragment1;
        }

        if (name.equals(Main_RecentFragment.class.getSimpleName())) {
            Main_RecentFragment fragment = new Main_RecentFragment();
            return fragment;
        } else if (name.equals(Main_MyLoveFragment.class.getSimpleName())) {
            Main_MyLoveFragment fragment = new Main_MyLoveFragment();
            return fragment;
        } else if (name.equals(Main_AllMusicFragment.class.getSimpleName())) {
            Main_AllMusicFragment fragment = new Main_AllMusicFragment();
            return fragment;
        }

        return new Main_AllMusicFragment();
    }

    @Override
    public void setDayModeMenu(boolean isDayMode) {
        if (isDayMode) {
            menuCellNightMode.setIcon(Utils.UI.tintResourceWithThemeColor(getPhoneMp3Activity(), R.drawable.icon_night));
            menuCellNightMode.setTitle(getString(R.string.nightMode));
        } else {
            menuCellNightMode.setIcon(getResources().getDrawable(R.drawable.icon_sun));
            menuCellNightMode.setTitle(getString(R.string.dayMode));
        }
    }

    @Override
    public void updateControllerInfo(String coverPath, String title, String desc, boolean love) {
        if (coverPath == null) {
            Glide.with(this).load(R.drawable.icon_ablem).into(imageViewCover);
        } else {
            RequestOptions requestOptions = new RequestOptions().error(R.drawable.icon_ablem);
            Glide.with(this).load(coverPath).apply(requestOptions).into(imageViewCover);
        }
        if (title == null) {
            title = getString(R.string.app_name);
        }
        if (desc == null) {
            desc = "";
        }
        textviewMusicTitle.setText(title);
        textviewMusicDesc.setText(desc);
        if (love) {
            imageViewLove.setImageDrawable(Utils.UI.tintResourceWithThemeColor(getPhoneMp3Activity(), R.drawable.icon_love));
        } else {
            imageViewLove.setImageDrawable(Utils.UI.tintResourceWithThemeColor(getPhoneMp3Activity(), R.drawable.icon_dislove));
        }
    }

    @Override
    public void show(Class<? extends Fragment> fragmentClass) {

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment primaryNavigationFragment = fragmentManager.getPrimaryNavigationFragment();

        if (primaryNavigationFragment != null) {
            if (primaryNavigationFragment.getClass().getSimpleName().equals(fragmentClass.getSimpleName())) {
                // 同一个界面，不用再切换了。
                return;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (primaryNavigationFragment != null) {
            fragmentTransaction.hide(primaryNavigationFragment);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // 判断要显示的fragment是否已经被fragmentManager管理了
        String fragTag = fragmentClass.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(fragTag);
        try {
            if (null == fragmentByTag) {
                fragmentByTag = fragmentClass.newInstance();
                // 没有加入fragment, 进行加入
                fragmentTransaction.add(FRAMENT_CONTAINER_ID, fragmentByTag, fragTag);
            } else {
                // 已经在fragmentManager中管理
                fragmentTransaction.show(fragmentByTag);
            }
            fragmentTransaction.setPrimaryNavigationFragment(fragmentByTag);
            fragmentTransaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyPlayStateChange(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        int stateInt = state.getState();

        int resource;

        if (stateInt == PlaybackStateCompat.STATE_PLAYING) { // 播放状态变为播放中
            resource = R.drawable.icon_pause;
        } else if (stateInt == PlaybackStateCompat.STATE_PAUSED) { // 播放状态变为暂停
            resource = R.drawable.icon_play;
        } else {
            resource = R.drawable.icon_play;
        }
        imageviewPausePlay.setImageDrawable(Utils.UI.tintResourceWithThemeColor(getPhoneMp3Activity(), resource));
    }
}
