package demo01_thread_create;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Callable + FutureTask 示例
 * 展示通过实现 Callable 接口来创建线程的方式
 * 特点：可以返回执行结果、可以抛出异常
 */
public class CallableDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("========== Callable + FutureTask 示例 ==========\n");

        // 示例1：Callable + FutureTask 获取返回结果
        demoWithResult();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：模拟实际场景 - 并行计算
        demoRealScenario();
    }

    /**
     * 示例1：Callable + FutureTask 获取返回结果
     */
    static void demoWithResult() throws InterruptedException, ExecutionException {
        System.out.println("【示例1】Callable + FutureTask 获取返回结果");
        System.out.println("------------------------------------------------");

        // 创建 Callable 任务（计算 1+2+...+100）
        Callable<Integer> task = new SumCallable(100);

        // 将 Callable 包装成 FutureTask
        // FutureTask 既充当 Runnable，又可以获取 Callable 的返回结果
        FutureTask<Integer> futureTask = new FutureTask<>(task);

        // 创建线程并启动
        Thread thread = new Thread(futureTask, "计算线程");
        thread.start();

        // 在主线程中等待计算结果
        // get() 会阻塞当前线程，直到 Future 任务完成并返回结果
        Integer result = futureTask.get();

        System.out.println("线程名称: " + thread.getName());
        System.out.println("计算结果: 1+2+...+100 = " + result);
        System.out.println("任务完成: " + futureTask.isDone());
    }

    /**
     * 示例2：模拟实际场景 - 并行计算后汇总结果
     * 比如：计算网站日活（不同地区的用户分别计算，最后汇总）
     */
    static void demoRealScenario() throws InterruptedException, ExecutionException {
        System.out.println("【示例2】实际场景 - 并行计算后汇总结果");
        System.out.println("------------------------------------------------");

        // 模拟四个地区的数据，分别由四个线程计算
        FutureTask<Long> futureNorth = new FutureTask<>(new UserCountCallable("北方区", 10000));
        FutureTask<Long> futureSouth = new FutureTask<>(new UserCountCallable("南方区", 15000));
        FutureTask<Long> futureEast = new FutureTask<>(new UserCountCallable("东方区", 12000));
        FutureTask<Long> futureWest = new FutureTask<>(new UserCountCallable("西方区", 8000));

        // 创建并启动四个线程
        Thread thread1 = new Thread(futureNorth, "北方线程");
        Thread thread2 = new Thread(futureSouth, "南方线程");
        Thread thread3 = new Thread(futureEast, "东方线程");
        Thread thread4 = new Thread(futureWest, "西方线程");

        long startTime = System.currentTimeMillis();

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        // 等待所有计算完成并获取结果
        Long northCount = futureNorth.get();
        Long southCount = futureSouth.get();
        Long eastCount = futureEast.get();
        Long westCount = futureWest.get();

        long endTime = System.currentTimeMillis();

        // 汇总结果
        long totalCount = northCount + southCount + eastCount + westCount;

        System.out.println("北方区用户数: " + northCount);
        System.out.println("南方区用户数: " + southCount);
        System.out.println("东方区用户数: " + eastCount);
        System.out.println("西方区用户数: " + westCount);
        System.out.println("总用户数: " + totalCount);
        System.out.println("计算耗时: " + (endTime - startTime) + "ms");
    }

    /**
     * Callable 实现类：计算 1+2+...+n 的和
     */
    static class SumCallable implements Callable<Integer> {
        private final int n;

        public SumCallable(int n) {
            this.n = n;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println("[" + Thread.currentThread().getName() + "] 开始计算 1+2+...+" + n);
            Thread.sleep(500);  // 模拟计算耗时

            int sum = 0;
            for (int i = 1; i <= n; i++) {
                sum += i;
            }

            System.out.println("[" + Thread.currentThread().getName() + "] 计算完成");
            return sum;
        }
    }

    /**
     * Callable 实现类：模拟统计用户数量
     */
    static class UserCountCallable implements Callable<Long> {
        private final String region;
        private final int baseCount;

        public UserCountCallable(String region, int baseCount) {
            this.region = region;
            this.baseCount = baseCount;
        }

        @Override
        public Long call() throws Exception {
            System.out.println("[" + Thread.currentThread().getName() + "] 开始统计 " + region);
            Thread.sleep(500);  // 模拟数据库查询耗时

            // 模拟返回用户数量（添加一些随机性）
            long count = baseCount + (long) (Math.random() * 1000);
            System.out.println("[" + Thread.currentThread().getName() + "] " + region + " 统计完成: " + count);
            return count;
        }
    }
}