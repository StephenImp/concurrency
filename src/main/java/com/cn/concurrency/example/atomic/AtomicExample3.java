package com.cn.concurrency.example.atomic;

import com.cn.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@ThreadSafe
public class AtomicExample3 {

    // 请求总数
    public static int clientTotal = 5000;

    // 同时并发执行的线程数
    public static int threadTotal = 200;

    /**
     * 1.8新增的类
     *
     * 对于long  与 double  对象，JVM 允许将64位的读操作或者写操作，拆分成两个32位的操作
     *
     * LongAdder  核心：将热点数据分离
     *
     * 这个比 AtomicLong  效率更高，低并发场景下，AtomicLong 效率高一些
     *
     * 缺点：会有误差。
     */
    public static LongAdder count = new LongAdder();

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal ; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("count:{}", count);
    }

    private static void add() {
        count.increment();
    }
}
