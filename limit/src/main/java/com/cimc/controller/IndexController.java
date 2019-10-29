package com.cimc.controller;

import com.cimc.annotation.ExtRateLimiter;
import com.cimc.service.OrderService;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 功能说明:使用RateLimiter 实现令牌桶算法
 *
 * @author chenz
 */
@RestController
public class IndexController {

    @Autowired
    private OrderService orderService;

    /**
     * 1.0 表示 每秒中生成1个令牌存放在桶中
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(1.0);

    /**
     * 下单请求
     *
     * @return
     */
    @RequestMapping("/order")
    public String order() {
        // 1.限流判断
        // 如果在500秒内 没有获取不到令牌的话，则会一直等待
        System.out.println("生成令牌等待时间:" + rateLimiter.acquire());
        boolean acquire = rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS);
        if (!acquire) {
            System.out.println("你在怎么抢，也抢不到，因为会一直等待的，你先放弃吧！");
            return "你在怎么抢，也抢不到，因为会一直等待的，你先放弃吧！";
        }

        // 2.如果没有达到限流的要求,直接调用订单接口
        boolean isOrderAdd = orderService.addOrder();
        if (isOrderAdd) {
            return "恭喜您,抢购成功!";
        }
        return "抢购失败!";
    }

    @RequestMapping("/order2")
    @ExtRateLimiter(value = 1.0, timeout = 500)
    public String order2() {
        boolean isOrderAdd = orderService.addOrder();
        if (isOrderAdd) {
            return "恭喜您,抢购成功!";
        }
        return "抢购失败!";
    }

}
