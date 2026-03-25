package demo03_JUC;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * JUC 原子类示例
 * 展示：AtomicInteger（CAS 无锁）、ABA 问题及解决方案（AtomicStampedReference）
 */
public class AtomicDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== JUC 原子类示例 ==========\n");

        // 示例1：AtomicInteger 基础使用（CAS 无锁）
        demoAtomicInteger();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：ABA 问题演示
        demoABAProblem();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例3：AtomicStampedReference 解决 ABA 问题
        demoABASolution();
    }

    /**
     * 示例1：AtomicInteger 基础使用（CAS 无锁）
     * 原理：Compare And Swap（比较并交换），乐观锁机制
     */
    static void demoAtomicInteger() throws InterruptedException {
        System.out.println("【示例1】AtomicInteger - CAS 无锁机制");
        System.out.println("------------------------------------------------");
        System.out.println("说明：AtomicInteger 使用 CAS 算法实现原子操作，无需加锁");

        AtomicInteger atomicInt = new AtomicInteger(0);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                // 相当于 i++，但保证原子性
                atomicInt.incrementAndGet();
            }
            System.out.println("线程1 完成，当前值: " + atomicInt.get());
        }, "线程-A");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                atomicInt.incrementAndGet();
            }
            System.out.println("线程2 完成，当前值: " + atomicInt.get());
        }, "线程-B");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("最终计数: " + atomicInt.get() + " (预期: 2000)");
        System.out.println("\nCAS 原理：");
        System.out.println("  1. 读取当前值");
        System.out.println("  2. 计算新值");
        System.out.println("  3. 比较当前值是否未被修改");
        System.out.println("  4. 如果未修改，则更新为新值");
    }

    /**
     * 示例2：ABA 问题演示
     * 问题：线程1读取A，线程2将A改为B再改回A，线程1认为未被修改
     */
    static void demoABAProblem() throws InterruptedException {
        System.out.println("【示例2】ABA 问题演示");
        System.out.println("------------------------------------------------");
        System.out.println("说明：ABA 问题是指线程1读取到A，线程2将A改为B又改回A，线程1认为未被修改");

        AtomicInteger atomicInt = new AtomicInteger(100);

        Thread thread1 = new Thread(() -> {
            System.out.println("线程1 读取值: " + atomicInt.get());
            try {
                Thread.sleep(1000);  // 等待线程2执行
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 尝试将 100 改为 200
            boolean success = atomicInt.compareAndSet(100, 200);
            System.out.println("线程1 CAS 结果: " + success + ", 当前值: " + atomicInt.get());
        }, "线程1");

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(100);  // 确保线程1先读取
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 线程2 执行 ABA 操作
            atomicInt.compareAndSet(100, 150);  // A -> B
            System.out.println("线程2: 100 -> 150，当前值: " + atomicInt.get());

            atomicInt.compareAndSet(150, 100);  // B -> A
            System.out.println("线程2: 150 -> 100（变回A），当前值: " + atomicInt.get());
        }, "线程2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("\nABA 问题结果：线程1 成功修改了值，但实际中间发生过变化");
    }

    /**
     * 示例3：AtomicStampedReference 解决 ABA 问题
     * 原理：通过版本号stamp解决，每次修改版本号+1
     */
    static void demoABASolution() throws InterruptedException {
        System.out.println("【示例3】AtomicStampedReference 解决 ABA 问题");
        System.out.println("------------------------------------------------");
        System.out.println("说明：通过版本号（stamp）解决 ABA 问题");

        // 初始值 100，版本号 1
        AtomicStampedReference<Integer> stampedRef = new AtomicStampedReference<>(100, 1);

        Thread thread1 = new Thread(() -> {
            int[] stamp = new int[1];
            int value = stampedRef.get(stamp);
            System.out.println("线程1 读取值: " + value + ", 版本号: " + stamp[0]);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 尝试将 100 改为 200，需要版本号匹配
            boolean success = stampedRef.compareAndSet(100, 200, stamp[0], stamp[0] + 1);
            System.out.println("线程1 CAS 结果: " + success + ", 当前值: " + stampedRef.getReference());
        }, "线程1");

        Thread thread2 = new Thread(() -> {
            int[] stamp = new int[1];
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int[] currentStamp = new int[1];
            int currentValue = stampedRef.get(currentStamp);

            // 线程2 执行 ABA 操作，版本号会变化
            stampedRef.compareAndSet(currentValue, 150, currentStamp[0], currentStamp[0] + 1);
            System.out.println("线程2: " + currentValue + " -> 150，版本号: " + currentStamp[0] + " -> " + (currentStamp[0] + 1));

            currentValue = stampedRef.get(currentStamp);
            stampedRef.compareAndSet(currentValue, 100, currentStamp[0], currentStamp[0] + 1);
            System.out.println("线程2: " + currentValue + " -> 100，版本号: " + currentStamp[0] + " -> " + (currentStamp[0] + 1));
        }, "线程2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("\n最终值: " + stampedRef.getReference() + ", 最终版本号: " + stampedRef.getStamp());
        System.out.println("线程1 失败是因为版本号不匹配！");
    }
}