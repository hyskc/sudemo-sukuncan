package demo01_thread_create;

/**
 * Runnable 接口示例
 * 展示通过实现 Runnable 接口来创建线程的方式
 * 重点展示：Runnable 便于资源共享的特点
 */
public class RunnableDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== Runnable 接口示例 ==========\n");

        // 示例1：一个 Runnable 任务被多个线程共享执行
        demoSharedTask();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：多个任务分别创建线程
        demoSeparateTask();
    }

    /**
     * 演示 Runnable 的核心优势：资源共享
     * 多个线程共享同一个 Runnable 对象，共享同一个资源
     */
    static void demoSharedTask() throws InterruptedException {
        System.out.println("【示例1】资源共享 - 多个线程共享同一个任务");
        System.out.println("------------------------------------------------");

        // 创建一个共享的资源（计数器）
        SharedCounter counter = new SharedCounter();

        // 创建5个线程，它们共享同一个 Runnable 和同一个计数器
        Runnable sharedTask = new CountTask(counter);

        Thread thread1 = new Thread(sharedTask, "线程-1");
        Thread thread2 = new Thread(sharedTask, "线程-2");
        Thread thread3 = new Thread(sharedTask, "线程-3");
        Thread thread4 = new Thread(sharedTask, "线程-4");
        Thread thread5 = new Thread(sharedTask, "线程-5");

        // 每个线程累加100次，5个线程总共应该累加500次
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();

        System.out.println("预期结果: 500");
        System.out.println("实际结果: " + counter.getCount());
    }

    /**
     * 演示每个任务独立创建线程
     */
    static void demoSeparateTask() throws InterruptedException {
        System.out.println("【示例2】任务分离 - 每个任务独立执行");
        System.out.println("------------------------------------------------");

        // 每个任务处理不同的数据
        Runnable downloadTask = new DownloadTask("文件A");
        Runnable processTask = new ProcessTask("数据B");
        Runnable saveTask = new SaveTask("结果C");

        // 创建线程并将任务绑定到线程
        Thread threadA = new Thread(downloadTask, "下载线程");
        Thread threadB = new Thread(processTask, "处理线程");
        Thread threadC = new Thread(saveTask, "保存线程");

        // 启动线程
        System.out.println("启动三个线程...");
        long startTime = System.currentTimeMillis();

        threadA.start();
        threadB.start();
        threadC.start();

        // 等待所有线程执行完成
        threadA.join();
        threadB.join();
        threadC.join();

        long endTime = System.currentTimeMillis();
        System.out.println("\n总耗时: " + (endTime - startTime) + "ms");
    }

    /**
     * 共享计数器类
     * 模拟多个线程共享的资源
     */
    static class SharedCounter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * 计数任务 - 实现 Runnable 接口
     * 这个任务会被多个线程共享执行，每个线程都会累加计数器
     */
    static class CountTask implements Runnable {
        private final SharedCounter counter;

        public CountTask(SharedCounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                counter.increment();
            }
            System.out.println("[" + Thread.currentThread().getName() + "] 累加完成");
        }
    }

    /**
     * 下载任务 - 实现 Runnable 接口
     */
    static class DownloadTask implements Runnable {
        private final String fileName;

        public DownloadTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] 开始下载: " + fileName);
            try {
                Thread.sleep(500);  // 模拟下载耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + threadName + "] 下载完成: " + fileName);
        }
    }

    /**
     * 处理任务 - 实现 Runnable 接口
     */
    static class ProcessTask implements Runnable {
        private final String dataName;

        public ProcessTask(String dataName) {
            this.dataName = dataName;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] 开始处理: " + dataName);
            try {
                Thread.sleep(500);  // 模拟处理耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + threadName + "] 处理完成: " + dataName);
        }
    }

    /**
     * 保存任务 - 实现 Runnable 接口
     */
    static class SaveTask implements Runnable {
        private final String resultName;

        public SaveTask(String resultName) {
            this.resultName = resultName;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] 开始保存: " + resultName);
            try {
                Thread.sleep(500);  // 模拟保存耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + threadName + "] 保存完成: " + resultName);
        }
    }
}