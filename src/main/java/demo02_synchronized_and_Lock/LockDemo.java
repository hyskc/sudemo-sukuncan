package demo02_synchronized_and_Lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Lock 接口示例
 * 展示：ReentrantLock（可重入、可中断、公平/非公平）、ReentrantReadWriteLock（读写锁）
 */
public class LockDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== Lock 接口示例 ==========\n");

        // 示例1：ReentrantLock 基础使用
        demoReentrantLock();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：ReentrantLock 可中断锁
        demoInterruptibleLock();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例3：公平锁 vs 非公平锁
        demoFairLock();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例4：ReentrantReadWriteLock 读写锁
        demoReadWriteLock();
    }

    /**
     * 示例1：ReentrantLock 基础使用
     * 特点：可重入、tryLock() 尝试获取锁
     */
    static void demoReentrantLock() throws InterruptedException {
        System.out.println("【示例1】ReentrantLock 基础使用");
        System.out.println("------------------------------------------------");
        System.out.println("说明：ReentrantLock 是可重入锁，与 synchronized 类似但更灵活");

        ReentrantLock lock = new ReentrantLock();
        Counter counter = new Counter(lock);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
            System.out.println("线程1 完成");
        }, "线程-A");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
            System.out.println("线程2 完成");
        }, "线程-B");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("最终计数: " + counter.getCount() + " (预期: 2000)");
    }

    /**
     * 示例2：ReentrantLock 可中断锁
     * 特点：lockInterruptibly() 可以响应中断
     */
    static void demoInterruptibleLock() throws InterruptedException {
        System.out.println("【示例2】ReentrantLock 可中断锁");
        System.out.println("------------------------------------------------");
        System.out.println("说明：lockInterruptibly() 可以响应线程中断");

        ReentrantLock lock = new ReentrantLock();

        Thread thread1 = new Thread(() -> {
            try {
                System.out.println("线程1 尝试获取锁...");
                lock.lockInterruptibly();
                System.out.println("线程1 获取到锁");
                Thread.sleep(3000);  // 持有锁3秒
            } catch (InterruptedException e) {
                System.out.println("线程1 被中断，放弃获取锁");
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }, "中断线程");

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(500);  // 等待线程1先获取锁
                System.out.println("线程2 尝试获取锁...");
                lock.lockInterruptibly();
                System.out.println("线程2 获取到锁");
            } catch (InterruptedException e) {
                System.out.println("线程2 被中断，放弃获取锁");
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }, "普通线程");

        thread1.start();
        thread2.start();

        Thread.sleep(1000);  // 等待线程2进入等待状态
        thread2.interrupt();  // 中断线程2

        thread1.join();
        thread2.join();
        System.out.println("示例2 结束");
    }

    /**
     * 示例3：公平锁 vs 非公平锁
     * 公平锁：按照等待顺序获取锁
     * 非公平锁：允许插队，可能导致线程饥饿
     */
    static void demoFairLock() throws InterruptedException {
        System.out.println("【示例3】公平锁 vs 非公平锁");
        System.out.println("------------------------------------------------");
        System.out.println("说明：公平锁按等待顺序，非公平锁允许插队");

        // 非公平锁（默认）
        System.out.println("\n--- 非公平锁 ---");
        demoFairness(false);

        // 公平锁
        System.out.println("\n--- 公平锁 ---");
        demoFairness(true);
    }

    static void demoFairness(boolean fair) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(fair);
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                lock.lock();
                try {
                    System.out.println("线程-" + index + " 获取锁");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }, "T-" + i);
        }

        // 所有线程同时启动
        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    /**
     * 示例4：ReentrantReadWriteLock 读写锁
     * 读操作可以并发，写操作独占
     */
    static void demoReadWriteLock() throws InterruptedException {
        System.out.println("【示例4】ReentrantReadWriteLock 读写锁");
        System.out.println("------------------------------------------------");
        System.out.println("说明：读操作并发执行，写操作独占，读写互斥");

        ReadWriteDemo demo = new ReadWriteDemo();

        // 多个读线程
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    demo.read("读线程-" + id);
                }
            }, "读-" + i).start();
        }

        // 多个写线程
        for (int i = 0; i < 2; i++) {
            final int id = i;
            new Thread(() -> {
                for (int j = 0; j < 2; j++) {
                    demo.write("写线程-" + id + "-数据-" + j);
                }
            }, "写-" + i).start();
        }

        // 等待所有线程完成
        Thread.sleep(3000);
    }

    /**
     * 计数器类（使用 ReentrantLock）
     */
    static class Counter {
        private final ReentrantLock lock;
        private int count = 0;

        public Counter(ReentrantLock lock) {
            this.lock = lock;
        }

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 读写锁演示类
     */
    static class ReadWriteDemo {
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private String data = "初始数据";

        public void read(String threadName) {
            rwLock.readLock().lock();
            try {
                System.out.println("[" + threadName + "] 读取数据: " + data);
                Thread.sleep(100);  // 模拟读取耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rwLock.readLock().unlock();
            }
        }

        public void write(String threadName) {
            rwLock.writeLock().lock();
            try {
                data = threadName;
                System.out.println("[" + threadName + "] 写入数据: " + data);
                Thread.sleep(200);  // 模拟写入耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }
}