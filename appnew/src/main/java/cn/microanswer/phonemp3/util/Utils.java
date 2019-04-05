package cn.microanswer.phonemp3.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import androidx.core.graphics.drawable.DrawableCompat;
import cn.microanswer.phonemp3.Database;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.Music_Table;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;
import cn.microanswer.phonemp3.ui.dialogs.AlertDialog;
import cn.microanswer.phonemp3.ui.dialogs.ConfirmDialog;
import cn.microanswer.phonemp3.ui.views.Cell;
import cn.microanswer.phonemp3.ui.views.FitSystemLinearLayout;
import cn.microanswer.phonemp3.ui.views.Group;

/**
 * 工具类
 * Created by Microanswer on 2018/1/11.
 */

public class Utils {

    /**
     * 视图相关工具类
     */
    public static class UI {
        /**
         * 获取状态栏高度
         *
         * @param context 上下文
         * @return 状态栏高度
         */
        public static int getStatusBarHeight(Context context) {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }

        /**
         * 获取导航栏高度
         *
         * @param context
         * @return
         */
        public static int getNavigationBarHeight(Context context) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        }

        /**
         * 将dp转化为px
         *
         * @param dp
         * @return
         */
        public static int dp2px(Context c, float dp) {
            DisplayMetrics metrics = c.getResources().getDisplayMetrics();
            float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
            int res = (int) (val + 0.5); // Round
            // Ensure at least 1 pixel if val was > 0
            return res == 0 && val > 0 ? 1 : res;
        }

        /**
         * 将px 转化为dp
         *
         * @param context
         * @param px
         * @return
         */
        public static int px2dp(Context context, int px) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }

        /**
         * 弹出警告框
         *
         * @param msg
         * @return
         */
        public static AlertDialog alert(Context context, String msg) {
            return alert(context, null, msg);
        }

        /**
         * 弹出警告框
         *
         * @param context
         * @param title
         * @param msg
         * @return
         */
        public static AlertDialog alert(Context context, String title, String msg) {
            AlertDialog alertDialog = new AlertDialog(context, title, msg);
            alertDialog.show();
            return alertDialog;
        }

        /**
         * 当用户选择自定义颜色的主题的时候，和 应用自定义颜色 时，该方法调起， 方法应该实现：
         * <p>
         * 将指定的 view 的前景色或则背景色改成对应颜色
         * </p>
         *
         * @param view             要改变颜色的 view
         * @param backgroundChange 是否改变背景色
         * @param colorChange      是否改变前景色
         */
        public static void _applyThemeColor(View view, boolean backgroundChange, boolean colorChange) {
            int colorPrimary = SettingHolder.getSettingHolder().getColorPrimary();

            // 改变背景色
            if (backgroundChange) {
                if (view instanceof FitSystemLinearLayout) {
                    FitSystemLinearLayout fitSystemLinearLayout = (FitSystemLinearLayout) view;
                    fitSystemLinearLayout.setBottomColor(colorPrimary);
                    fitSystemLinearLayout.setLeftColor(colorPrimary);
                    fitSystemLinearLayout.setTopColor(colorPrimary);
                    fitSystemLinearLayout.setRightColor(colorPrimary);
                } else if (view instanceof Button) {
                    Drawable background = view.getBackground();
                    Log.i("background", background.getClass().getName());

                    // int dp8 = UI.dp2px(view.getContext(), 8f);
                    // int dp10 = UI.dp2px(view.getContext(), 10f);
                    // int outRadius = 100;
                    // int innerRadius = 0;
                    // /* 圆角矩形 */
                    // float[] outerRadii = {outRadius, outRadius, 0, 0, 0, 0, outRadius, outRadius};//左上x2,右上x2,右下x2,左下x2，注意顺序（顺时针依次设置）
                    // int spaceBetOutAndInner = outRadius - innerRadius;//内外矩形间距
                    // RectF inset = new RectF(spaceBetOutAndInner, spaceBetOutAndInner, spaceBetOutAndInner, spaceBetOutAndInner);
                    // float[] innerRadii = {innerRadius, innerRadius, 0, 0, 0, 0, 0, 0};//内矩形 圆角半径
                    // RoundRectShape roundRectShape = new RoundRectShape(outerRadii, inset, innerRadii);
                    // ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
                    // // shapeDrawable.setColorFilter(colorPrimary, PorterDuff.Mode.CLEAR);
                    // InsetDrawable insetDrawable = new InsetDrawable(shapeDrawable, 0, dp8, 0, dp8);
                    // Button button = (Button)view;
                    if (Build.VERSION.SDK_INT < 21) {
                        //     button.setBackgroundDrawable(insetDrawable);
                    } else {
                        //     int [][] stats = new int[][]{{}};
                        //     int [] color = new int[]{Color.parseColor("#f0f0f0")};
                        //     ColorStateList colorStateList = new ColorStateList(stats, color);
                        //     RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, insetDrawable, new ColorDrawable(colorPrimary));
                        //     button.setBackground(rippleDrawable);
                        if (background instanceof RippleDrawable) {
                            RippleDrawable rpd = (RippleDrawable) background;
                            rpd.findDrawableByLayerId(0);

                            int[][] stats = new int[][]{{}};
                            int[] color = new int[]{Color.parseColor("#f0f0f0")};
                            ColorStateList colorStateList = new ColorStateList(stats, color);
                            rpd.setColor(colorStateList);
                        }
                    }

                } else {
                    view.setBackgroundColor(colorPrimary);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(new ColorDrawable(colorPrimary));
                    } else {
                        view.setBackgroundDrawable(new ColorDrawable(colorPrimary));
                    }
                }
            }

            // 改变前景色
            if (colorChange) {
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(colorPrimary);
                } else if (view instanceof Cell) {
                    Cell cell = (Cell) view;
                    cell.setIconColor(colorPrimary);
                } else if (view instanceof Group) {
                    Group group = (Group) view;
                    group.setTitleColor(colorPrimary);
                } else if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    Drawable drawable = imageView.getDrawable();
                    if (null != drawable) {
                        Drawable drawable1 = DrawableCompat.wrap(drawable).mutate();
                        DrawableCompat.setTint(drawable1, colorPrimary);
                        imageView.setImageDrawable(drawable1);
                    }
                } else if (view instanceof ProgressBar) {
                    ProgressBar progressBar = (ProgressBar) view;

                } else if (view instanceof Button) {
                    Button button = (Button) view;
                    button.setTextColor(colorPrimary);
                }
            }


        }

        /**
         * 根据主题颜色着色
         *
         * @param context
         * @param resource
         * @return
         */
        public static Drawable tintResourceWithThemeColor(Context context, int resource) {
            // 根据主题色着色
            Drawable drawable1 = DrawableCompat.wrap(context.getResources().getDrawable(resource)).mutate();
            if (SettingHolder.getSettingHolder().isDayMode()) {
                DrawableCompat.setTint(drawable1, SettingHolder.getSettingHolder().getColorPrimary());
            }
            return drawable1;
        }

        public static DisplayMetrics getScreenSize(Context context) {
            return context.getResources().getDisplayMetrics();
        }

        public static int getScreenHeight(Context context) {
            return getScreenSize(context).heightPixels;
        }

        public static int getScreenWidth(Context context) {
            return getScreenSize(context).widthPixels;
        }

        public static ConfirmDialog confirm(PhoneMp3Activity phoneMp3Activity, String msg, String sureTxt, DialogInterface.OnClickListener onClickListener) {
            ConfirmDialog dialog = new ConfirmDialog(phoneMp3Activity, msg, sureTxt);
            dialog.setOnClickListener(onClickListener);
            dialog.show();
            return dialog;
        }
    }

    public static class APP {
        /**
         * 获取应用版本号
         *
         * @param context
         * @return 版本号
         */
        public static String getVersion(Context context) {
            return ApplicationUtil.getVersion(context);
        }

        /**
         * 发送一条广播
         *
         * @param context
         * @param action
         */
        public static void sendBroadcast(Context context, String action, String data) {
            Intent intent = new Intent(action);
            if (!COMMON.isNull(data)) {
                intent.putExtra("data", data);
            }
            context.sendBroadcast(intent);
        }

        public static void sendBroadcast(Context context, String action) {
            sendBroadcast(context, action, null);
        }

        private static MediaMetadataCompat.Builder __mediametadataCompatBuilder;

        public static MediaMetadataCompat music2MetaData(Music music) {
            if (music == null) {
                return null;
            }
            if (__mediametadataCompatBuilder == null) {
                __mediametadataCompatBuilder = new MediaMetadataCompat.Builder();
            }
            __mediametadataCompatBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, JSON.toJSONString(music));
            return __mediametadataCompatBuilder.build();
        }

        public static Music metaData2Music(MediaMetadataCompat mediaMetadataCompat) {
            String music_ = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            return JSON.parseObject(music_, Music.class);
        }

        public static Bundle music2Bundle(Music music) {
            if (music == null) {
                return null;
            }
            Bundle b = new Bundle();
            b.putString("music", JSON.toJSONString(music));
            return b;
        }

        public static Music bundle2Music(Bundle bundle) {
            if (null == bundle) {
                return null;
            }
            return JSON.parseObject(bundle.getString("music"), Music.class);
        }

        /**
         * 执行手机中所有歌曲的扫描。
         */
        public static void scanAllMusic(Context param, final OnScannListener onScannListener) {

            onScannListener.onStart();

            boolean checkNew = false; // 标记是否需要寻找新加入的歌曲。

            // 先检查数据库中是否有所有歌曲列表， 没有则先创建一个
            PlayList playList = new PlayList();
            playList.setId(Database.PLAYLIST_ID_ALL);
            playList.setName("所有歌曲");
            playList.setRamark("该列表保存了手机中所有的歌曲");

            if (playList.exists()) {
                // 播放列表存在，则要检查哪些是新歌
                checkNew = true;
            } else {
                // 播放列表不存在，不检查哪些是新歌
                playList.insert();
                checkNew = false;
            }

            // 先查询内部存储空间
            __scannAllMusic(checkNew, playList, param,
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, onScannListener);


            // 再查询外部空间
            __scannAllMusic(checkNew, playList, param,
                    MediaStore.Audio.Media.INTERNAL_CONTENT_URI, onScannListener);


            onScannListener.onEnd();

        }

        private static void __scannAllMusic(boolean checkNew, PlayList playList,
                                            Context param, Uri uri, OnScannListener onScannListener) {
            Cursor c = null;
            ContentResolver mContentResolver = param.getContentResolver();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.CHINA
            );
            try {
                c = mContentResolver.query(uri, null, null,
                        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                if (c == null) {
                    return;
                }

                while (c.moveToNext()) {
                    String path = c.getString(
                            c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    );// 路径

                    File musicFile = new File(path);

                    int isMusic = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
                    int isRingtone = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_RINGTONE));
                    int isNotification = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_NOTIFICATION));
                    int isAlarm = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_ALARM));
                    int isPodcast = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_PODCAST));

                    if (isMusic == 1 &&
                            isRingtone != 1 &&
                            isNotification != 1 &&
                            isAlarm != 1 &&
                            isPodcast != 1 &&
                            path.toLowerCase().endsWith("mp3")) { // 是音乐

                        String title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                        String titleKey = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE_KEY));
                        String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                        String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                        long albumId = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)); // 专辑 ID
                        String albumKey = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_KEY));
                        String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                        long artistId = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)); // 艺术家ID
                        String artistKey = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_KEY));
                        long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                        int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                        // int id        = c.getInt(   c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID         ));// 歌曲的id

                        Music music = new Music();

                        music.set_data(path);
                        music.set_display_name(name);
                        music.setTitle(title);
                        music.setTitle_key(titleKey);
                        music.setAlbum(album);
                        music.setAlbum_id(String.valueOf(albumId));
                        music.setAlbum_key(albumKey);
                        music.setArtist(artist);
                        music.setArtist_id(String.valueOf(artistId));
                        music.setArtist_key(String.valueOf(artistKey));
                        music.set_size(String.valueOf(size));
                        music.setDuration(String.valueOf(duration));
                        music.setListId(playList.getId());

                        onScannListener.onMusic(music);

                        if (!musicFile.exists()) { // 音乐文件不存在。将数据从数据库移除
                            music.delete();
                            continue;
                        }

                        // 判断数据库中所有歌曲列表是否存在这首歌
                        if (SQLite.select().from(Music.class)
                                .where(Music_Table._data.eq(path))
                                .and(Music_Table.list_id.eq(playList.getId())).querySingle() != null) {
                            // 数据库中已存在。
                            continue;
                        } else {
                            music.setUpdateAt(simpleDateFormat.format(new Date()));

                            // 加载歌曲封面
                            String mUriAlbums = "content://media/external/audio/albums";
                            String[] projection = new String[]{"album_art"};
                            Cursor cur = param.getContentResolver().query(
                                    Uri.parse(mUriAlbums + "/" + music.getAlbum_id()),
                                    projection, null, null, null);
                            if (cur != null) {
                                String album_art = null;
                                if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                                    cur.moveToNext();
                                    album_art = cur.getString(0);
                                }
                                cur.close();
                                cur = null;
                                music.setCoverPath(album_art);
                            }


                            // 数据库中不存在
                            if (checkNew) {
                                music.setIsNew("yes");
                            }

                            long insert = music.insert();
                            if (1 <= insert) {
                                // 保存成功
                            } else {
                                // 保存失败
                                // logger.e(insert + ", 保存歌曲数据失败：" + music.toString());
                            }
                            music.setListId(null);
                        }

                        // 加入文件夹统计
                        PlayList dirPlayList = new PlayList();
                        String canonicalPath = musicFile.getParentFile().getCanonicalPath();
                        dirPlayList.setId(String.format("dir-%s", Utils.COMMON.MD5(canonicalPath)));
                        dirPlayList.setName("文件夹");
                        dirPlayList.setRamark(canonicalPath);
                        if (!dirPlayList.exists()) {
                            dirPlayList.insert();
                        }
                        music.setListId(dirPlayList.getId());
                        music.setId(0L);
                        if (SQLite.select().from(Music.class)
                                .where(Music_Table._data.eq(path))
                                .and(Music_Table.list_id.eq(dirPlayList.getId()))
                                .querySingle() == null) {
                            music.insert();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }

        }


        public interface OnScannListener {
            void onStart();

            void onMusic(Music music);

            void onEnd();
        }
    }

    public static class COMMON {
        public static String MD5(String str) {
            return MD5(str, "UTF-8");
        }

        public static String MD5(String str, String charset) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes(charset));
                byte[] result = md.digest();
                StringBuilder sb = new StringBuilder(32);
                for (byte aResult : result) {
                    int val = aResult & 0xff;
                    if (val <= 0xf) {
                        sb.append("0");
                    }
                    sb.append(Integer.toHexString(val));
                }
                return sb.toString().toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static boolean isNull(Object any) {
            if (any == null) {
                return true;
            }
            if (any instanceof String) {
                return TextUtils.isEmpty((String) any);
            } else if (any instanceof Collection) {
                return ((Collection) any).isEmpty();
            } else if (any instanceof Map) {
                return ((Map) any).size() <= 0;
            } else {
                return false;
            }
        }

        public static void nullThrow(Object value, String msg) throws Exception {
            if (isNull(value)) {
                throw new Exception(msg);
            }
        }

        public static void trueThrow(boolean value, String msg) throws Exception {
            if (value) {
                throw new Exception(msg);
            }
        }

        public static void falseThrow(boolean value, String msg) throws Exception {
            trueThrow(!value, msg);
        }

        /**
         * 按格式获取当前时间
         *
         * @param s
         * @return
         */
        public static String getDateStr(String s) {
            return new SimpleDateFormat(s, Locale.CHINA).format(new Date());
        }
    }
}
