package cn.microanswer.phonemp3.util;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

/**
 * 一个异步任务执行类
 */
public class Task<P, R> {
    private ITask<P, R> iTask; // 要执行的任务
    private boolean isCancel; // 是否取消了任务
    private boolean isFinish; // 标记任务是否执行完成了
    private P param; // 任务执行的参数

    private Task(ITask<P, R> iTask) {
        this.iTask = iTask;
        this.param = this.iTask.getParam();
    }

    private void setId(int id) {
        this.iTask.setId(id);
    }

    public String getDesc() {
        return this.iTask.getDesc();
    }

    /**
     * 取消这次任务。
     * <p style="color:red">
     * 此操作本质上是阻止后台任务执行完成后调用 afterRun() 方法, 没有调用这个方法，也就相当于这次任务被取消了。
     * </p>
     */
    public void cancel() {
        if (isFinish) {
            Log.w("Task", "任务[" + iTask.getId() + "]已经运行完成了，cancel() 方法调用的意义不大。");
        }
        this.isCancel = true;
    }

    public int getId() {
        return iTask.getId();
    }

    private R rruunn(P param) throws Exception {
        return iTask.run(param);
    }

    private void onError(Exception e) {
        if (isCancel) {
            return;
        }
        iTask.onError(e);
        this.isFinish = true;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public boolean isFinish() {
        return isFinish;
    }

    private void rruunnEnd(R vaue) {
        if (isCancel) {
            return;
        }
        iTask.afterRun(vaue);
        this.isFinish = true;
    }

    /**
     * 异步任务构建使用工具
     */
    public static class TaskHelper {
        private static TaskHelper taskHelper;

        // 在一个Task还没有运行完成的时候，又有多个task需要运行的时候，会尝试建立新的线程去运行这些task
        // 该字段限定了最多创建的线程个数， 当所有线程都有task正在运行的时候，接下来需要运行的task将会被
        // 放入一个集合中，等待前面的task运行完成后，这些放入集合的task将会被运行。
        private int maxThreadCount;

        /**
         * 存放需要运行的task的集合
         */
        private Vector<Task> needRunTasks;

        private ArrayList<TaskThread> taskThreads;

        private TaskHelper(int maxThreadCount) {
            this.maxThreadCount = maxThreadCount;
            if (this.maxThreadCount < 1) {
                this.maxThreadCount = 1;
            }
            this.taskThreads = new ArrayList<>();
            this.needRunTasks = new Vector<>();
        }

        public static TaskHelper getInstance() {
            return getInstance(10);
        }

        public static TaskHelper getInstance(int maxThreadCount) {
            if (taskHelper == null) {
                taskHelper = new TaskHelper(maxThreadCount);
            }
            return taskHelper;
        }

        public static TaskHelper newInstance(int maxThreadCount) {
            return new TaskHelper(maxThreadCount);
        }

        // 检查并启动任务线程
        private void initThread() {

            // 还没有开启任何一个任务线程
            if (taskThreads.isEmpty()) {
                TaskThread<Object, Object> taskThread = new TaskThread<>(needRunTasks, this);
                taskThread.start();
                taskThreads.add(taskThread);
                // Log.w("Microanswer", "没有开任何线程，现在开启一个线程了。");
            } else { // 线程列队中已经开启了线程的

                // 检查当前任务数量是否大于当前已开启的线程数量，
                // 如果大于了，则再开启线程处理这些任务，当开启的线程
                // 数量已经达到最大线程数量， 则不再开启新线程

                if (needRunTasks.size() >= taskThreads.size()) {

                    // 开启新的线程处理这些较多的任务
                    if (taskThreads.size() < maxThreadCount) {
                        // Log.w("Microanswer", "全部线程：" + taskThreads.size() + ", 全部任务： " + needRunTasks.size() + ", 任务较多，新开线程了。 ");
                        TaskThread<Object, Object> taskThread = new TaskThread<>(needRunTasks, this);
                        taskThread.start();
                        taskThreads.add(taskThread);
                    } else {
                        // 不再开启线程了。
                        // Log.w("Microanswer", "任务较多，线程已全部开启。");

                    }

                } else {
                    // 任务较少，当前开启的线程足以处理这些任务
                    // Log.w("Microanswer", "任务较少， 使用现有线程运行，现有线程数：" + taskThreads.size());
                }

            }
        }

        /**
         * 新建一个任务，但是并不会执行。<p>
         * 通过 run()方法可以执行
         * </p>
         *
         * @param iTask
         * @param id
         * @param <P>
         * @param <R>
         * @return
         */
        public <P, R> Task<P, R> newTask(ITask<P, R> iTask, int id) {
            Task<P, R> task = new Task<>(iTask);
            task.setId(id);
            return task;
        }

        /**
         * 新建一个任务，但是并不会执行。<p>
         * 通过 run()方法可以执行
         * </p>
         *
         * @param iTask
         * @param <P>
         * @param <R>
         * @return
         */
        public <P, R> Task<P, R> newTask(ITask<P, R> iTask) {
            return newTask(iTask, needRunTasks.size() + 1);
        }

        /**
         * 执行一个异步任务
         *
         * @param task 要执行的任务
         * @param <P>  任务执行过程中参数的类型
         * @param <R>  任务执行过程中结果数据的类型
         */
        public <P, R> void run(Task<P, R> task) {
            needRunTasks.add(task);

            initThread();

            synchronized (this) {
                try {
                    this.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 执行一个异步任务
         *
         * @param iTask 要执行的任务
         * @param <P>   任务执行过程中参数的类型
         * @param <R>   任务执行过程中结果数据的类型
         * @return 返回任务对象
         */
        public <P, R> Task<P, R> run(ITask<P, R> iTask) {
            Task<P, R> task = newTask(iTask);
            run(task);
            return task;
        }

        public void runAfter(final Runnable runnable, final long timeMillis) {
            if (runnable != null) {

                run(new ITask<Void, Void>() {
                    @Override
                    public Void run(Void param) throws Exception {
                        SystemClock.sleep(timeMillis);
                        return null;
                    }

                    @Override
                    public void afterRun(Void value) {
                        runnable.run();
                    }
                });
            }
        }

        public void stopAfterLastTaskFlish() {
            if (taskThreads != null) {
                for (TaskThread t : taskThreads) {
                    t.stopAfterLastTaskFlish();
                }
                taskThreads.clear();
            }
        }
    }

    /**
     * 用于执行Task的线程。
     */
    private static class TaskThread<P, R> extends Thread {
        private final Object lock;

        // 标记这个线程是否正在工作中。
        private boolean isRunning = false;

        // 任务列队
        private Vector needRunTasks;

        private TaskThread(Vector needRunTasks, TaskHelper taskHelper) {
            this.needRunTasks = needRunTasks;
            this.lock = taskHelper;
        }

        private Handler handler;

        @Override
        public void run() {
            // Log.i("Mic", "线程开始：" + Thread.currentThread().getName());

            handler = new Handler(Looper.getMainLooper());

            while (isRunning) {
                try {
                    final Task<P, R> task;
                    synchronized (lock) {
                        // 在集合中没有需要运行的task的时候，线程进入wait状态，该线程将运行到这里暂停
                        while (needRunTasks.isEmpty()) {
                            // Log.i("Mic", "任务集合为空，暂停：" + Thread.currentThread().getName());
                            if (!isRunning) return;
                            lock.wait();
                        }
                        task = (Task<P, R>) needRunTasks.remove(0);
                        // Log.w("Microanswer", "线程：" + getName() + ", 取出任务，剩下：" + needRunTasks.size() + "个任务");
                    }

                    // 在新的task加入的时候，会通知该线程醒过来，继续执行任务。


                    // 执行task的内容。
                    final Object[] returnData = new Object[2];
                    returnData[0] = null; // 用于保存结果
                    returnData[1] = null; // 用于保存异常
                    try {
                        returnData[0] = task.rruunn(task.param);
                    } catch (Exception e) {
                        returnData[1] = e;
                        // 错误信息抛出到主线程
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                task.onError((Exception) returnData[1]);
                            }
                        });
                    }

                    // 没有抛错，继续执行主线程通知。
                    if (returnData[1] == null) {
                        // 执行完成，通知主线程
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                task.rruunnEnd((R) returnData[0]);
                            }
                        });
                    }

                    // Log.i("Mic", "执行完成:" + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handler = null;
            // Log.i("Mic", "线程结束:" + Thread.currentThread().getName());
        }

        /**
         * 开启线程执行
         */
        @Override
        public synchronized void start() {
            isRunning = true;
            super.start();
        }

        /**
         * 调用该方法后，此线程将在运行完成当前的任务后终止。
         */
        private void stopAfterLastTaskFlish() {
            isRunning = false;
            try {
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static abstract class ITask<P, R> {

        /**
         * 任务描述
         */
        private String desc;
        /**
         * 任务唯一标识
         */
        private int id;

        public ITask() {
        }

        public ITask(int id) {
            this.setId(id);
        }

        /**
         * 重写该方法返回参数，该方法运行在主线
         *
         * @return 返回参数
         */
        public P getParam() {
            return null;
        }

        /**
         * 子线程
         *
         * @param param 参数
         * @return 返回运行结果
         */
        abstract public R run(P param) throws Exception;

        /**
         * 主线程
         *
         * @param value 结果
         */
        public void afterRun(R value) {
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void onError(Exception e) {
            e.printStackTrace();
        }
    }
}
