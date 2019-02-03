package cn.microanswer.phonemp3.ui.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.IntroductionLogic;
import cn.microanswer.phonemp3.logic.answer.IntroductionAnswer;
import cn.microanswer.phonemp3.ui.IntroductionPage;

public class IntroductionFragment extends BaseFragment<IntroductionLogic> implements IntroductionPage, ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager viewPagerIntroduction;

    private LayoutInflater layoutInflater;
    private LinearLayout linearLayoutFlagContent;
    private Button buttonUseApp;
    private boolean buttonUseAppIsShow;

    private int scrollStatus = ViewPager.SCROLL_STATE_IDLE;

    private ArgbEvaluator argbEvaluator; // 处理颜色渐变的

    private int rgbs[]; // 定义一个数组，每个页面一种背景色

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_introduction, container, false);
        return inflate;
    }

    @Override
    IntroductionLogic newLogic() {
        return new IntroductionAnswer(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutInflater = LayoutInflater.from(getPhoneMp3Activity());
        viewPagerIntroduction = findViewById(R.id.viewPagerIntroduction);
        linearLayoutFlagContent = findViewById(R.id.linearLayoutFlagContent);
        buttonUseApp = findViewById(R.id.buttonUseApp);
        buttonUseApp.setOnClickListener(this);
        buttonUseAppIsShow = false;

        rgbs = new int[3];
        rgbs[0] = 0xff4bb7ec;
        rgbs[1] = 0xff424842;
        rgbs[2] = 0xff5d121b;
        argbEvaluator = new ArgbEvaluator();

        ((View) viewPagerIntroduction.getParent()).setBackgroundColor(rgbs[0]);
        getLogic().onPageCreated(savedInstanceState, getArguments());

        viewPagerIntroduction.addOnPageChangeListener(this);
        viewPagerIntroduction.setAdapter(new ViewPagerAdapter());
    }


    private boolean hasKnowSideWay; // 标记是否知道了要往那边滑动
    private float mv = 0;
    private int way = 0; // 1 = 往右边，2=往左边

    @Override
    public void onPageScrolled(int position, float v, int distance) {
        if (scrollStatus != ViewPager.SCROLL_STATE_IDLE && v != 0) {
            // 滑动中

            // 处理指示标志位置
            int measuredWidth = Math.round(linearLayoutFlagContent.getMeasuredWidth() / 3f);
            linearLayoutFlagContent.scrollTo(-(measuredWidth * position + Math.round(measuredWidth * v)), 0);

            // 处理颜色渐变
            int c1 = rgbs[position];

            // 计算目标是左边的页面还是右边的页面，在得知这件事之前，先默认用c1的颜色
            int c2 = c1;
            if (!hasKnowSideWay) {
                if (mv == 0) { // 第一次执行该方法，还不知道往那边
                    mv = v;
                } else { // 第二次执行，通过大小判断得出方向
                    if (mv < v) {
                        way = 2;
                    } else {
                        way = 1;
                    }
                    hasKnowSideWay = true;
                    mv = v;
                }
            } else {
                // Log.i("MMM", position+"<-");
                if (way == 1) {
                    c2 = rgbs[position + 1];
                    c1 = rgbs[position];
                } else if (way == 2) {
                    c2 = rgbs[position + 1];
                }

                int evaluate = (int) argbEvaluator.evaluate(v, c1, c2);
                ((View) viewPagerIntroduction.getParent()).setBackgroundColor(evaluate);
            }
        } else {
            hasKnowSideWay = false;
            mv = 0;
            way = 0;
            // Log.i("MMM", position+"");
        }
    }

    @Override
    public void onPageSelected(int i) {
        hasKnowSideWay = false;
        mv = 0;
        way = 0;

        if (!buttonUseAppIsShow && i == 2) {
            buttonUseAppIsShow = true;
            buttonUseApp.setVisibility(View.VISIBLE);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(buttonUseApp, "alpha", 0f, 1f);
            alpha.setDuration(300);
            alpha.start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        scrollStatus = i;
    }

    @Override
    public void onClick(View v) {
        getLogic().jumpToMain();
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int layoutId;
            int drawableId;
            if (position == 0) {
                layoutId = R.layout.layout_introduction0;
                drawableId = R.drawable.i0;
            } else if (position == 1) {
                layoutId = R.layout.layout_introduction1;
                drawableId = R.drawable.i1;
            } else {
                layoutId = R.layout.layout_introduction2;
                drawableId = R.drawable.i2;
            }
            View inflate = layoutInflater.inflate(layoutId, container, false);

            ImageView imageView = inflate.findViewById(R.id.imageview);
            Glide.with(getPhoneMp3Activity()).load(drawableId).into(imageView);

            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
