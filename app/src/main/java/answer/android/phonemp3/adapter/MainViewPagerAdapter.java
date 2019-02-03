package answer.android.phonemp3.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import answer.android.phonemp3.activity.BaseActivity;
import answer.android.phonemp3.activity.MainActivity;
import answer.android.phonemp3.fragment.AblumFragment;
import answer.android.phonemp3.fragment.AllMusicFragment;
import answer.android.phonemp3.fragment.BaseFragment;
import answer.android.phonemp3.fragment.ArtistFragment;
import answer.android.phonemp3.fragment.LovesFragment;
import answer.android.phonemp3.fragment.PlayhistoryFragment;

/**
 * 主界面viewpager适配器
 * Created by Microanswer on 2017/6/14.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

  private static ArrayList<BaseFragment> fragments;

  public MainViewPagerAdapter(FragmentManager fm, BaseActivity baseActivity) {
    super(fm);
    if (fragments != null) {
      return;
    }
    fragments = new ArrayList<>();

    // 添加默认的fragment
    fragments.add(new AllMusicFragment().afterNew(baseActivity));
    fragments.add(new AblumFragment().afterNew(baseActivity));
    fragments.add(new ArtistFragment().afterNew(baseActivity));
    fragments.add(new LovesFragment().afterNew(baseActivity));
    fragments.add(new PlayhistoryFragment().afterNew(baseActivity));
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return fragments.get(position).getName();
  }

  @Override
  public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override
  public int getCount() {
    return fragments == null ? 0 : fragments.size();
  }

  public void onMainActivityCreate(MainActivity mainActivity) {
    for (BaseFragment f : fragments) {
      f.onMainActivityCreate(mainActivity);
    }
  }
}
