package demo04_JVM;

import java.util.ArrayList;
import java.util.List;

/**
 * 堆内存溢出示例及解决方案
 * 展示：内存溢出、增大堆内存、内存泄漏分析
 */
public class HeapOOMDemo {

    // 静态集合，模拟内存泄漏（持有对象引用）
    private static List<byte[]> list = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("========== 堆内存溢出示例及解决方案 ==========\n");

        // 示例1：基础堆内存溢出
        System.out.println("【示例1】堆内存溢出演示");
        System.out.println("------------------------------------------------");
        demoBasicOOM();

        // 示例2：解决方案1 - 增大堆内存
        System.out.println("\n【示例2】解决方案 - 增大堆内存");
        System.out.println("------------------------------------------------");
        System.out.println("JVM 参数: -Xms100m -Xmx100m");
        System.out.println("通过增大堆内存避免 OOM\n");

        // 示例3：模拟内存泄漏
        System.out.println("【示例3】内存泄漏演示");
        System.out.println("------------------------------------------------");
        demoMemoryLeak();
    }

    /**
     * 示例1：基础堆内存溢出
     */
    static void demoBasicOOM() {
        System.out.println("运行命令: java -Xms20m -Xmx20m -cp out demo04_JVM.HeapOOMDemo");
        System.out.println("结果: 当分配超过 20MB 时抛出 OutOfMemoryError\n");

        System.out.println("原因分析:");
        System.out.println("  -Xmx20m 设置堆最大为 20MB");
        System.out.println("  每次分配 1MB");
        System.out.println("  约 19 次后堆空间不足，抛出 OOM\n");

        System.out.println("解决方案:");
        System.out.println("  1. 增大堆内存: -Xmx512m");
        System.out.println("  2. 使用 MAT 分析内存泄漏");
    }

    /**
     * 示例3：模拟内存泄漏
     * 静态集合持有对象引用，导致 GC 无法回收
     */
    static void demoMemoryLeak() {
        System.out.println("说明: 静态集合持有对象引用，导致内存泄漏\n");

        // 不断向集合添加对象，但不清理
        int count = 0;
        try {
            while (true) {
                // 模拟缓存或日志，不断添加对象
                byte[] buffer = new byte[1024 * 1024]; // 1MB
                list.add(buffer);

                if (++count % 5 == 0) {
                    System.out.println("已添加 " + count + " MB 到列表");
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("\n========== 发生 OOM ==========");
            System.out.println("列表大小: " + list.size() + " 个对象");
            System.out.println("占用内存: 约 " + count + " MB");
            System.out.println("\n内存泄漏原因:");
            System.out.println("  - static List 持有大量 byte[] 引用");
            System.out.println("  - 即使对象不使用，也无法被 GC 回收");
            System.out.println("\n解决方案:");
            System.out.println("  1. 及时清理: list.clear()");
            System.out.println("  2. 使用软引用/弱引用");
            System.out.println("  3. 限制集合大小");
            System.out.println("  4. 使用 MAT 分析找出泄漏源");
        }
    }
}