package cn.microanswer.phonemp3.ui.activitys;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.LayoutInflaterCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import answer.android.phonemp3.BuildConfig;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.services.CoreServices1;
import cn.microanswer.phonemp3.services.MyMediaController;
import cn.microanswer.phonemp3.ui.fragments.BaseFragment;
import cn.microanswer.phonemp3.ui.fragments.IndexFragment;
import cn.microanswer.phonemp3.ui.fragments.MainFragment;
import cn.microanswer.phonemp3.ui.views.Cell;
import cn.microanswer.phonemp3.ui.views.FitSystemLinearLayout;
import cn.microanswer.phonemp3.ui.views.Group;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

/**
 * <p>此版本的 PhoneMp3 力求不卡顿，遵循 Google 设计，分包明确，代码清晰易于阅读</p>
 * <p>
 * <p>对于此Activity的说明：</p>
 * <p>
 * 整个程序用且仅仅使用这一个 Activity， 所有不同界面使用 fragment 来实现，
 * 此 Activity 负责一些较为底层的事情。
 * </p>
 */
public class PhoneMp3Activity extends AppCompatActivity {

    /**
     * <pre>
     *     所有的 Fragment 都将渲染到这个容器中，这个容器是每个activity的根容器，android已经定义好的，
     *     所以我们只需要拿来使用即可。就不需要使用setContentView了，使用fragment填充。
     * </pre>
     */
    private static int FRAMENT_CONTAINER_ID = R.id.appContent;

    /**
     * <pre>
     *     在切换主题颜色时，任何一个View中如果其tag中包含该值，则认为该view的背景色需要随着主题色改变。
     * </pre>
     */
    private String MTHEME_BACKGROUND,

    /**
     * <pre>
     *     在切换主题颜色时，任何一个View中如果其tag中包含该值，则认为该view的前景色需要随着主题色改变。
     * </pre>
     */
    MTHEME_COLOR;

    /**
     * 日志记录器。
     */
    private static Logger logger = Logger.getLogger(PhoneMp3Activity.class);

    /**
     * <pre>
     *     标记activity是否被destory了。
     * </pre>
     */
    private boolean isDestory;

    /**
     * <pre>
     *     连接音乐播放服务的多媒体对象。
     * </pre>
     */
    private MediaBrowserCompat mediaBrowser;

    /**
     * <pre>
     *     保存需要接受多媒体控制器时间的类，实现了 MyMediaController 的类.
     *     都可以通过activity.addMyMediaController方法将自己设置为需要接受多媒体状态回调。
     * </pre>
     */
    private List<MyMediaController> myMediaControllerList; // 要接受音乐控制的fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 开启服务
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(this, CoreServices1.class);
        intent.setComponent(componentName);
        startService(intent);

        // 首先对这两个主题操作相关的变量赋值。
        MTHEME_BACKGROUND = getResources().getString(R.string.mtheme_background);
        MTHEME_COLOR = getResources().getString(R.string.mtheme_color);

        // 根据设置中保存的夜间模式和日间模式将界面变更为对应模式。
        if (SettingHolder.getSettingHolder().isDayMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // 为了使各view能随主题色改变，需要在渲染界面的时候，介入颜色的设置，通过这段代码，可以在渲染界面的时候设置
        // 自己需要的各种颜色
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), this);

        // 在 android 5.0+ 设备上，当状态栏和导航栏都设置为不透明属性，然后设置其绘制颜色为透明颜色时
        // 没有任何效果[我将这个设置方式叫做A]，将状态栏设置为不透明属性，将导航栏设置为透明属性，
        // 然后设置状态栏的绘制颜色为透明 此时可以达到状态栏全透明，但是导航栏半透明的效果[我将这个设置方式叫做B]。
        // 为了达到导航栏也全透明，将设置方式重新改为A方式。然后下面的代码将让A设置全部生效。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        // 初始化结合
        myMediaControllerList = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 添加测试环境标记。
        TextView textView = findViewById(R.id.textView);
        if (BuildConfig.isDev) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.format("%s %s", BuildConfig.desc, BuildConfig.VERSION_NAME));
        }

        // 下面这个判断要注意：
        // 在程序从未被打开过，然后进入程序时，savedInstanceState肯定是null的，这是就显示按App的流程，先显示
        // logo 界面。如果程序被打开过，然后又被关了，但是短时间内又被打开了，这时候的 savedInstanceState
        // 就不是 null 了，且，activity会自动根据上次的fragment显示出上次的界面，不需要做任何处理。
        if (savedInstanceState == null) {

            // 如果打开界面时提供了跳过logo界面的指令，则直接显示主界面。
            String skipIndex = getIntent().getStringExtra("skipIndex");
            if ("true".equals(skipIndex)) {
                push(MainFragment.class);
            } else {
                // 否则，默认展示 logo 界面。
                push(IndexFragment.class);
            }
        }

        // 创建音乐资源客户端
        // ComponentName componentName =
        //         new ComponentName(PhoneMp3Activity.this, CoreServices1.class);
        mediaBrowser = new MediaBrowserCompat(PhoneMp3Activity.this, componentName,
                connectionCallback, null);
        mediaBrowser.connect();

        // 为了将返回事件下发到每一个fragment里面，这里讲监听每个fragment加入，然后设置时为需要监听返回。
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(frlifecb, false);
    }

    /**
     * 与服务链接成功的回调对象。
     */
    private MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {

                /**
                 * 当服务与界面链接成功，此方法被调起
                 */
                @Override
                public void onConnected() {
                    super.onConnected();
                    try {

                        // 音乐控制器需要token来创建，所以此处先拿到token
                        MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                        // 构建音乐控制器
                        MediaControllerCompat mediaController =
                                new MediaControllerCompat(PhoneMp3Activity.this, token);

                        // 设置当前 activity为控制器
                        MediaControllerCompat
                                .setMediaController(PhoneMp3Activity.this, mediaController);

                        // 此代码可以不要的，现已被注释
                        // mediaController = MediaControllerCompat.getMediaController(PhoneMp3Activity.this);

                        // 注册控制器回调方法
                        mediaController.registerCallback(controllerCallback);

                        // 手动回调需要接受这个链接成功事件的类。
                        for (int i = 0; i < myMediaControllerList.size(); i++) {
                            myMediaControllerList.get(i).onBrowserConnected();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                }

            };

    /**
     * 此对象内部实现其实就是将各事件继续分发到各个需要接收事件的类中。
     */
    private MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onSessionReady() {
                    super.onSessionReady();
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onSessionReady();
                    }
                }

                @Override
                public void onSessionDestroyed() {
                    super.onSessionDestroyed();
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onSessionDestroyed();
                    }
                }

                @Override
                public void onSessionEvent(String event, Bundle extras) {
                    super.onSessionEvent(event, extras);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onSessionEvent(event, extras);
                    }
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onPlaybackStateChanged(state);
                    }
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onMetadataChanged(metadata);
                    }
                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    super.onQueueChanged(queue);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onQueueChanged(queue);
                    }
                }

                @Override
                public void onQueueTitleChanged(CharSequence title) {
                    super.onQueueTitleChanged(title);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onQueueTitleChanged(title);
                    }
                }

                @Override
                public void onExtrasChanged(Bundle extras) {
                    super.onExtrasChanged(extras);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onExtrasChanged(extras);
                    }
                }

                @Override
                public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
                    super.onAudioInfoChanged(info);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onAudioInfoChanged(info);
                    }
                }

                @Override
                public void onCaptioningEnabledChanged(boolean enabled) {
                    super.onCaptioningEnabledChanged(enabled);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onCaptioningEnabledChanged(enabled);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    super.onRepeatModeChanged(repeatMode);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onRepeatModeChanged(repeatMode);
                    }
                }

                @Override
                public void onShuffleModeChanged(int shuffleMode) {
                    super.onShuffleModeChanged(shuffleMode);
                    for (int i = 0; i < myMediaControllerList.size(); i++) {
                        MyMediaController m = myMediaControllerList.get(i);
                        m.onShuffleModeChanged(shuffleMode);
                    }
                }
            };

    private FragmentManager.FragmentLifecycleCallbacks frlifecb =
            new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                    super.onFragmentCreated(fm, f, savedInstanceState);
                    if (f instanceof BaseFragment) {
                        BaseFragment bf = (BaseFragment) f;
                        removeOnBackPressedCallback(bf);
                        addOnBackPressedCallback(bf);
                    }
                }

                @Override
                public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                    super.onFragmentDestroyed(fm, f);
                    if (f instanceof BaseFragment) {
                        BaseFragment bf = (BaseFragment) f;
                        removeOnBackPressedCallback(bf);
                    }
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        // 取消注册的回调并断开链接。
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            mediaController.unregisterCallback(controllerCallback);
        }
        mediaBrowser.disconnect();

        // 保证退出程序后，所有的任务在执行完成后关闭线程。
        Task.TaskHelper.getInstance().stopAfterLastTaskFlish();
        super.onDestroy();

        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(frlifecb);

        // 将界面是否被销毁标记为true。
        isDestory = true;
    }

    /**
     * 将一个界面显示出来并加入列队。
     *
     * @param fragmentClass 要显示的界面
     */
    public void push(Class<? extends BaseFragment> fragmentClass) {
        this.push(fragmentClass, null);
    }

    /**
     * 添加一个界面并且立刻显示
     *
     * @param fragmentClass 要显示的界面
     * @param argument      此界面创建时的参数
     */
    public void push(Class<? extends BaseFragment> fragmentClass, Bundle argument) {

        // 如果界面已经被销毁了，此方法不应该继续运行，此处的判断杜绝了错误的发生。
        if (isDestory) {
            return;
        }

        // 要操作fragment，需得使用manager
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

        // 为了方便和提升优化，使用类名作为tag保存fragment到manger中。
        String tag = fragmentClass.getSimpleName();

        // 先获取主显示的fragment
        Fragment primaryNavigationFragment = supportFragmentManager.getPrimaryNavigationFragment();

        // 如果没有主fragment显示，说明这是第一个被放入界面的fragment，这时候不需要有fragment的过渡动画
        // 所以，此处代码有效的解决了这个问题。
        if (primaryNavigationFragment != null) {

            // 设置动画效果。
            fragmentTransaction.setCustomAnimations(
                    R.animator.fragment_push_in,
                    R.animator.fragment_push_out,
                    R.animator.fragment_back_in,
                    R.animator.fragment_back_out
            );

            // 隐藏，以免新界面上的操作作用到以前的界面
            fragmentTransaction.hide(primaryNavigationFragment);
        }

        // 判断该 fragment 是否在 FragmentManager 中被管理着
        Fragment fragmentByTag = supportFragmentManager.findFragmentByTag(tag);

        if (null == fragmentByTag) {
            // 没有在 FragmentManager 中被管理。添加到 FragmentManager 中

            try {
                fragmentByTag = fragmentClass.newInstance();
                if (argument != null) fragmentByTag.setArguments(argument);

                // 将界面放入管理队列
                fragmentTransaction.add(FRAMENT_CONTAINER_ID, fragmentByTag, tag);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 添加到返回栈中，让用户按返回的时候，可以自动返回到上一个fragment。
            fragmentTransaction.addToBackStack(tag);
        } else {
            // 在 fragementManager 中被管理的，直接显示
            fragmentTransaction.show(fragmentByTag);
        }

        // 把传入的fragment界面设置为当前显示的主fragment
        fragmentTransaction.setPrimaryNavigationFragment(fragmentByTag);

        // 开始进行界面显示行为。
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * <pre>
     *     界面替换
     *     不同于 push 方法，此方法会将当前显示的界面替换了，而不是添加到列队。
     *     这样说：把现在显示的移除了，把要显示的加入。可能更加利于理解。
     * </pre>
     *
     * @param fragmentClass 要显示的界面
     */
    public void replace(Class<? extends BaseFragment> fragmentClass) {

        // 如果界面已经被销毁了，此方法不应该继续运行，此处的判断杜绝了错误的发生。
        if (isDestory) {
            return;
        }

        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 判断该 fragment 是否在 FragmentManager 中被管理着
        Fragment fragment = supportFragmentManager.findFragmentByTag(fragmentClass.getSimpleName());
        if (null == fragment) {
            // 没有在 FragmentManager 中被管理。添加到 FragmentManager 中
            try {
                fragment = fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 在 fragementManager 中被管理的，直接显示
        }
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.animator.fragment_replace_in,
                R.animator.fragment_replace_out
        );
        fragmentTransaction.replace(FRAMENT_CONTAINER_ID, fragment, fragmentClass.getSimpleName());
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 认为菜单键和键盘M按键都可以打开侧滑菜单
        if (keyCode == KeyEvent.KEYCODE_M || keyCode == KeyEvent.KEYCODE_MENU) {
            Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment();

            // 如果当前显示的主Fragment是MainFragment，
            // 则将菜单按钮的行为传递给MainFragment，让其可以弹出侧滑菜单。
            if (fragment instanceof MainFragment) ((MainFragment) fragment).onSystemMenuClick();
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * <pre>
     * 当界面上的，每一个view被创建时，这个方法都会被调用，从而有机会修改主题色。
     * 此方法的引用结合Oncreate方法里的：
     * LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), this);
     * </pre>
     *
     * @param parent  父view
     * @param name    要创建的view的类名
     * @param context 上下文
     * @param attrs   属性
     * @return 创建好了则返回该view实列，可以返回null，则会由系统创建。
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        // 先尝试使用系统创建view
        View v = getDelegate().createView(parent, name, context, attrs);

        // 如果上一步没有创建完成，尝试父类创建
        if (v == null) {
            v = super.onCreateView(parent, name, context, attrs);
        }

        // 如果 view 依然没有被实列化， 则手动将能够实列化的进行实列化
        if (v == null) {
            if (name.contains("FitSystemLinearLayout")) {
                v = new FitSystemLinearLayout(context, attrs);
            } else if (name.contains("Toolbar")) {
                v = new Toolbar(context, attrs);
            } else if (name.contains("Cell")) {
                v = new Cell(context, attrs);
            } else if (name.contains("Group")) {
                v = new Group(context, attrs);
            } else if (name.contains("RelativeLayout")) {
                v = new RelativeLayout(context, attrs);
            } else if (name.contains("TabLayout")) {
                v = new TabLayout(context, attrs);
            }
            // TODO 手动实列化更多 view
        }

        // 将创建好的view对齐进行主题色的适配。
        if (v != null && SettingHolder.getSettingHolder().isDayMode()) {
            // 解析主题需要更改的 view
            Object tagObject = v.getTag();

            if (tagObject instanceof String) {
                // 只有在日间模式允许使用自定义颜色的主题
                String tag = tagObject.toString();
                boolean b = tag.contains(MTHEME_BACKGROUND);
                boolean c = tag.contains(MTHEME_COLOR);
                if (b || c) {
                    Utils.UI._applyThemeColor(v, b, c);
                }
            }

        }
        return v;
    }

    /**
     * 将要接受播放事件回调的类加入队列。
     *
     * @param myMediaController 类
     */
    public void addMyMediaController(MyMediaController myMediaController) {

        // 如果队列里已经有了，则不用管了。
        if (myMediaControllerList.contains(myMediaController)) {
            return;
        }
        this.myMediaControllerList.add(myMediaController);
    }

    /**
     * 获取多媒体控制器。
     *
     * @return 多媒体控制器
     */
    public MediaControllerCompat mGetMediaController() {
        return MediaControllerCompat.getMediaController(this);
    }

    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    /**
     * 点击返回按钮
     */
    @Override
    public void onBackPressed() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment primaryNavigationFragment = supportFragmentManager.getPrimaryNavigationFragment();

        super.onBackPressed();

        // 如果当前显示的界面是主界面，此时按下返回按钮进行程序退出操作。
        // 否则会退回成白屏界面，因为已经没有fragment能显示了。
        if (primaryNavigationFragment instanceof MainFragment) {
            finish();
        }
    }

}
