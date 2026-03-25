package demo03_JUC;

import java.util.concurrent.CountDownLatch;

/**
 * ThreadLocal 示例
 * 展示：每个线程独立副本、内存泄漏问题
 */
public class ThreadLocalDemo {

    // ThreadLocal：每个线程独立的数据副本
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    private static ThreadLocal<Integer> threadLocalInt = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== ThreadLocal 示例 ==========\n");

        // 示例1：每个线程独立副本
        demoIndependentCopy();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：ThreadLocal 内存泄漏演示
        demoMemoryLeak();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例3：正确的清理方式
        demoProperCleanup();
    }

    /**
     * 示例1：每个线程独立副本
     * 特点：不同线程访问 ThreadLocal，得到的是各自独立的值
     */
    static void demoIndependentCopy() throws InterruptedException {
        System.out.println("【示例1】ThreadLocal - 每个线程独立副本");
        System.out.println("------------------------------------------------");
        System.out.println("说明：不同线程访问 ThreadLocal，得到各自独立的值");

        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                // 每个线程设置自己的值
                threadLocal.set("线程-" + threadId + " 的值");
                threadLocalInt.set(threadId * 100);

                // 读取自己的值
                System.out.println("[" + Thread.currentThread().getName() + "] threadLocal = " + threadLocal.get());
                System.out.println("[" + Thread.currentThread().getName() + "] threadLocalInt = " + threadLocalInt.get());

                // 清理（避免内存泄漏）
                threadLocal.remove();
                threadLocalInt.remove();

                latch.countDown();
            }, "T-" + i).start();
        }

        latch.await();
        System.out.println("\n结论：ThreadLocal 为每个线程提供独立的变量副本");
    }

    /**
     * 示例2：ThreadLocal 内存泄漏演示
     * 原理：ThreadLocalMap 使用弱引用（Entry extends WeakReference<ThreadLocal<?>>）
     *      key（ThreadLocal）被回收后，value 可能泄漏
     */
    static void demoMemoryLeak() throws InterruptedException {
        System.out.println("【示例2】ThreadLocal 内存泄漏问题");
        System.out.println("------------------------------------------------");
        System.out.println("说明：ThreadLocalMap 的 Entry 使用弱引用，key 可能被回收导致 value 泄漏");

        System.out.println("\nThreadLocalMap 结构：");
        System.out.println("  Entry(ThreadLocal<?> k, Object v)");
        System.out.println("  k 是弱引用（WeakReference）");
        System.out.println("  v 是强引用");

        System.out.println("\n内存泄漏场景：");
        System.out.println("  1. ThreadLocal 被回收（key = null）");
        System.out.println("  2. Thread 仍在运行");
        System.out.println("  3. Entry 的 value 不会被回收（强引用）");

        System.out.println("\n解决方案：");
        System.out.println("  1. 手动调用 remove()");
        System.out.println("  2. 使用 try-finally 块");
        System.out.println("  3. 在finally中清理");

        // 演示正确清理
        Thread thread = new Thread(() -> {
            threadLocal.set("重要数据");

            try {
                System.out.println("[" + Thread.currentThread().getName() + "] 获取值: " + threadLocal.get());
            } finally {
                // 务必清理！
                threadLocal.remove();
                System.out.println("[" + Thread.currentThread().getName() + "] 已清理 ThreadLocal");
            }
        }, "演示线程");

        thread.start();
        thread.join();
    }

    /**
     * 示例3：正确的清理方式
     */
    static void demoProperCleanup() {
        System.out.println("【示例3】ThreadLocal 正确清理方式");
        System.out.println("------------------------------------------------");
        System.out.println("说明：展示如何正确清理 ThreadLocal，避免内存泄漏");

        // 方式1：try-finally 块（推荐）
        System.out.println("\n方式1：try-finally 块（推荐）");
        threadLocal.set("数据A");
        try {
            System.out.println("获取值: " + threadLocal.get());
        } finally {
            threadLocal.remove();
            System.out.println("已清理");
        }

        // 方式2：使用 ThreadLocal.withInitial()
        System.out.println("\n方式2：使用 withInitial() 初始化");
        ThreadLocal<Integer> safeCounter = ThreadLocal.withInitial(() -> 0);
        System.out.println("初始值: " + safeCounter.get());
        safeCounter.set(100);
        System.out.println("设置后: " + safeCounter.get());
        safeCounter.remove();
        System.out.println("清理后: " + safeCounter.get());

        System.out.println("\n最佳实践：");
        System.out.println("  1. 在 finally 块中调用 remove()");
        System.out.println("  2. 使用 try-with-resources 模式");
        System.out.println("  3. 在线程池中使用 ThreadLocal 尤其要注意清理");
    }
}