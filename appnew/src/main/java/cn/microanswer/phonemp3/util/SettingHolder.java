package cn.microanswer.phonemp3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * 设置内容保存器。
 */
public class SettingHolder {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Logger logger = null;

    private int colorPrimary = Color.parseColor("#c51162"); // 自定义颜色主题的时候，自定义的颜色值。

    public boolean isDayMode() {
        return sharedPreferences.getBoolean("isDayMode", true);
    }

    public SettingHolder setDayMode(boolean isDayMode) {
        editor.putBoolean("isDayMode", isDayMode);
        editor.putBoolean("isNightMode", !isDayMode).apply();
        return this;
    }

    public boolean isNightMode() {
        return sharedPreferences.getBoolean("isNightMode", false);
    }

    public SettingHolder setNightMode(boolean isNightMode) {
        editor.putBoolean("isDayMode", !isNightMode);
        editor.putBoolean("isNightMode", isNightMode).apply();
        return this;
    }

    public SettingHolder setColorPrimary(int colorPrimary) {
        editor.putInt("colorPrimary", colorPrimary).apply();
        return this;
    }

    public int getColorPrimary() {
        return sharedPreferences.getInt("colorPrimary", colorPrimary);
    }

    public SettingHolder setDisplayIntroduction(boolean displayIntroduction) {
        editor.putBoolean("isDisplayIntroduction", displayIntroduction).apply();
        return this;
    }

    public boolean isDisplayIntroduction() {
        return sharedPreferences.getBoolean("isDisplayIntroduction", false);
    }

    public void commit() {
        editor.apply();
    }

    public void init(Context context) {
        this.logger = Logger.getLogger(getClass());
        this.sharedPreferences = context.getSharedPreferences("set", Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    // ================================================
    private static SettingHolder settingHolder;

    private SettingHolder() {
    }

    public static SettingHolder getSettingHolder() {
        if (settingHolder == null) {
            settingHolder = new SettingHolder();
        }
        return settingHolder;
    }
}
