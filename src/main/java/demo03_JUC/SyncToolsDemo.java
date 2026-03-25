package demo03_JUC;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * JUC 同步工具示例
 * 展示：CountDownLatch、CyclicBarrier、Semaphore、Exchanger
 */
public class SyncToolsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== JUC 同步工具示例 ==========\n");

        // 示例1：CountDownLatch（倒计时门闩）
        demoCountDownLatch();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：CyclicBarrier（循环栅栏）
        demoCyclicBarrier();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例3：Semaphore（信号量）
        demoSemaphore();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例4：Exchanger（线程间数据交换）
        demoExchanger();
    }

    /**
     * 示例1：CountDownLatch（倒计时门闩）
    倒数 * 特点：到 0 后打开，执行一次后不可复用
     */
    static void demoCountDownLatch() throws InterruptedException {
        System.out.println("【示例1】CountDownLatch - 倒计时门闩");
        System.out.println("------------------------------------------------");
        System.out.println("说明：等待 N 个线程完成后再执行，适合'主线程等待子线程'场景");

        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("主线程: 等待 " + threadCount + " 个子线程完成...");

        for (int i = 1; i <= threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                System.out.println("子线程-" + threadId + " 开始执行");
                try {
                    Thread.sleep((long) (Math.random() * 1000 + 500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("子线程-" + threadId + " 执行完成");
                latch.countDown();  // 计数 -1
            }, "子线程-" + i).start();
        }

        latch.await();  // 等待计数到 0
        System.out.println("主线程: 所有子线程已完成，继续执行");

        System.out.println("\n特点：不可复用，计数到 0 后不能重置");
    }

    /**
     * 示例2：CyclicBarrier（循环栅栏）
     * 特点：所有线程到达后一起放行，可循环使用
     */
    static void demoCyclicBarrier() throws InterruptedException {
        System.out.println("【示例2】CyclicBarrier - 循环栅栏");
        System.out.println("------------------------------------------------");
        System.out.println("说明：所有线程到达栅栏后一起执行，可循环使用，适合'线程汇总'场景");

        int threadCount = 3;
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            System.out.println(">>> 所有线程已到达，开始汇总结果 <<<");
        });

        System.out.println("模拟多人开会，必须等所有人都到齐才能开始\n");

        for (int i = 1; i <= threadCount; i++) {
            final int personId = i;
            new Thread(() -> {
                try {
                    System.out.println("人员-" + personId + " 出发去会议室");
                    Thread.sleep((long) (Math.random() * 1000 + 500));
                    System.out.println("人员-" + personId + " 到达，等待其他人...");

                    barrier.await();  // 等待所有人到达

                    System.out.println("人员-" + personId + " 开始开会");

                    barrier.await();  // 第二阶段

                    System.out.println("人员-" + personId + " 会议结束，离开");
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }, "人员-" + i).start();
        }

        Thread.sleep(4000);
        System.out.println("\n特点：可循环使用，适用于分阶段任务");
    }

    /**
     * 示例3：Semaphore（信号量）
     * 特点：控制同时访问资源的线程数量
     */
    static void demoSemaphore() throws InterruptedException {
        System.out.println("【示例3】Semaphore - 信号量");
        System.out.println("------------------------------------------------");
        System.out.println("说明：控制同时访问资源的线程数量，适合'限流'场景");

        int permitCount = 3;  // 同时允许 3 个线程
        Semaphore semaphore = new Semaphore(permitCount);

        System.out.println("停车场有 " + permitCount + " 个车位\n");

        for (int i = 1; i <= 5; i++) {
            final int carId = i;
            new Thread(() -> {
                try {
                    System.out.println("车辆-" + carId + " 来到停车场");
                    semaphore.acquire();  // 获取许可
                    System.out.println("车辆-" + carId + " 进入车位");

                    Thread.sleep((long) (Math.random() * 2000 + 1000));

                    System.out.println("车辆-" + carId + " 离开车位");
                    semaphore.release();  // 释放许可
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "车辆-" + i).start();
        }

        Thread.sleep(6000);
        System.out.println("\n特点：限流，可控制并发数量");
    }

    /**
     * 示例4：Exchanger（线程间数据交换）
     * 特点：两个线程交换数据
     */
    static void demoExchanger() throws InterruptedException {
        System.out.println("【示例4】Exchanger - 数据交换");
        System.out.println("------------------------------------------------");
        System.out.println("说明：两个线程交换数据，适合'线程间数据传递'场景");

        Exchanger<String> exchanger = new Exchanger<>();

        Thread threadA = new Thread(() -> {
            try {
                String dataA = "数据A（来自线程A）";
                System.out.println("线程A 持有: " + dataA);
                System.out.println("线程A 等待交换...");

                String received = exchanger.exchange(dataA);
                System.out.println("线程A 交换后得到: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "线程A");

        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(500);  // 确保线程A先执行
                String dataB = "数据B（来自线程B）";
                System.out.println("线程B 持有: " + dataB);
                System.out.println("线程B 等待交换...");

                String received = exchanger.exchange(dataB);
                System.out.println("线程B 交换后得到: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "线程B");

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();

        System.out.println("\n特点：两个线程交换数据，exchange() 相互等待");
    }
}