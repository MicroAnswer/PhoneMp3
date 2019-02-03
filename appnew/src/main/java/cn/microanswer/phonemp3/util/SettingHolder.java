package cn.microanswer.phonemp3.util;

import android.graphics.Color;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cn.microanswer.phonemp3.PhoneMp3Application;

/**
 * 设置内容保存器。
 */
public class SettingHolder {
    private boolean isInited = false; // 标记是否初始化
    private JSONObject setObject;
    private File setFile;
    private Logger logger = null;

    private boolean isDayMode = true; // 是否日间模式
    private boolean isNightMode = false; // 是否夜间模式
    private boolean displayIntroduction = true; // 是否显示引导
    private int colorPrimary = Color.parseColor("#c51162"); // 自定义颜色主题的时候，自定义的颜色值。

    public boolean isDayMode() {
        return isDayMode;
    }

    public SettingHolder setDayMode(boolean isDayMode) {
        checkInited();
        try {
            this.isDayMode = isDayMode;
            this.isNightMode = !this.isDayMode;
            setObject.put("isDayMode", this.isDayMode);
            setObject.put("isNightMode", this.isNightMode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e.getMessage());
        }
        return this;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public SettingHolder setNightMode(boolean isNightMode) {
        checkInited();
        try {
            this.isNightMode = isNightMode;
            this.isDayMode = !this.isNightMode;
            setObject.put("isNightMode", this.isNightMode);
            setObject.put("isDayMode", this.isDayMode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e.getMessage());
        }
        return this;
    }

    public SettingHolder setColorPrimary(int colorPrimary) {
        checkInited();
        try{
            this.colorPrimary = colorPrimary;
            setObject.put("colorPrimary", this.colorPrimary);
        }catch (Exception e){
            e.printStackTrace();
            logger.e(e.getMessage());
        }
        return this;
    }

    public int getColorPrimary() {
        return colorPrimary;
    }

    public SettingHolder setDisplayIntroduction(boolean displayIntroduction) {
        checkInited();
        try{
            this.displayIntroduction = displayIntroduction;
            setObject.put("displayIntroduction", this.displayIntroduction);
        }catch (Exception e){
            e.printStackTrace();
            logger.e(e.getMessage());
        }
        return this;
    }
    public boolean isDisplayIntroduction (){
        return displayIntroduction;
    }

    public void commit() {
        Task.TaskHelper.getInstance().run(new Task.ITask() {
            @Override
            public Object run(Object param) throws Exception {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(setFile), "UTF-8");
                writer.write(setObject.toString(2));
                writer.flush();
                writer.close();
                return null;
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                logger.e(e.getMessage());
            }
        });
    }

    private void checkInited() {
        if (!isInited) {
            throw new RuntimeException("设置未初始化，或初始化失败。");
        }
    }

    private void internalInit(JSONObject setObject) throws Exception {
        this.setObject = setObject;
        if (setObject.has("isDayMode")) isDayMode = setObject.getBoolean("isDayMode");
        if (setObject.has("isNightMode")) isNightMode = setObject.getBoolean("isNightMode");
        if (setObject.has("colorPrimary")) colorPrimary = setObject.getInt("colorPrimary");
        if (setObject.has("displayIntroduction")) displayIntroduction = setObject.getBoolean("displayIntroduction");

        this.isInited = true;
    }

    public void init() {
        this.logger = Logger.getLogger(getClass());

        try {
            if (!PhoneMp3Application.DIR_SET.exists()) {
                PhoneMp3Application.DIR_SET.mkdirs();
            }

            setFile = new File(PhoneMp3Application.DIR_SET, "set.json");

            if (!setFile.exists()) {
                setFile.createNewFile();

            }

            // 读取文件内容
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(setFile), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            char[] data = new char[512];
            int dataSize = 0;
            StringBuilder stringBuilder = new StringBuilder();

            while ((dataSize = bufferedReader.read(data)) != -1) {
                stringBuilder.append(data, 0, dataSize);
            }

            bufferedReader.close();


            String setContent = stringBuilder.toString();

            if (TextUtils.isEmpty(setContent)) {
                setContent = "{}";
            }
            internalInit(new JSONObject(setContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================================
    private static SettingHolder settingHolder;
    private SettingHolder (){}
    public static SettingHolder getSettingHolder() {
        if (settingHolder == null) {
            settingHolder = new SettingHolder();
        }
        return settingHolder;
    }
}
