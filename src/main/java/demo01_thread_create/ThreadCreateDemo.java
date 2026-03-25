package demo01_thread_create;

import java.util.concurrent.TimeUnit;

public class ThreadCreateDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== 多线程学习示例 ==========\n");

        // 示例1：单线程顺序执行
        demoSingleThread();

        // 示例2：多线程并行执行
        demoMultiThread();

        // 示例3：两种创建线程的方式
        demoTwoWaysCreateThread();
    }

    /**
     * 单线程执行示例
     * 三个任务串行执行，总耗时 = 各任务耗时之和
     */
    static void demoSingleThread() throws InterruptedException {
        System.out.println("【示例1】单线程执行 - 串行任务");
        System.out.println("-----------------------------------");

        long startTime = System.currentTimeMillis();

        // 任务按顺序一个接一个执行
        task("任务A - 下载文件", 500);  // 等待500ms
        task("任务B - 处理数据", 500);  // 再等待500ms
        task("任务C - 保存结果", 500);  // 再等待500ms
        // 总耗时: 1500ms (串行)

        long endTime = System.currentTimeMillis();
        System.out.println("单线程总耗时: " + (endTime - startTime) + "ms\n");
    }

    /**
     * 多线程执行示例
     * 三个任务同时执行，总耗时 ≈ 最长任务的耗时
     */
    static void demoMultiThread() throws InterruptedException {
        System.out.println("【示例2】多线程执行 - 并行任务");
        System.out.println("-----------------------------------");

        long startTime = System.currentTimeMillis();

        // 创建三个线程，分别执行不同任务
        Thread threadA = new Thread(() -> task("任务A - 下载文件", 500));
        Thread threadB = new Thread(() -> task("任务B - 处理数据", 500));
        Thread threadC = new Thread(() -> task("任务C - 保存结果", 500));

        // 启动三个线程（它们会同时运行）
        threadA.start();
        threadB.start();
        threadC.start();

        // 等待所有线程执行完成
        // join()：阻塞当前线程，直到指定线程执行完毕
        threadA.join();
        threadB.join();
        threadC.join();

        long endTime = System.currentTimeMillis();
        // 总耗时: ≈500ms（三个任务并行执行）
        System.out.println("多线程总耗时: " + (endTime - startTime) + "ms\n");
    }

    /**
     * 展示创建线程的两种方式
     */
    static void demoTwoWaysCreateThread() throws InterruptedException {
        System.out.println("【示例3】创建线程的两种方式");
        System.out.println("-----------------------------------");

        System.out.println("\n方式一：继承 Thread 类");
        System.out.println("优点：直接调用 start()，代码简单");
        System.out.println("缺点：Java单继承，如果已继承其他类则无法使用");

        // 方式一：继承 Thread 类
        MyThread thread1 = new MyThread("线程-1");
        thread1.start();

        System.out.println("\n方式二：实现 Runnable 接口");
        System.out.println("优点：更灵活，可继承其他类，任务与线程分离");
        System.out.println("缺点：需要手动创建 Thread 对象");

        // 方式二：实现 Runnable 接口（推荐使用）
        Thread thread2 = new Thread(new MyRunnable(), "线程-2");
        thread2.start();

        // 等待两个线程执行完毕
        thread1.join();
        thread2.join();
        System.out.println("\n两个线程执行完毕！\n");
    }

    /**
     * 模拟一个耗时的任务
     * @param name     任务名称
     * @param duration 任务耗时（毫秒）
     */
    static void task(String name, int duration) {
        // Thread.currentThread().getName() 获取当前执行的线程名称
        System.out.println("[" + Thread.currentThread().getName() + "] 开始: " + name);
        try {
            // 模拟耗时操作（睡眠指定毫秒）
            TimeUnit.MILLISECONDS.sleep(duration);
        } catch (InterruptedException e) {
            // 当线程被中断时，恢复中断状态
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + Thread.currentThread().getName() + "] 完成: " + name);
    }

    /**
     * 方式一：继承 Thread 类
     * 通过继承 Thread 并重写 run() 方法来定义线程任务
     */
    static class MyThread extends Thread {
        MyThread(String name) {
            super(name);  // 设置线程名称
        }

        @Override
        public void run() {
            // 线程执行的具体任务写在 run() 方法中
            task("MyThread 执行任务", 300);
        }
    }

    /**
     * 方式二：实现 Runnable 接口
     * 通过实现 Runnable 接口并重写 run() 方法来定义任务
     * 然后将任务交给 Thread 对象执行
     */
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            // 任务逻辑写在 run() 方法中
            task("MyRunnable 执行任务", 300);
        }
    }
}
