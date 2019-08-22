package com.cimc.basic.test;

import org.apache.zookeeper.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper基础API使用
 *
 * @author chenz
 * @create 2019-08-22 10:37
 */
public class ZookeeperBasicAPITest {

    /**
     * 连接地址
     */
    private static final String ADDRESS = "127.0.0.1:2181";

    /**
     * 超时时间
     */
    private static final int SESSION_TIME_OUT = 2000;
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void testCreate() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper(ADDRESS, SESSION_TIME_OUT, new Watcher() {

            public void process(WatchedEvent event) {
                // 获取事件状态
                Event.KeeperState keeperState = event.getState();
                // 获取事件类型
                Event.EventType eventType = event.getType();

                if (Event.KeeperState.SyncConnected == keeperState) {
                    if (Event.EventType.None == eventType) {
                        countDownLatch.countDown();
                        System.out.println("开启连接............");
                    }
                }
            }
        });
        countDownLatch.await();
        // 创建节点，参数如下
        // path	创建节点的路径
        // data[] 字节数组，创建节点初始化内容。使用者需自己进行序列化和反序列化。复杂对象可使用 Hessian或Kryo进行进行序列化和反序列化。
        // acl 节点的acl策略
        // createMode 节点类型，类型定义在枚举CreateMode中
        // 包括PERSISTENT：持久; PERSISTENT_SEQUENTIAL：持久顺序; EPHEMERAL：临时; EPHEMERAL_SEQUENTIAL：临时顺序
        String result = zooKeeper.create("/data", "data_a".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        System.out.println("result:" + result);
        Thread.sleep(1000 * 10);
        zooKeeper.close();
    }

}
