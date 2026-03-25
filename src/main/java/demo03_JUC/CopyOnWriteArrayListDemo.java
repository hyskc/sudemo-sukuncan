package demo03_JUC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * CopyOnWriteArrayList 示例
 * 展示：读多写少场景下的线程安全List
 */
public class CopyOnWriteArrayListDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== CopyOnWriteArrayList 示例 ==========\n");

        // 示例1：读写性能对比
        demoReadWritePerformance();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：迭代器弱一致性
        demoIteratorConsistency();
    }

    /**
     * 示例1：读多写少场景性能对比
     * 读操作：100个线程，每个读1000次
     * 写操作：10个线程，每个写100次
     */
    static void demoReadWritePerformance() throws InterruptedException {
        System.out.println("【示例1】读多写少场景性能对比");
        System.out.println("------------------------------------------------");
        System.out.println("说明：读操作远多于写操作的场景");

        int readThreadCount = 100;
        int writeThreadCount = 10;
        int readTimes = 1000;
        int writeTimes = 100;

        // 1. ArrayList（非线程安全）
        System.out.println("\n--- ArrayList（非线程安全）---");
        testList(new ArrayList<>(), readThreadCount, writeThreadCount, readTimes, writeTimes);

        // 2. Collections.synchronizedList（synchronized 包装）
        System.out.println("\n--- Collections.synchronizedList ---");
        testList(Collections.synchronizedList(new ArrayList<>()), readThreadCount, writeThreadCount, readTimes, writeTimes);

        // 3. CopyOnWriteArrayList（Copy-On-Write）
        System.out.println("\n--- CopyOnWriteArrayList ---");
        testList(new CopyOnWriteArrayList<>(), readThreadCount, writeThreadCount, readTimes, writeTimes);
    }

    static void testList(List<Integer> list, int readThreadCount, int writeThreadCount, int readTimes, int writeTimes)
            throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(readThreadCount + writeThreadCount);

        // 写线程
        for (int i = 0; i < writeThreadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < writeTimes; j++) {
                        list.add(threadId * 1000 + j);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        // 读线程
        for (int i = 0; i < readThreadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < readTimes; j++) {
                        int size = list.size();
                        if (size > 0) {
                            list.get(0);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        long startTime = System.nanoTime();
        startLatch.countDown();
        endLatch.await();
        long endTime = System.nanoTime();

        System.out.println("读线程: " + readThreadCount + " × " + readTimes + " = " + (readThreadCount * readTimes) + " 次");
        System.out.println("写线程: " + writeThreadCount + " × " + writeTimes + " = " + (writeThreadCount * writeTimes) + " 次");
        System.out.println("耗时: " + (endTime - startTime) / 1_000_000 + "ms");
    }

    /**
     * 示例2：迭代器弱一致性
     * 迭代器反映的是创建时的快照，不会抛出 ConcurrentModificationException
     */
    static void demoIteratorConsistency() throws InterruptedException {
        System.out.println("【示例2】迭代器弱一致性");
        System.out.println("------------------------------------------------");
        System.out.println("说明：迭代器反映的是创建时的快照，不会抛出 ConcurrentModificationException");

        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        System.out.println("原始列表: " + list);

        // 启动迭代线程
        Thread iteratorThread = new Thread(() -> {
            for (Integer num : list) {
                System.out.println("迭代器读取: " + num);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "迭代线程");

        // 启动修改线程
        Thread modifyThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            list.add(100);
            list.remove(0);
            System.out.println("修改后列表: " + list);
        }, "修改线程");

        iteratorThread.start();
        modifyThread.start();

        iteratorThread.join();
        modifyThread.join();

        System.out.println("\n特点：迭代器不会抛出 ConcurrentModificationException");
        System.out.println("迭代器遍历的是创建时的快照，修改不影响迭代");
    }
}