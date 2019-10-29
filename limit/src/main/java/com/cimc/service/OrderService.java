package com.cimc.service;

import org.springframework.stereotype.Service;

/**
 * @author chenz
 * @create 2019-10-29 11:05
 */
@Service
public class OrderService {

    public boolean addOrder() {
        System.out.println("db....正在操作订单表数据库...");
        return true;
    }

}
