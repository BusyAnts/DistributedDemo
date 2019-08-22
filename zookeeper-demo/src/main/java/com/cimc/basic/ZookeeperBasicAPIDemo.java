package com.cimc.basic;

import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper基础API使用
 *
 * @author chenz
 * @create 2019-08-22 10:37
 */
public class ZookeeperBasicAPIDemo {

    /**
     * 连接地址
     */
    private static final String ADDRESS = "127.0.0.1:2181";

    /**
     * 超时时间
     */
    private static final int SESSION_TIME_OUT = 2000;
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
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

        System.out.println("/n1. 创建ZooKeeper节点");
        // 创建节点
        String result = zooKeeper.create("/data", "myData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        System.out.println("result:" + result);

        System.out.println("/n2. 查看是否创建成功： ");
        System.out.println(new String(zooKeeper.getData("/data", false, null)));

        System.out.println("/n3. 修改节点数据 ");
        zooKeeper.setData("/data", "newData".getBytes(), -1);

        System.out.println("/n4. 查看是否修改成功： ");
        System.out.println(new String(zooKeeper.getData("/data", false, null)));

        System.out.println("/n5. 删除节点 ");
        zooKeeper.delete("/data", -1);

        System.out.println("/n6. 查看节点是否被删除： ");
        System.out.println(" 节点状态： [" + zooKeeper.exists("/data", false) + "]");

        //创建临时节点，观察10秒后，是否自动消失
        zooKeeper.create("/tmp", "tmpData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        Thread.sleep(1000 * 10);
        zooKeeper.close();
    }

}
