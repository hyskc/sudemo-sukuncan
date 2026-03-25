package demo02_synchronized_and_Lock;

/**
 * synchronized 关键字示例
 * 展示：对象锁、类锁、可重入特性、monitor 机制
 */
public class SynchronizedDemo {

    // 共享资源
    private int count = 0;

    // 演示对象锁
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== synchronized 关键字示例 ==========\n");

        // 示例1：对象锁 - synchronized 方法
        demoObjectLock();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：类锁 - synchronized static 方法
        demoClassLock();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例3：synchronized 可重入特性
        demoReentrant();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例4：synchronized 代码块（对象锁）
        demoSyncBlock();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例5：synchronized 代码块（类锁）
        demoSyncClassBlock();
    }

    /**
     * 示例1：对象锁 - synchronized 实例方法
     * 锁的是当前对象（this）
     */
    static void demoObjectLock() throws InterruptedException {
        System.out.println("【示例1】对象锁 - synchronized 实例方法");
        System.out.println("------------------------------------------------");
        System.out.println("说明：synchronized 修饰实例方法，锁的是当前对象（this）");

        SynchronizedDemo demo = new SynchronizedDemo();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                demo.increment();
            }
            System.out.println("线程1 完成");
        }, "线程-A");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                demo.increment();
            }
            System.out.println("线程2 完成");
        }, "线程-B");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("最终计数: " + demo.getCount() + " (预期: 2000)");
    }

    /**
     * 示例2：类锁 - synchronized static 方法
     * 锁的是 Class 对象（类本身）
     */
    static void demoClassLock() throws InterruptedException {
        System.out.println("【示例2】类锁 - synchronized static 方法");
        System.out.println("------------------------------------------------");
        System.out.println("说明：synchronized 修饰静态方法，锁的是 Class 对象");

        Thread thread1 = new Thread(() -> {
            StaticDemo.method1();
        }, "静态线程-A");

        Thread thread2 = new Thread(() -> {
            StaticDemo.method2();
        }, "静态线程-B");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    /**
     * 示例3：synchronized 可重入特性
     * 同一个线程可以多次获取同一把锁
     */
    static void demoReentrant() throws InterruptedException {
        System.out.println("【示例3】synchronized 可重入特性");
        System.out.println("------------------------------------------------");
        System.out.println("说明：同一个线程可以多次获取同一把锁，不会死锁");

        ReentrantDemo demo = new ReentrantDemo();

        Thread thread = new Thread(() -> {
            demo.outer();
        }, "重入线程");

        thread.start();
        thread.join();
    }

    /**
     * 示例4：synchronized 代码块（对象锁）
     * 比同步方法更灵活，可以只锁定部分代码
     */
    static void demoSyncBlock() throws InterruptedException {
        System.out.println("【示例4】synchronized 代码块 - 对象锁");
        System.out.println("------------------------------------------------");
        System.out.println("说明：synchronized(this) 锁定当前对象");

        SyncBlockDemo demo = new SyncBlockDemo();

        Thread thread1 = new Thread(() -> {
            demo.methodA();
        }, "A线程");

        Thread thread2 = new Thread(() -> {
            demo.methodB();
        }, "B线程");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    /**
     * 示例5：synchronized 代码块（类锁）
     * synchronized(类名.class) 锁定整个类
     */
    static void demoSyncClassBlock() throws InterruptedException {
        System.out.println("【示例5】synchronized 代码块 - 类锁");
        System.out.println("------------------------------------------------");
        System.out.println("说明：synchronized(类名.class) 锁定整个类");

        Thread thread1 = new Thread(() -> {
            SyncClassDemo.method1();
        }, "类锁线程-A");

        Thread thread2 = new Thread(() -> {
            SyncClassDemo.method2();
        }, "类锁线程-B");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    /**
     * 静态方法同步类
     */
    static class StaticDemo {
        public static synchronized void method1() {
            System.out.println("[method1] 开始执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[method1] 执行完成");
        }

        public static synchronized void method2() {
            System.out.println("[method2] 开始执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[method2] 执行完成");
        }
    }

    /**
     * 可重入演示类
     */
    static class ReentrantDemo {
        public synchronized void outer() {
            System.out.println("outer 方法开始");
            inner();  // 调用 inner，会再次获取锁
            System.out.println("outer 方法结束");
        }

        public synchronized void inner() {
            System.out.println("  inner 方法开始");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("  inner 方法结束");
        }
    }

    /**
     * synchronized 代码块演示类（对象锁）
     */
    static class SyncBlockDemo {
        public void methodA() {
            synchronized (this) {
                System.out.println("[methodA] 开始，持有当前对象锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("[methodA] 结束，释放锁");
            }
        }

        public void methodB() {
            synchronized (this) {
                System.out.println("[methodB] 开始，持有当前对象锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("[methodB] 结束，释放锁");
            }
        }
    }

    /**
     * synchronized 代码块演示类（类锁）
     */
    static class SyncClassDemo {
        public static void method1() {
            synchronized (SyncClassDemo.class) {
                System.out.println("[method1] 开始，持有类锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("[method1] 结束，释放类锁");
            }
        }

        public static void method2() {
            synchronized (SyncClassDemo.class) {
                System.out.println("[method2] 开始，持有类锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("[method2] 结束，释放类锁");
            }
        }
    }
}
