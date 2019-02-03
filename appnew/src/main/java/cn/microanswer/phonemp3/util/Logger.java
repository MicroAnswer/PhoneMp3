package cn.microanswer.phonemp3.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import cn.microanswer.phonemp3.PhoneMp3Application;

/**
 * 日志打印工具类
 */
public class Logger extends Handler {
    private static final Object lock = new Object();

    private BufferedWriter bufferedWriter;

    java.util.logging.Logger logger = null;

    public Logger(Class clas) {
        this.logger = java.util.logging.Logger.getLogger(clas.getSimpleName());
        this.logger.setLevel(Level.INFO);
        this.logger.addHandler(this);

        // 每天产生一个日志文件， 7前的日志文件删除。
        String logFileName = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(new Date()) + ".log";
        if (!PhoneMp3Application.DIR_LOG.exists()) {
            PhoneMp3Application.DIR_LOG.mkdirs();
        }
        File file = new File(PhoneMp3Application.DIR_LOG, logFileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file,true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger(Class clas) {
        return new Logger(clas);
    }

    public void i(String message) {
        logger.info(message);
    }
    public void f(String message){logger.fine(message);}
    public void w(String message){logger.warning(message);}
    public void e(String message) {logger.severe(message);}

    @Override
    public void publish(final LogRecord record) {
        final Level level = record.getLevel();
        if (level.intValue() >= logger.getLevel().intValue()) {
            Task.TaskHelper.getInstance().run(new Task.ITask() {
                @Override
                public Object run(Object param) throws Exception{
                    synchronized (lock) {
                        Date date = new Date();
                        String le;
                        if (level.intValue() == Level.SEVERE.intValue()) {
                            le = "ERROR";
                        } else {
                            le = level.getName();
                        }
                        String time = new SimpleDateFormat("HH:mm:ss", Locale.CHINESE).format(date);
                        String message = record.getMessage();
                        String loggerName = record.getLoggerName();
                        bufferedWriter.append(new StringBuilder().append("[").append(time).append("][")
                                .append(le).append("][").append(loggerName).append("] ").append(message).append("\n"));
                        bufferedWriter.flush();
                    }
                    return null;
                }

                @Override
                public void onError(Exception e) {
                    super.onError(e);
                }
            });
        }
    }

    @Override
    public void flush() {
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws SecurityException {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
