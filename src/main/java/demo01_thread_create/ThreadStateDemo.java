package demo01_thread_create;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程状态示例
 * 展示 Java 线程的六种状态：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED
 */
public class ThreadStateDemo {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== Java 线程六种状态示例 ==========\n");

        // 示例1：NEW 状态
        demoNewState();

        // 示例2：RUNNABLE 状态
        demoRunnableState();

        // 示例3：BLOCKED 状态
        demoBlockedState();

        // 示例4：WAITING 状态
        demoWaitingState();

        // 示例5：TIMED_WAITING 状态
        demoTimedWaitingState();

        // 示例6：TERMINATED 状态
        demoTerminatedState();
    }

    /**
     * NEW 状态：新建状态
     * 线程对象已创建，但尚未调用 start() 方法
     */
    static void demoNewState() throws InterruptedException {
        System.out.println("【示例1】NEW 状态 - 新建状态");
        System.out.println("------------------------------------------------");
        System.out.println("说明：线程对象已创建，但尚未调用 start() 方法");

        Thread thread = new Thread(() -> {
            System.out.println("线程执行中...");
        }, "新线程");

        // 获取线程状态（NEW）
        System.out.println("线程创建后, 调用 start() 前的状态: " + thread.getState());

        thread.start();
        thread.join();

        System.out.println("线程执行完毕后状态: " + thread.getState());
        System.out.println();
    }

    /**
     * RUNNABLE 状态：可运行状态
     * 线程已调用 start()，正在 JVM 中运行，或者等待 CPU 分配时间片
     */
    static void demoRunnableState() throws InterruptedException {
        System.out.println("【示例2】RUNNABLE 状态 - 可运行状态");
        System.out.println("------------------------------------------------");
        System.out.println("说明：线程正在 JVM 中运行，或者等待 CPU 调度（就绪）");

        Thread thread = new Thread(() -> {
            long sum = 0;
            for (int i = 0; i < 100000000; i++) {
                sum += i;
            }
            System.out.println("计算完成: " + sum);
        }, "计算线程");

        thread.start();

        // 短暂等待，让线程进入运行状态
        Thread.sleep(100);

        // 线程可能处于 RUNNABLE（运行中）或 RUNNABLE（就绪）
        System.out.println("线程运行中状态: " + thread.getState());

        thread.join();
        System.out.println();
    }

    /**
     * BLOCKED 状态：阻塞状态
     * 线程等待获取锁资源（如 synchronized 同步块）
     */
    static void demoBlockedState() throws InterruptedException {
        System.out.println("【示例3】BLOCKED 状态 - 阻塞状态");
        System.out.println("------------------------------------------------");
        System.out.println("说明：线程等待获取锁资源，进入 synchronized 同步块时被阻塞");

        Thread thread1 = new Thread(() -> {
            synchronized (ThreadStateDemo.class) {
                System.out.println("线程1 获取到锁，开始执行");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("线程1 释放锁");
            }
        }, "线程-A");

        Thread thread2 = new Thread(() -> {
            synchronized (ThreadStateDemo.class) {
                System.out.println("线程2 获取到锁");
            }
        }, "线程-B");

        thread1.start();
        Thread.sleep(100);  // 确保线程1先获取锁

        thread2.start();
        Thread.sleep(100);  // 线程2应该被阻塞

        System.out.println("线程2 被阻塞时的状态: " + thread2.getState());

        thread1.join();
        thread2.join();
        System.out.println();
    }

    /**
     * WAITING 状态：无限期等待状态
     * 线程调用 Object.wait()、Thread.join()、LockSupport.park() 等方法
     */
    static void demoWaitingState() throws InterruptedException {
        System.out.println("【示例4】WAITING 状态 - 无限期等待");
        System.out.println("------------------------------------------------");
        System.out.println("说明：线程调用 wait()、join()、park() 等方法，无限期等待");

        Thread thread1 = new Thread(() -> {
            System.out.println("线程1 等待中...");
            try {
                // 调用 join() 会无限期等待目标线程结束
                // 这里用 Object.wait() 演示
                synchronized (ThreadStateDemo.class) {
                    ThreadStateDemo.class.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "等待线程");

        thread1.start();
        Thread.sleep(100);

        System.out.println("线程1 等待中状态: " + thread1.getState());

        // 唤醒等待的线程
        synchronized (ThreadStateDemo.class) {
            ThreadStateDemo.class.notifyAll();
        }

        thread1.join();
        System.out.println();
    }

    /**
     * TIMED_WAITING 状态：计时等待状态
     * 线程调用 sleep()、wait(long timeout)、join(long timeout)、LockSupport.parkNanos() 等方法
     */
    static void demoTimedWaitingState() throws InterruptedException {
        System.out.println("【示例5】TIMED_WAITING 状态 - 计时等待");
        System.out.println("------------------------------------------------");
        System.out.println("说明：线程调用带超时时间的方法，如 sleep(1000)、wait(1000)、join(1000)");

        Thread thread = new Thread(() -> {
            System.out.println("线程睡眠 2 秒...");
            try {
                Thread.sleep(2000);  // sleep 进入 TIMED_WAITING
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("线程醒来");
        }, "睡眠线程");

        thread.start();
        Thread.sleep(500);  // 500ms 时线程还在睡眠

        System.out.println("线程睡眠中状态: " + thread.getState());

        thread.join();
        System.out.println();
    }

    /**
     * TERMINATED 状态：终止状态
     * 线程已执行完毕或异常终止
     */
    static void demoTerminatedState() throws InterruptedException {
        System.out.println("【示例6】TERMINATED 状态 - 终止状态");
        System.out.println("------------------------------------------------");
        System.out.println("说明：线程已执行完毕或异常终止");

        Thread thread = new Thread(() -> {
            System.out.println("线程执行任务");
        }, "完成线程");

        thread.start();
        thread.join();  // 等待线程执行完毕

        System.out.println("线程执行完毕后状态: " + thread.getState());
    }
}