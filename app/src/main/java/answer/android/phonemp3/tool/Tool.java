package answer.android.phonemp3.tool;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.graphics.Palette;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONObject;
import org.xutils.common.util.MD5;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.BaseActivity;
import answer.android.phonemp3.activity.MainActivity;
import answer.android.phonemp3.activity.PlayActivity;
import answer.android.phonemp3.api.API;

/**
 * Created by Micro on 2017/6/12.
 */

public class Tool {
    private static HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();



    /**
     * 产生0到end但是不等于end的一个整形随机数
     * @param end
     * @return
     */
    public static int randomInt(int end) {
        return (int)Math.floor(Math.random() * end);
    }


    /**
     * Exception的错误堆栈转换为String
     */
    public static String Exception2String(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        stringWriter.flush();
        String s = stringWriter.toString();
        try {
            stringWriter.close();
        } catch (IOException e1) {
            Log.e("Tool", "将Throwable转换为String出错.");
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 是否有耳机
     *
     * @return
     */
    public static boolean hasHeadset(AudioManager a) {
        return a.isWiredHeadsetOn();
    }

    /**
     * 是否有耳机
     *
     * @param context
     * @return
     */
    public static boolean hasHeadset(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isWiredHeadsetOn();
    }

    public static void log2Service(final Context context) {
        API.useApi(context, "apkopenlog", new API.Config() {

            @Override
            public Map<String, String> getParam() {
                String position = "-";

                String version = getSelfVersion(context);
                String phoneInfo = "";

                try {
                    phoneInfo = new JSONObject()
                            .put("系统定制商", Build.BRAND)
                            .put("主板", Build.BOARD)
                            .put("系统启动程序版本号", Build.BOOTLOADER)
                            .put("设备参数", Build.DEVICE)
                            .put("屏幕参数", Build.DISPLAY)
                            .put("唯一参数", Build.FINGERPRINT)
                            .put("硬件名称", Build.HARDWARE)
                            .put("硬件制造商", Build.MANUFACTURER)
                            .put("版本号", Build.MODEL)
                            .put("产品名称", Build.PRODUCT)
                            .put("无线电固件版本", Build.getRadioVersion())
                            .put("硬件序列号", Build.SERIAL)
                            .put("SDK", Build.VERSION.SDK)
                            .put("SDK_INT", Build.VERSION.SDK_INT)
                            .put("固件版本", Build.VERSION.RELEASE)
                            .put("CODENAME", Build.VERSION.CODENAME)
                            .put("基带版本", Build.VERSION.INCREMENTAL)
                            .toString();
                } catch (Exception e){}

                HashMap<String, String> param = new HashMap<>();
                param.put("version", version);
                param.put("phoneinfo", phoneInfo);
                param.put("position", position);
                return param;
            }

            @Override
            public void success(JSONObject data) {
                Log.i("log2Service", data.toString());
            }

            @Override
            public void fail(Throwable e) {
                Log.i("log2Service", e.getMessage());
            }
        });
    }

    /**
     * 跳转到市场评分
     */
    public static void go2AppMarkt(Context c) {
        try {
            Uri uri = Uri.parse("market://details?id=" + c.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMimeType(java.io.File f) {
        return getMimeType(f.getAbsolutePath());
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param filePath
     */
    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    /**
     * 分享文件
     */
    public static void share(Context context, java.io.File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(getMimeType(file));
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, "answer.android.phonemp3", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }

        // Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, data);
        intent.putExtra(Intent.EXTRA_TEXT, file.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        Intent chooser = Intent.createChooser(intent, context.getString(R.string.shareTo));
        if (hasApplication(chooser, context)) {
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, R.string.noappcando, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断某个intent是否能被手机里的某个APP处理
     *
     * @param intent
     * @return
     */
    public static boolean hasApplication(Intent intent, Context context) {
        PackageManager packageManager = context.getPackageManager();
        //查询是否有该Intent的Activity
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        if (null == activities) {
            return false;
        }
        //activities里面不为空就有，否则就没有
        return activities.size() > 0;
    }

    public static String getNiceFileSize(long size) {
        float result = size;
        String sizeArray[] = {"b", "Kb", "Mb", "Gb", "Tb", "Pb", "Eb", "Zb", "Yb"};

        int doCount = 0; // 累计计算次数
        while (result >= 1024.0) {
            result /= 1024.0f;
            doCount++;
        }
        return ((float) (Math.round(result * 100)) / 100) + sizeArray[doCount];
    }


    /**
     * copy from : http://www.jianshu.com/p/469400940ee0
     * 判断service是否已经运行
     * 必须判断uid,因为可能有重名的Service,所以要找自己程序的Service,不同进程只要是同一个程序就是同一个uid,个人理解android系统中一个程序就是一个用户
     * 用pid替换uid进行判断强烈不建议,因为如果是远程Service的话,主进程的pid和远程Service的pid不是一个值,在主进程调用该方法会导致Service即使已经运行也会认为没有运行
     * 如果Service和主进程是一个进程的话,用pid不会出错,但是这种方法强烈不建议,如果你后来把Service改成了远程Service,这时候判断就出错了
     *
     * @param className Service的全名,例如PushService.class.getName()
     * @return true:Service已运行 false:Service未运行
     */
    public static boolean isServiceRunning(Context c, String className) {
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.uid == myUid && runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把一个字符串内容的第一个英文字符返回,
     * 如果这个字符串是中文字符串,返回第一个中文的拼音得第一个字母
     *
     * @param txt
     */
    public static String spellFirst(String txt) {
        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        if (TextUtils.isEmpty(txt)) {
            return "*";
        }

        char f = txt.charAt(0);

        if ((65 <= f && f <= 90) || (97 <= f && f <= 122)) {
            return String.valueOf(f).toUpperCase();
        }


        String results[] = new String[]{};
        try {
            results = PinyinHelper.toHanyuPinyinStringArray(f, hanyuPinyinOutputFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (results == null || results.length < 1) {
            return "#";
        } else {
            return String.valueOf(results[0].charAt(0)).toUpperCase();
        }
    }

    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return String
     */
    public static String getPinYin(String src) {
        if (src == null || "".endsWith(src)) {
            return "#";
        }
        char[] words = src.toCharArray();
        int wordCount = words.length;
        String[] pinyin = new String[wordCount];

        // 设置汉字拼音输出的格式
        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        StringBuffer sb = new StringBuffer();
        try {
            for (int i = 0; i < wordCount; i++) {
                // 判断能否为汉字字符
                // System.out.println(t1[i]);
                if (Character.toString(words[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    pinyin = PinyinHelper.toHanyuPinyinStringArray(words[i], hanyuPinyinOutputFormat);// 将汉字的几种全拼都存到t2数组中
                    if (pinyin == null || pinyin.length == 0) {
                        return "#";
                    }
                    sb.append(pinyin[0]);// 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，间接取出字符并连接到字符串t4后
                    sb.append(Character.toString(words[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Context activity) {
        Resources resources = activity.getResources();
        return resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

    /**
     * 获取自身应用的版本号
     *
     * @return version
     */
    public static String getSelfVersion(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 比较版本号
     *
     * @param version1
     * @param version2
     * @return 1=>version1>version2, 0=>version1==version2, -1=>version1<version2
     */
    public static int compareVersionJava(Comparable version1, Comparable version2) {
        return version1.compareTo(version2);
    }

    /**
     * 获取专辑图
     *
     * @param albumId
     * @return
     */
    public static Bitmap getAblumBitmap(Context context, String albumId) {
        if (TextUtils.isEmpty(albumId)) {
            return null;
        }

        int album_id = -1;

        try {
            album_id = Integer.parseInt(albumId);
        } catch (Exception e) {
            e.printStackTrace();
            album_id = -1;
        }

        if (album_id < 0) {
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, new BitmapFactory.Options());
            } catch (FileNotFoundException ex) {
                // ex.printStackTrace();
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 修复专辑图
     *
     * @param ablumId
     * @param ablumPicFile
     */
    public static boolean replarAblum(String ablumId, File ablumPicFile, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_art", ablumPicFile.getAbsolutePath());
        return 0 < contentResolver.update(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, contentValues, "_id=?", new String[]{ablumId});
    }

    public static String parseTime(long time) {
        return parseTime(time, ":");
    }

    // 把时间转化为友好的格式
    public static String parseTime(long time, String sp) {
        // 出去毫秒,换算成秒的
        time = time / 1000;

        // 换算后小于1秒的算0
        time = time < 1 ? 0L : time;

        StringBuilder stringBuilder = new StringBuilder();

        // 低于一分钟的不用算了, 直接拼接算了
        if (time < 60) {
            stringBuilder.append("00" + sp); // [1]
            if (time < 10) {
                stringBuilder.append("0"); // 注解[1]: 为了友好,还是要把空位填上0
            }
            stringBuilder.append(time);
        } else {
            // 计算时分秒

            // 分
            long minute = time / 60;

            // (分钟数得后,可能存在不到一分钟的秒,这时候反过来算剩下的秒)
            long second = time - (minute * 60);

            // 下面的小时,此程序不需要
            if (minute < 10) {
                stringBuilder.append("0");
            }

            stringBuilder.append(minute).append(sp);

            if (second < 10) {
                stringBuilder.append("0");
            }

            stringBuilder.append(second);
      /*
      // 求得小时数
      long hour = minute / 60;

      // (同理求得小时候剩下的分钟数)
      minute = minute - (hour * 60);
      */
        }
        return stringBuilder.toString();
    }

    public static String parseTime2(long time) {
        // 出去毫秒,换算成秒的
        time = time / 1000;

        // 换算后小于1秒的算0
        time = time < 1 ? 0L : time;


        // 低于一分钟的不用算了, 直接拼接算了
        if (time < 60) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("00分"); // [1]
            if (time < 10) {
                stringBuilder.append("0"); // 注解[1]: 为了友好,还是要把空位填上0
            }
            stringBuilder.append(time + "秒");
            return stringBuilder.toString();
        } else {
            // 计算时分秒

            // 分
            long minute = time / 60;

            // (分钟数得后,可能存在不到一分钟的秒,这时候反过来算剩下的秒)
            long second = time - (minute * 60);

            // 求得小时数
            long hour = minute / 60;

            // (同理求得小时候剩下的分钟数)
            minute = minute - (hour * 60);

            if (hour <= 0) {
                StringBuilder stringBuilder = new StringBuilder();
                if (minute < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(minute).append("分");
                if (second < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(second).append("秒");
                return stringBuilder.toString();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(hour).append("小时");
                if (minute < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(minute).append("分");
                if (second < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(second).append("秒");
                return stringBuilder.toString();
            }
        }
    }

    public static void cancelPlayNotify(Context context) {
        NotificationManagerCompat.from(context).cancel(7014);
    }

    /**
     * 系统状态栏通知
     */
    public static void showSystemStatusBarPlayNotify(Context context, String ticketMsg, Bitmap ablumImg, String title, String subtitle, boolean isplaying, boolean islove) {
        NotificationManagerCompat from = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(ticketMsg);
        builder.setSmallIcon(R.drawable.ic_music);

        Intent[] intents = new Intent[2];
        intents[0] = new Intent(context, MainActivity.class);
        intents[1] = new Intent(context, PlayActivity.class);
        PendingIntent pIntent = PendingIntent.getActivities(context, 7, intents, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pIntent);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_playnotify);

        remoteViews.setTextViewText(R.id.view_notify_title, title);
        remoteViews.setTextViewText(R.id.view_notify_subtitle, subtitle);
        if (ablumImg != null) {
            remoteViews.setImageViewBitmap(R.id.view_notify_img, ablumImg);
        } else {
            remoteViews.setImageViewResource(R.id.view_notify_img, R.mipmap.ic_launcher);
        }

        // 上一曲
        Intent intent = new Intent();
        intent.setAction(ACTION.ASK.UP);
        PendingIntent uppendinntent = PendingIntent.getBroadcast(context, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.view_notify_upbtn, uppendinntent);

        // 播放暂停
        intent = new Intent();
        intent.setAction(ACTION.ASK.PLAY$PAUSE);
        PendingIntent pausependinntent = PendingIntent.getBroadcast(context, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.view_notify_pause$playbtn, pausependinntent);

        // 下一曲
        intent = new Intent();
        intent.setAction(ACTION.ASK.NEXT);
        PendingIntent nextpendinntent = PendingIntent.getBroadcast(context, 13, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.view_notify_nextbtn, nextpendinntent);

        // 收藏
        intent = new Intent();
        intent.setAction(ACTION.ASK.LOVE);
        PendingIntent lovependinntent = PendingIntent.getBroadcast(context, 15, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.view_notify_lovebtn, lovependinntent);

        // 退出
        intent = new Intent();
        intent.setAction(ACTION.ASK.EXIT);
        PendingIntent exitpendinntent = PendingIntent.getBroadcast(context, 17, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.view_notify_closes, exitpendinntent);

        if (!isplaying) {
            remoteViews.setImageViewResource(R.id.view_notify_pause$playbtn, R.drawable.ic_play);
        } else {
            remoteViews.setImageViewResource(R.id.view_notify_pause$playbtn, R.drawable.ic_pause);
        }

        if (islove) {
            remoteViews.setImageViewResource(R.id.view_notify_lovebtn, R.drawable.ic_love);
        } else {
            remoteViews.setImageViewResource(R.id.view_notify_lovebtn, R.drawable.ic_unlove);
        }

        RemoteViews remoteViewSmall = new RemoteViews(context.getPackageName(), R.layout.view_playnotify_small);
        remoteViewSmall.setTextViewText(R.id.view_notify_small_title, title);
        remoteViewSmall.setTextViewText(R.id.view_notify_small_subtitle, subtitle);
        remoteViewSmall.setOnClickPendingIntent(R.id.view_notify_small_closes, exitpendinntent);
        if (ablumImg != null) {
            remoteViewSmall.setImageViewBitmap(R.id.view_notify_small_img, ablumImg);
        } else {
            remoteViewSmall.setImageViewResource(R.id.view_notify_small_img, R.mipmap.ic_launcher);
        }


//    builder.setContent();
        builder.setCustomContentView(remoteViewSmall);
        builder.setCustomBigContentView(remoteViews);
        // builder.setCustomHeadsUpContentView(null);
        // Notification build = builder.build();
        // build.bigContentView = remoteViews;
        // builder.setCustomHeadsUpContentView(remoteViews); // 大自定义view
        // builder.setCustomContentView(remoteViewSmall); // 普通zidingyiview
        from.notify(7014, builder.build());
    }

    /**
     * 更新监听器,当有新版本的时候onNew调用,否则onThis调用
     */
    public static interface UpdateListener {
        // 有新版本
        void onNew(String url, String name, String version, String newFunctions, String size, String updateat, String createdat, boolean mustDownload);

        // 没有新版本
        void onThis();
    }

    public static final void checkupdate(final Context context, final UpdateListener updateListener, boolean showDialog) {
        ProgressDialog progressDialog = null;
        if (showDialog) {
            progressDialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.updating));
        }
        final ProgressDialog finalProgressDialog = progressDialog;
        x.task().run(new Runnable() {
            @Override
            public void run() {
                final String myVersion = getSelfVersion(context);
                API.useApi(context, R.string.CHECKUPDATE_, new API.Config() {
                    @Override
                    public void success(JSONObject data) {
                        if (finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        try {
                            if (data.getInt("code") == 200) {
                                JSONObject dataIndata = data.getJSONObject("data");
                                String version = dataIndata.getString("version");
                                String name = dataIndata.getString("name");
                                String newfunction = dataIndata.getString("newfunction");
                                String size = dataIndata.getString("size");
                                String updateat = dataIndata.getString("updateat");
                                String createdat = dataIndata.getString("createdat");
                                String mustDownload = dataIndata.getString("mustDownload");
                                int i = compareVersionJava(version, myVersion);
                                if (i > 0) {
                                    // 有新版
                                    if (updateListener != null) {
                                        updateListener.onNew(dataIndata.getString("link"), name, version, newfunction, size, updateat, createdat, "1".equals(mustDownload));
                                    }
                                } else {
                                    // 没有新版
                                    if (updateListener != null) {
                                        updateListener.onThis();
                                    }
                                }
                                // alert(context, data.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(Throwable e) {
                        if (updateListener != null) {
                            updateListener.onThis();
                        }
                        // alert(context, Tool.Exception2String(e));

                        if (finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    public static interface OnClick extends BaseActivity.Click {
        String getBtnTxt();
    }

    public static final void alert(final Context c, String msg) {
        alert(c, msg, new OnClick() {
            @Override
            public String getBtnTxt() {
                return c.getResources().getString(R.string.alertok);
            }

            @Override
            public void d0() {

            }
        });
    }

    public static final void alert(Context c, String message, BaseActivity.Click... clicks) {
        alert(c, c.getResources().getString(R.string.tip), message, clicks);
    }

    public static final void alert(Context context, String title, String message, final BaseActivity.Click... clicks) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(title);
        b.setMessage(message);
        if (clicks != null && clicks.length <= 3) {
            for (int i = 0; i < clicks.length; i++) {
                switch (i) {
                    case 0:
                        if (clicks[0] != null) {
                            String txt = context.getResources().getString(R.string.alertok);
                            if (clicks[0] instanceof OnClick) {
                                txt = ((OnClick) clicks[0]).getBtnTxt();
                            }
                            b.setPositiveButton(txt, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clicks[0].d0();
                                }
                            });
                        }
                        break;
                    case 1:
                        if (clicks[1] != null) {
                            String txt = context.getResources().getString(R.string.cancel);
                            if (clicks[1] instanceof OnClick) {
                                txt = ((OnClick) clicks[1]).getBtnTxt();
                            }
                            b.setNegativeButton(txt, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clicks[1].d0();
                                }
                            });
                        }
                        break;
                    case 2:
                        if (clicks[2] != null) {
                            String txt = context.getResources().getString(R.string.more);
                            if (clicks[2] instanceof OnClick) {
                                txt = ((OnClick) clicks[2]).getBtnTxt();
                            }
                            b.setNeutralButton(txt, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clicks[2].d0();
                                }
                            });
                        }
                        break;
                }
            }
        }
        b.show();
    }


    // 下载封面
    public static final File downloadCover(String httpurl) {
        File coverdir = new File(Environment.getExternalStorageDirectory(), "Android");
        coverdir = new File(coverdir, "data");
        coverdir = new File(coverdir, "answer.android.phonemp3");
        coverdir = new File(coverdir, "cover");
        if (!coverdir.exists()) {
            if (!coverdir.mkdirs()) {
                return null;
            }
        }
        File f = new File(coverdir, "cover");
        if (null == httpurl) {
            if (f.exists()) {
                return f;
            } else {
                return null;
            }
        }
        try {
            URL uri = new URL(httpurl);
            HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
            int responseCode = urlConnection.getResponseCode();
            // Log.i("Cover", "responseCode:" + responseCode);
            if (responseCode == 200) {
                FileOutputStream fileOutputStream = new FileOutputStream(f);
                // urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                // Log.i("Cover", "inputStream.available:" + inputStream.available());
                byte[] data = new byte[1024];
                int dataSize = 0;
                while ((dataSize = inputStream.read(data)) != -1) {
                    fileOutputStream.write(data, 0, dataSize);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                Log.i("Cover", "封面下载完成:" + httpurl);
                return f;
            } else {
                urlConnection.disconnect();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 比较版本
     *
     * @param version1
     * @param version2
     * @return 1=>version1>version2, 0=>version1==version2, -1=>version1<version2
     */
    @Deprecated
    public static int compareVersionOwn(String version1, String version2) {
        String[] version1array = version1.split(".");
        String[] version2array = version2.split(".");
        for (int i = 0; i < version1array.length; i++) {
            int flag = Integer.valueOf(version1array[i]);
            int flag2 = Integer.valueOf(version2array[i]);
            if (flag > flag2) {
                return 1;
            } else if (flag == flag2) {
                // continue;
            } else {
                if (flag < flag2) {
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * 判断当前是否在主线程
     *
     * @return
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    public static class ColorUtil {

        public static int getStatusBarColor(int color) {
            float[] arrayOfFloat = new float[3];
            Color.colorToHSV(color, arrayOfFloat);
            arrayOfFloat[2] *= 0.9F;
            return Color.HSVToColor(arrayOfFloat);
        }

        public static int getBlackWhiteColor(int color) { //根据颜色的亮度转换为黑白色
            double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
            if (darkness >= 0.5) {
                return Color.WHITE;
            } else return Color.BLACK;
        }

        public static int getOpaqueColor(@ColorInt int paramInt) {
            return 0xFF000000 | paramInt;
        }

        public static
        @Nullable
        Palette.Swatch getMostPopulousSwatch(Palette palette) {
            Palette.Swatch mostPopulous = null;
            if (palette != null) {
                for (Palette.Swatch swatch : palette.getSwatches()) {
                    if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                        mostPopulous = swatch;
                    }
                }
            }
            return mostPopulous;
        }
    }


}
