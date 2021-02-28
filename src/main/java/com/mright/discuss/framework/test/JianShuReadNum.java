package com.mright.discuss.framework.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JianShuReadNum {

    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy()
        );
        String articleId = "fed330f7c3c0";//文章id
        //开启4个线程执行
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            poolExecutor.execute(() -> {
                int num = 10000;//每个线程刷取的阅读量
                for (int j = 0; j < num; j++) {
                    System.out.println(Thread.currentThread()+"====="+j);
                    Map map = new HashMap<>();
                    map.put("fuck",1);
                    HttpRequest.post("https://www.jianshu.com/shakespeare/notes/"+articleId+"/mark_viewed")
                            .header("Host", "www.jianshu.com")
                            .header("Origin", "https://www.jianshu.com")
                            .header("Referer", "https://www.jianshu.com/p/"+articleId)
                            .body(JSONUtil.toJsonStr(map), "application/json")
                            .execute();
                }
            });
        }
        poolExecutor.shutdown();
        while (!poolExecutor.isTerminated()) {
        }
        System.out.println("线程执行完成");

    }
}
