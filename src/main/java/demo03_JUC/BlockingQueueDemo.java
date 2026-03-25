package demo03_JUC;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * BlockingQueue 示例
 * 展示：ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue
 */
public class BlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== BlockingQueue 示例 ==========\n");

        // 示例1：ArrayBlockingQueue（有界队列）
        demoArrayBlockingQueue();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：LinkedBlockingQueue（无界队列）
        demoLinkedBlockingQueue();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例3：SynchronousQueue（同步队列）
        demoSynchronousQueue();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例4：阻塞方法演示
        demoBlockingMethods();
    }

    /**
     * 示例1：ArrayBlockingQueue（有界队列）
     * 特点：基于数组实现，有容量限制
     */
    static void demoArrayBlockingQueue() throws InterruptedException {
        System.out.println("【示例1】ArrayBlockingQueue - 有界队列");
        System.out.println("------------------------------------------------");
        System.out.println("说明：基于数组实现，有容量限制， FIFO 顺序");

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        // 生产者线程
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    String msg = "消息-" + i;
                    queue.put(msg);  // 队列满时会阻塞
                    System.out.println("生产: " + msg + " (队列大小: " + queue.size() + ")");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "生产者");

        // 消费者线程
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(200);  // 消费慢一点
                    String msg = queue.take();  // 队列空时会阻塞
                    System.out.println("消费: " + msg + " (队列大小: " + queue.size() + ")");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "消费者");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }

    /**
     * 示例2：LinkedBlockingQueue（无界队列）
     * 特点：基于链表实现，默认无容量限制
     */
    static void demoLinkedBlockingQueue() throws InterruptedException {
        System.out.println("【示例2】LinkedBlockingQueue - 无界队列");
        System.out.println("------------------------------------------------");
        System.out.println("说明：基于链表实现，默认无容量限制（Integer.MAX_VALUE）");

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        // 放入元素
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        System.out.println("放入3个元素: " + queue);

        // 尝试超出容量（实际上不会满，因为无界）
        queue.offer(4);
        System.out.println("再放入1个: " + queue);

        // 取出元素
        System.out.println("poll(): " + queue.poll());
        System.out.println("poll(): " + queue.poll());
        System.out.println("剩余: " + queue);
    }

    /**
     * 示例3：SynchronousQueue（同步队列）
     * 特点：每个 put 必须等待一个 take，容量为 0
     */
    static void demoSynchronousQueue() throws InterruptedException {
        System.out.println("【示例3】SynchronousQueue - 同步队列");
        System.out.println("------------------------------------------------");
        System.out.println("说明：容量为0，每个 put 必须等待一个 take，用于线程间直接传递数据");

        BlockingQueue<String> queue = new SynchronousQueue<>();

        Thread sender = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String msg = "数据-" + i;
                    queue.put(msg);  // 必须等待消费者取走
                    System.out.println("发送: " + msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "发送者");

        Thread receiver = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    Thread.sleep(500);  // 模拟处理时间
                    String msg = queue.take();  // 必须等待发送者放入
                    System.out.println("接收: " + msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "接收者");

        sender.start();
        receiver.start();

        sender.join();
        receiver.join();
    }

    /**
     * 示例4：BlockingQueue 阻塞方法演示
     */
    static void demoBlockingMethods() throws InterruptedException {
        System.out.println("【示例4】BlockingQueue 阻塞方法演示");
        System.out.println("------------------------------------------------");
        System.out.println("说明：展示不同特性的插入和移除方法");

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);

        // 1. add() - 满时抛异常
        System.out.println("\n--- add() - 满时抛异常 ---");
        queue.add(1);
        queue.add(2);
        queue.add(3);
        try {
            queue.add(4);  // 会抛出 IllegalStateException
        } catch (IllegalStateException e) {
            System.out.println("队列已满，add() 抛异常: " + e.getClass().getSimpleName());
        }

        // 2. offer() - 满时返回 false
        System.out.println("\n--- offer() - 满时返回 false ---");
        boolean result = queue.offer(4);
        System.out.println("队列满时 offer(4) 返回: " + result);

        // 3. put() - 满时阻塞
        System.out.println("\n--- put() - 满时阻塞 ---");
        System.out.println("put(4) 会阻塞，等待消费者消费...");

        // 启动消费者
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(500);
                queue.take();
                System.out.println("消费者取走一个元素");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "消费者");
        consumer.start();

        queue.put(4);  // 会阻塞直到消费者取走元素
        System.out.println("put(4) 完成");

        consumer.join();

        // 4. remove() - 空时抛异常
        queue.clear();
        System.out.println("\n--- remove() - 空时抛异常 ---");
        try {
            queue.remove();
        } catch (Exception e) {
            System.out.println("队列为空，remove() 抛异常: " + e.getClass().getSimpleName());
        }

        // 5. poll() - 空时返回 null
        System.out.println("\n--- poll() - 空时返回 null ---");
        Integer val = queue.poll();
        System.out.println("队列为空 poll() 返回: " + val);

        // 6. take() - 空时阻塞
        System.out.println("\n--- take() - 空时阻塞 ---");
        System.out.println("take() 会阻塞，等待生产者放入...");

        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(500);
                queue.put(100);
                System.out.println("生产者放入元素");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "生产者");
        producer.start();

        Integer taken = queue.take();
        System.out.println("take() 取得: " + taken);

        producer.join();
    }
}