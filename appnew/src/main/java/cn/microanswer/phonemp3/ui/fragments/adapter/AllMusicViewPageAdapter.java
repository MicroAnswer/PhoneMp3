package cn.microanswer.phonemp3.ui.fragments.adapter;

import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import cn.microanswer.phonemp3.ui.fragments.Main_AllMusic_A_Fragment;
import cn.microanswer.phonemp3.ui.fragments.Main_AllMusic_D_Fragment;
import cn.microanswer.phonemp3.ui.fragments.Main_AllMusic_M_Fragment;
import cn.microanswer.phonemp3.ui.fragments.Main_AllMusic_S_Fragment;

public class AllMusicViewPageAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"所有歌曲", "专辑", "歌手", "文件夹"};

    private SparseArray<Fragment> fragmentMap;

    public AllMusicViewPageAdapter(FragmentManager fm) {
        super(fm);

        fragmentMap = new SparseArray<>();

        Fragment bf = fm.findFragmentByTag(Main_AllMusic_M_Fragment.class.getSimpleName());
        if (bf == null) {
            bf = new Main_AllMusic_M_Fragment();
        }
        fragmentMap.put(0, bf);

        bf = fm.findFragmentByTag(Main_AllMusic_A_Fragment.class.getSimpleName());
        if (bf == null) {
            bf = new Main_AllMusic_A_Fragment();
        }
        fragmentMap.put(1, bf);

        bf = fm.findFragmentByTag(Main_AllMusic_S_Fragment.class.getSimpleName());
        if (bf == null) {
            bf = new Main_AllMusic_S_Fragment();
        }
        fragmentMap.put(2, bf);

        bf = fm.findFragmentByTag(Main_AllMusic_D_Fragment.class.getSimpleName());
        if (bf == null) {
            bf = new Main_AllMusic_D_Fragment();
        }
        fragmentMap.put(3, bf);


    }

    @Override
    public Fragment getItem(int position) {
        return fragmentMap.get(position);
    }

    @Override
    public int getCount() {
        return fragmentMap.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
