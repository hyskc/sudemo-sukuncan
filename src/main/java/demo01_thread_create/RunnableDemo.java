package demo01_thread_create;

/**
 * Runnable 接口示例
 * 展示通过实现 Runnable 接口来创建线程的方式
 */
public class RunnableDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== Runnable 接口示例 ==========\n");

        // 创建 Runnable 任务
        Runnable downloadTask = new DownloadTask();
        Runnable processTask = new ProcessTask();
        Runnable saveTask = new SaveTask();

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
     * 下载任务 - 实现 Runnable 接口
     */
    static class DownloadTask implements Runnable {
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] 开始下载文件...");
            try {
                Thread.sleep(500);  // 模拟下载耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + threadName + "] 下载完成！");
        }
    }

    /**
     * 处理任务 - 实现 Runnable 接口
     */
    static class ProcessTask implements Runnable {
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] 开始处理数据...");
            try {
                Thread.sleep(500);  // 模拟处理耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + threadName + "] 处理完成！");
        }
    }

    /**
     * 保存任务 - 实现 Runnable 接口
     */
    static class SaveTask implements Runnable {
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] 开始保存结果...");
            try {
                Thread.sleep(500);  // 模拟保存耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + threadName + "] 保存完成！");
        }
    }
}