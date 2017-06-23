package com.wls.mist.socketstudy.tool;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 * Created by 13526 on 2017/6/12.
 */

public class ThreadPoolTool {
    private static ThreadPoolTool threadPoolTool;
    private static ExecutorService executorService = null;
    private static final int coreThreadNum = 3;
    private static ThreadFactory factory = new ThreadFactory() {
        AtomicInteger integer = new AtomicInteger();
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r,"thread:"+integer.getAndIncrement());
        }
    };
    static {
        if (executorService == null)
             executorService = Executors.newFixedThreadPool(coreThreadNum,factory);
    }


    private ThreadPoolTool(){}
    public static ThreadPoolTool getInstance(){
        if (threadPoolTool == null)
            threadPoolTool = new ThreadPoolTool();
        return threadPoolTool;
    }
    //获取线程池对象
    public void getExecutorService(){
        if (executorService == null){
            executorService = Executors.newFixedThreadPool(coreThreadNum,factory);
        }
    }
    //执行线程
    public void execute(Runnable runnable){
        if (executorService != null)
            executorService.execute(runnable);
    }


}
