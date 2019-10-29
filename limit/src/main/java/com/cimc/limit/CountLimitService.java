package com.cimc.limit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数器
 *
 * @author chenz
 */
public class CountLimitService {
    /**
     * 限制最大访问的容量
     */
    private int limitCount = 60;

    /**
     * 每秒钟 实际请求的数量
     */
    AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 获取当前系统时间
     */
    private long start = System.currentTimeMillis();

    /**
     * 间隔时间60秒
     */
    private int interval = 60;

    public boolean acquire() {
        long newTime = System.currentTimeMillis();
        if (newTime > (start + interval)) {
            // 判断是否是一个周期
            start = newTime;
            // 清理为0
            atomicInteger.set(0);
            return true;
        }
        // i++;
        atomicInteger.incrementAndGet();
        return atomicInteger.get() <= limitCount;
    }

    static CountLimitService limitService = new CountLimitService();

    public static void main(String[] args) {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 1; i < 100; i++) {
            final int tempI = i;
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (limitService.acquire()) {
                        System.out.println("你没有被限流,可以正常访问逻辑 i:" + tempI);
                    } else {
                        System.out.println("你已经被限流呢  i:" + tempI);
                    }
                }
            });
        }
    }

}
