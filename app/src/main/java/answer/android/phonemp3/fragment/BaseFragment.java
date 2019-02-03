package answer.android.phonemp3.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.BaseActivity;
import answer.android.phonemp3.activity.MainActivity;

/**
 * Created by Microanswer on 2017/6/14.
 */

public abstract class BaseFragment extends Fragment {
  private String name;

  // 用于缓存fragment显示的view,避免每次都重新inflat一次view
  private View fragmentView;

  /**
   * 当这个Fragment被new出来之后,紧接着这个方法会被调用
   * @param baseActivity
   * @return
   */
  public BaseFragment afterNew(BaseActivity baseActivity){return this;}

  public void setFragmentView(View fragmentView) {
    this.fragmentView = fragmentView;
  }

  protected View findViewById(int id) {
    if (null == fragmentView) {
      return null;
    }
    return fragmentView.findViewById(id);
  }

  public View getFragmentView() {

    return fragmentView;
  }

  protected void showLoadingView(){
    if (getFragmentView() != null) {
      LinearLayout linearLayout = (LinearLayout) findViewById(R.id.emptyview);
      linearLayout.setVisibility(View.VISIBLE);
    }
  }

  protected void showEmptyView(){
    if (getFragmentView() != null) {
      LinearLayout linearLayout = (LinearLayout) findViewById(R.id.emptyview);
      linearLayout.removeAllViews();
      linearLayout.addView(View.inflate(getContext(), R.layout.view_emptyview, null));
      linearLayout.setVisibility(View.VISIBLE);
    }
  }

  protected void hideLoad_EmptyView(){
    if (getFragmentView() != null) {
      LinearLayout linearLayout = (LinearLayout) findViewById(R.id.emptyview);
      linearLayout.setVisibility(View.GONE);
    }
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name == null ? "" : name;
  }

  public BaseActivity getBaseActivity() {
    return (BaseActivity) getActivity();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    fragmentView = null;
    // Log.i("BaseFragment", "onDestroy");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    // Log.i("BaseFragment", "onDestroyView");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    // Log.i("BaseFragment", "onDetach");
  }

  public void onMainActivityCreate(MainActivity mainActivity) {

  }
}
