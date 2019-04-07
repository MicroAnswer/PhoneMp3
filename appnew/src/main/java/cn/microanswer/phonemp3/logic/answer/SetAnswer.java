package cn.microanswer.phonemp3.logic.answer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.ACTION;
import cn.microanswer.phonemp3.logic.SetLogic;
import cn.microanswer.phonemp3.ui.SetPage;
import cn.microanswer.phonemp3.ui.fragments.CodeUseFragment;
import cn.microanswer.phonemp3.ui.fragments.LogFragment;
import cn.microanswer.phonemp3.ui.fragments.ScannFragment;
import cn.microanswer.phonemp3.ui.fragments.WebFragment;
import cn.microanswer.phonemp3.ui.views.colorpicker.ColorPickerDialog;
import cn.microanswer.phonemp3.ui.views.colorpicker.ColorPickerDialogListener;
import cn.microanswer.phonemp3.util.Logger;
import cn.microanswer.phonemp3.util.SettingHolder;
import cn.microanswer.phonemp3.util.Task;
import cn.microanswer.phonemp3.util.Utils;

public class SetAnswer extends BaseAnswer<SetPage> implements SetLogic {
    private Logger logger = new Logger(getClass());
    private Task<Void, String> checkUpdateTask;

    public SetAnswer(SetPage page) {
        super(page);
    }

    @Override
    public void onPageCreated(Bundle savedInstanceState, Bundle arguments) {
        // 加载并显示版本信息
        loadVersionInfo();
    }

    // 加载并显示版本信息
    private void loadVersionInfo() {
        Task.TaskHelper.getInstance().run(new Task.ITask<Context, String>() {
            @Override
            public Context getParam() {
                return getPhoneMp3Activity();
            }

            @Override
            public String run(Context param) throws Exception {
                return Utils.APP.getVersion(param);
            }

            @Override
            public void afterRun(String value) {
                getPage().setVersion(value);
            }
        });
    }

    // 检查更新
    private void checkUpdate() {
        if (checkUpdateTask == null || checkUpdateTask.isFinish()) {
            checkUpdateTask = Task.TaskHelper.getInstance().run(new Task.ITask<Void, String>() {
                @Override
                public Void getParam() {
                    getPage().setVersion("检查更新中...");
                    return super.getParam();
                }

                @Override
                public String run(Void param) throws Exception {
                    SystemClock.sleep(5000);
                    return "2.2.0";
                }

                @Override
                public void afterRun(String value) {
                    super.afterRun(value);
                    getPage().setVersion(value);
                    Toast.makeText(getPhoneMp3Activity(), "检查更新完成", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // logger.w("正在进行检查更新...");
        }
    }

    @Override
    public void onSetItemClick(int cellId) {
        if (cellId == R.id.cellSetItemScannMusic) {
            getPhoneMp3Activity().push(ScannFragment.class);
        } else if (cellId == R.id.cellSetItemThemeColor) {
            preDoChangeThemeColor();
        } else if (cellId == R.id.cellCheckUpdate) {
            checkUpdate();
        } else if (cellId == R.id.cellGithub) {
            jumpGithub();
        } else if (cellId == R.id.cellCodeUse) {
            doCodeUse();
        } else if (cellId == R.id.cellLog) {
            doCheckLog();
        }
    }

    private void doCheckLog() {
        getPhoneMp3Activity().push(LogFragment.class);
    }

    private void jumpGithub() {
        Uri uri = Uri.parse(getPhoneMp3Activity().getString(R.string.github));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getPhoneMp3Activity().startActivity(intent);
    }

    // 改变应用中的主色调 - 处理一些判断
    private void preDoChangeThemeColor() {
        boolean dayMode = SettingHolder.getSettingHolder().isDayMode();
        if (!dayMode) {
            getPage().confirm(getPhoneMp3Activity().getString(R.string.changeThemeColorTip), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 改为日间模式
                    SettingHolder.getSettingHolder().setDayMode(true).commit();
                    // 重启activity
                    getPhoneMp3Activity().recreate();
                }
            }, null);
            return;
        }
        doChangeThemeColor();
    }

    // 弹出自定义颜色界面
    private void doChangeThemeColor() {
        final boolean[] isSelected = {false}; // 标记有没有选择颜色
        final ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setColor(SettingHolder.getSettingHolder().getColorPrimary())
                .setShowAlphaSlider(true)
                .setColorPickerDialogListener(new ColorPickerDialogListener() {
                    @Override
                    public void onColorSelected(int dialogId, int color) {
                        // 保存选择的颜色。
                        SettingHolder.getSettingHolder().setColorPrimary(color).commit();

                        isSelected[0] = true;
                    }

                    @Override
                    public void onDialogDismissed(int dialogId) {
                        if (isSelected[0]) {

                            // 发送通知，颜色改变了
                            getPhoneMp3Activity().mGetMediaController().getTransportControls().sendCustomAction(ACTION.THEME_COLOR_CHANGE, null);
                            // 重启 activity
                            getPhoneMp3Activity().recreate();
                        }
                    }
                }).create();
        colorPickerDialog.show(getPhoneMp3Activity().getFragmentManager(), "t");
    }

    // 开源引用点击
    private void doCodeUse() {
        getPhoneMp3Activity().push(CodeUseFragment.class);
    }
}
