本项目是为了更好的掌握java的语法而产生的，其主要目的是方便各位同学/自己去实际看看语法当中当中的区别，打下坚实的基础

目前板块如下

1.review板块
  
就放在项目包下，用途是方便同学进行借鉴自己归纳总结出来的一些要点


2.log.txt板块

也是放在项目包下，对于一些实际demo运行当中出现的问题自己也进行了思考和解答




3.src/main/java/com.example.sudemo模块，也是主要内容，包含应该存在的各项类，欢迎提建议以及补充

（1）demo01_thread_create包下 

为对应包名实例，主要用于多线程

CallableDemo  Callable 

接口类似于 Runnable，但它可以返回一个结果，并且可以抛出受检异常，只有一个方法 call()

RunnableDemo    

Runnable接口：实现了线程与任务的分离，仅定义了一个抽象方法run()

ThreadCreateDemo 

Thread接口，最基础的多线程接口

ThreadStateDemo     

展示 Java 线程的六种状态：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED

（2）demo02_synchronized_and_Lock，主要用于锁

LockDemo    

ReentrantLock（可重入、可中断、公平/非公平）、ReentrantReadWriteLock（读写锁）

SynchronizedDemo    

对象锁、类锁、可重入特性、monitor 机制

（3） demo03_JUC（juc主要是为了开发者进行多线程编程时减少竞争条件和死锁的问题）

AtomicDemo  

AtomicInteger 基础使用（CAS 无锁）

BlockingQueueDemo  

有界队列、无界队列、同步队列

ConcurrentHashMapDemo   

线程安全哈希表 vs 非线程安全 vs synchronized 包装

CopyOnWriteArrayListDemo    

读多写少场景下的线程安全List

SyncToolsDemo   

CountDownLatch（倒计时门闩）、CyclicBarrier（循环栅栏）、Semaphore（信号量）、Exchanger（线程间数据交换）

ThreadLocalDemo ThreadLocal 

示例、每个线程独立副本

（4）demo04_JVM主要用于java虚拟机

HeapOOMDemo     

内存溢出、增大堆内存、内存泄漏分析



