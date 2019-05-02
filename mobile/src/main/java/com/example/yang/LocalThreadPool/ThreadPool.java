package com.example.yang.LocalThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

    /* 1.线程池状态

　　 * 2.任务的执行

　　 * 3.线程池中的线程初始化

　　 * 4.任务缓存队列及排队策略

　　 * 5.任务拒绝策略

　　 * 6.线程池的关闭

　　 * 7.线程池容量的动态调整*/
    /*
    * 以上是线程池操作必须考虑的问题点*/
    
public abstract class ThreadPool implements Runnable, Comparable<ThreadPool> {

    ExecutorService LocaleThreadPool;
    private int priority;

    public ThreadPool(int priority) {
        if (priority < 0)
            throw new IllegalArgumentException();
        this.priority = priority;
    }

    public void CreateThreadPool(){
        LocaleThreadPool = new ThreadPoolExecutor(
                3,
                3,
                0L,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>());
    }

    public void AddTask(Runnable task){
        LocaleThreadPool.execute(task);
    }

    @Override
    public int compareTo(ThreadPool another) {
        int my = this.getPriority();
        int other = another.getPriority();
        return my < other ? 1 : my > other ? -1 : 0;
    }

    @Override
    public void run() {
        doSth();
    }

    public abstract void doSth();

    public int getPriority() {
        return priority;
    }
}
