package demo03_JUC;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * ConcurrentHashMap 示例
 * 展示：线程安全哈希表 vs 非线程安全 vs synchronized 包装
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== ConcurrentHashMap 示例 ==========\n");

        // 示例1：多线程并发操作 Map 安全性对比
        demoConcurrentMap();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 示例2：ConcurrentHashMap 核心方法演示
        demoCoreMethods();
    }

    /**
     * 示例1：多线程并发操作 Map 安全性对比
     */
    static void demoConcurrentMap() throws InterruptedException {
        System.out.println("【示例1】多线程并发操作 Map 安全性对比");
        System.out.println("------------------------------------------------");
        System.out.println("说明：对比 HashMap、Hashtable、Collections.synchronizedMap、ConcurrentHashMap");

        int threadCount = 100;
        int operationCount = 1000;

        // 1. HashMap（非线程安全）
        System.out.println("\n--- HashMap（非线程安全）---");
        testMap(new HashMap<>(), threadCount, operationCount, false);

        // 2. Hashtable（synchronized 修饰方法）
        System.out.println("\n--- Hashtable（synchronized）---");
        testMap(new Hashtable<>(), threadCount, operationCount, true);

        // 3. Collections.synchronizedMap（synchronized 包装）
        System.out.println("\n--- Collections.synchronizedMap ---");
        testMap(Collections.synchronizedMap(new HashMap<>()), threadCount, operationCount, true);

        // 4. ConcurrentHashMap（分段锁/CAS）
        System.out.println("\n--- ConcurrentHashMap（分段锁/CAS）---");
        testMap(new ConcurrentHashMap<>(), threadCount, operationCount, true);
    }

    static void testMap(Map<String, Integer> map, int threadCount, int operationCount, boolean expectCorrect)
            throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    startLatch.await();  // 等待所有线程就绪

                    for (int j = 0; j < operationCount; j++) {
                        String key = "key-" + threadId;
                        map.put(key, j);

                        if (map.get(key) == null) {
                            System.out.println("出现 null 键值！线程不安全！");
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();  // 启动所有线程
        endLatch.await();  // 等待所有线程完成

        int size = map.size();
        int expectedSize = threadCount;
        System.out.println("预期大小: " + expectedSize + ", 实际大小: " + size);

        if (size == expectedSize) {
            System.out.println("✓ 线程安全");
        } else {
            System.out.println("✗ 线程不安全！");
        }
    }

    /**
     * 示例2：ConcurrentHashMap 核心方法演示
     * 特点：putIfAbsent、compute、merge 等原子操作
     */
    static void demoCoreMethods() {
        System.out.println("【示例2】ConcurrentHashMap 核心方法");
        System.out.println("------------------------------------------------");
        System.out.println("说明：展示 ConcurrentHashMap 的原子操作方法");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // putIfAbsent：key 不存在时才插入
        map.put("apple", 1);
        Integer result1 = map.putIfAbsent("apple", 2);  // 不会覆盖，返回原值
        System.out.println("putIfAbsent('apple', 2) 返回: " + result1 + ", 当前值: " + map.get("apple"));

        Integer result2 = map.putIfAbsent("banana", 2);  // 会插入
        System.out.println("putIfAbsent('banana', 2) 返回: " + result2 + ", 当前值: " + map.get("banana"));

        // compute：原子计算
        map.compute("count", (key, oldValue) -> {
            int old = oldValue == null ? 0 : oldValue;
            return old + 100;
        });
        System.out.println("compute('count') 后: " + map.get("count"));

        // merge：原子合并
        map.merge("count", 50, (oldValue, newValue) -> oldValue + newValue);
        System.out.println("merge('count', 50) 后: " + map.get("count"));

        // getOrDefault：获取默认值
        int value = map.getOrDefault("notExist", 0);
        System.out.println("getOrDefault('notExist', 0): " + value);

        System.out.println("\n最终 Map 内容: " + map);
    }
}