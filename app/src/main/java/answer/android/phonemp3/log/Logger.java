package answer.android.phonemp3.log;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import answer.android.phonemp3.R;
import answer.android.phonemp3.tool.Tool;

/**
 * 日志打印器.
 * 日志记录规则:
 * 1. 每天产生一个日志文件
 * 2. 只记录7天的日志
 * 3. 如果某一天程序没有任何日志输出,不产生对应的日志文件
 * Created by Micro on 2017/6/12.
 */

public class Logger {
  // 日志标签
  private String i, w, e;

  // 所在类名
  private String className;

  // 日志输出文件对象
  private File logFile;

  // 日志输出打印器
  private RandomAccessFile p;

  public void info(Throwable t) {
    info(Tool.Exception2String(t));
  }

  public void warn(Throwable t) {
    warn(Tool.Exception2String(t));
  }

  public void err(Throwable t) {
    err(Tool.Exception2String(t));
  }


  // 记录信息
  public void info(String msg) {
    try {
      p.seek(p.length());
      p.writeChar('\r');
      p.writeChar('\n');
      String v = _now() + i + className + msg;
      p.writeUTF(v);
      Log.i("Logger", v);
    } catch (Exception e) {
      Log.e("Logger", "写入日志info信息出错");
      e.printStackTrace();
    }
  }

  // 记录错误
  public void err(String err) {
    try {
      p.seek(p.length());
      p.writeChar('\n');
      String a = _now() + e + className + err;
      p.writeUTF(a);
      Log.e("Logger", a);
    } catch (Exception e) {
      Log.e("Logger", "写入日志err信息出错");
      e.printStackTrace();
    }
  }

  // 记录警告
  public void warn(String warn) {
    try {
      p.seek(p.length());
      p.writeChar('\r');
      p.writeChar('\n');
      String t = _now() + w + className + warn;
      p.writeUTF(t);
      Log.w("Logger", t);
    } catch (Exception e) {
      Log.e("Logger", "写入日志warn信息出错");
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      p.close();
    } catch (Exception e) {
      Log.e("Logger", "关闭日志文件出错");
      e.printStackTrace();
    }
  }

  public void setClassName(String className) {
    this.className = "[" + className + "]";
  }

  public File getLogFile() {
    return logFile;
  }

  // 时间格式化对象
  private SimpleDateFormat s;

  private String _now() {
    return s.format(new Date());
  }

  /**
   * 构造函数:
   * 实列化logFile对象.
   * 使用当前的日期进行实列化
   */
  private Logger(Context c) {
    logFile = i(c);
    s = new SimpleDateFormat(c.getResources().getString(R.string.logtimeformat), Locale.CHINESE);
    i = c.getResources().getString(R.string.loginfo);
    w = c.getResources().getString(R.string.logwarn);
    e = c.getResources().getString(R.string.logerror);
    className = "[" + c.getClass().getSimpleName() + "]";
    try {
      p = new RandomAccessFile(i(c), "rwd");
      p.seek(p.length());
    } catch (IOException e) {
      Log.e("Logger", "构建日志文件输出打印器错误.");
      e.printStackTrace();
    }
  }

  /**
   * 获取当天的日志打印器对象
   *
   * @return answer.android.phonemp3.log.Logger
   */
  public static Logger getLogger(Context c) {
    return new Logger(c);
  }

  // 获取当天时间对应的日志文件对象
  private File i(Context c) {
    // 获取当前时间
    Calendar d = Calendar.getInstance();

    // 进行格式化文件名
    Resources r = c.getResources();
    String n = new SimpleDateFormat(r.getString(R.string.logfilenameformat), Locale.CHINESE)
            .format(d.getTime()) + r.getString(R.string.logsuffix);
    File f_ = new File(c.getExternalCacheDir(), n);
    if (!f_.exists()) {
      try {
        if (f_.createNewFile()) {
          return f_;
        } else {
          Log.e("Logger", "创建日志文件失败.");
        }
      } catch (IOException e) {
        Log.e("Logger", "创建日志文件出错.");
        e.printStackTrace();
      }
    }
    return f_;
  }

}
