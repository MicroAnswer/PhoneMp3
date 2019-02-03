package cn.microanswer.phonemp3.logic.answer;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.API;
import cn.microanswer.phonemp3.entity.Config;
import cn.microanswer.phonemp3.entity.Config_Table;
import cn.microanswer.phonemp3.logic.IndexLogic;
import cn.microanswer.phonemp3.ui.IndexPage;
import cn.microanswer.phonemp3.ui.fragments.IntroductionFragment;
import cn.microanswer.phonemp3.ui.fragments.MainFragment;
import cn.microanswer.phonemp3.util.ApplicationUtil;
import cn.microanswer.phonemp3.util.HttpUtil;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;

public class IndexAnswer extends BaseAnswer<IndexPage> implements IndexLogic {

    private static Logger logger = Logger.getLogger(IndexAnswer.class);

    public IndexAnswer(IndexPage page) {
        super(page);
    }

    private String coverUrl = null; // 保存封面地址
    private boolean isKeepJump = false; // 保存是否直接跳转应用内的变量

    @Override
    public void jumpToAdLink() {


    }

    @Override
    public void onPageCreated(Bundle args, Bundle argments) {
        isKeepJump = false;

        // 设置 app 名称和版本号到 logo下面。
        Context context = getPhoneMp3Activity();
        String appName = context.getString(R.string.app_name);
        getPage().displVersionInfo(appName + "\nv" + ApplicationUtil.getVersion(getPhoneMp3Activity()));


        if (null == coverUrl) {

            /*
             * 封面加载逻辑：<p>
             *     先从数据库加载现有的封面信息， 如果有，则显示，没有则加载今日的封面，加载完成后显示。
             *     如果有的情况，判断今天是否冲网上加载过封面，如果加载过，则今天不再加载，如果没有加载过，则
             *     加载，并保存（不显示）。
             * </p>
             */
            Task.TaskHelper.getInstance().run(new Task.ITask<Void, String>() {
                @Override
                public String run(Void param) throws Exception {
                    logger.i("从数据库加载封面数据...");
                    // 从数据库加载
                    final Config config = SQLite.select().from(Config.class).where(Config_Table._Key.eq("cover")).querySingle();

                    if (config == null) {
                        logger.i("从数据库中没有获取到信息...从网上加载...");
                        // 数据库没有封面信息， 此时加载封面信息并显示。
                        String s = HttpUtil.postCnMicroanswer(API.METHOD.GET_COVER, null);
                        if (!TextUtils.isEmpty(s)) {
                            JSONObject res = new JSONObject(s);
                            if (res.has("code") && res.getInt("code") == 200) {
                                // 加载完成。保存数据库
                                JSONObject object = res.getJSONArray("data").getJSONObject(0);
                                Config config2 = new Config();
                                config2.setKey("cover");
                                config2.setDesc(String.valueOf(System.currentTimeMillis()));
                                config2.setValue(object.toString());
                                long insert = config2.insert();
                                String rl = object.getString("url");
                                logger.i("从网络加载封面完成：" + rl + "，已插入数据库，结果：" + insert);
                                return rl;
                            }
                        }
                    } else {
                        // 数据库有。那么立刻显示
                        String value = config.getValue();
                        final JSONObject data = new JSONObject(value);

                        logger.i("从数据库中加载封面成功...");
                        // 检测今天是否加载过， 如果没有加载过则加载下来，下一次打开app的时候会显示。
                        long lastCheckCoverTime = Long.valueOf(config.getDesc());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(lastCheckCoverTime);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.CHINA);
                        String lastCheckDay = simpleDateFormat.format(calendar.getTime());

                        logger.i("判断上次加载的时间和这次加载的时间是否同一天...");
                        // 此时时间和上次加载的时间不是同一天，则认为今天还没有加载。
                        if (!lastCheckDay.equals(simpleDateFormat.format(new Date()))) {
                            logger.i("上次加载时间不是今天，尝试加载新的封面");
                            Task.TaskHelper.getInstance().run(new Task.ITask<Void, Void>() {

                                @Override
                                public Void run(Void param) throws Exception {
                                    String s = HttpUtil.postCnMicroanswer(API.METHOD.GET_COVER, null);
                                    if (!TextUtils.isEmpty(s)) {
                                        JSONObject res = new JSONObject(s);
                                        if (res.has("code") && res.getInt("code") == 200) {
                                            // 加载完成。保存数据库
                                            JSONObject object = res.getJSONArray("data").getJSONObject(0);
                                            config.setValue(object.toString());
                                            config.setDesc(System.currentTimeMillis() + "");
                                            boolean update = config.update();
                                            logger.i("今天加载封面并保存数据库结果：" + update);
                                        }
                                    }

                                    return null;
                                }

                                @Override
                                public void onError(Exception e) {
                                    super.onError(e);
                                    logger.e("请求封面出错：" + e.getMessage());
                                }
                            });
                        } else {
                            logger.i("上次加载的时间是今天，今天不再加载封面。");
                        }

                        return data.getString("url");

                    }


                    return null;
                }

                @Override
                public void afterRun(String value) {
                    super.afterRun(value);
                    if (!TextUtils.isEmpty(value)) {
                        coverUrl = value;
                        getPage().displayCover(value);
                    }
                }

                @Override
                public void onError(Exception e) {
                    super.onError(e);
                    getPage().alert(e.getMessage());
                }
            });
        } else {
            getPage().displayCover(coverUrl);
        }

        // logo界面显示 3 秒
        Task.TaskHelper.getInstance().runAfter(() -> {
            // 在用户按下home按钮，退出了软件的时候，是不能进行fragment操作了的
            // 所以下面进行一个判断，并保存一个变量，这个变量在恢复使用软件的时候直接跳转对应界面
            boolean visible = getPage().mIsVisible();
            if (visible) {
                jumpInApplication();
            } else {
                isKeepJump = true;
            }
        }, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isKeepJump) {
            Task.TaskHelper.getInstance().runAfter(this::jumpInApplication, 500);
        }
    }

    private void jumpInApplication() {
        boolean displayIntroduction = SettingHolder.getSettingHolder().isDisplayIntroduction();
        if (displayIntroduction) {
            getPhoneMp3Activity().replace(IntroductionFragment.class);
        } else {
            getPhoneMp3Activity().replace(MainFragment.class);
        }
        isKeepJump = false;
    }
}
