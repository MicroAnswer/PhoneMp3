package cn.microanswer.phonemp3.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 应用崩溃处理类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static Logger logger = Logger.getLogger(CrashHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        String s = stringWriter.toString();

        logger.e("[" + t.getName() + "线程] " + s);


        try {
            Thread.sleep(2000);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
