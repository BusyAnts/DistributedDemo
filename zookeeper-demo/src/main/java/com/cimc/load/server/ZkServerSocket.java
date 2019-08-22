package com.cimc.load.server;

import org.I0Itec.zkclient.ZkClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket服务端
 *
 * @author chenz
 */
public class ZkServerSocket implements Runnable {
    private static int port = 8080;
    private String parentService = "/service";
    /**
     * 服务器端:<br>
     * 1.服务端启动的时候，会将当前服务信息注册到注册中心。首先先创建一个父节点为service，在父节点下面在创建一个子节点，
     * 每个子节点都存放当前服务接口地址。<br>
     * ##节点结构 <br>
     * /service 持久节点<br>
     * #####/8080 value 127.0.0.1:8080 临时节点<br>
     * #####/8081 value 127.0.0.1:8081 临时节点<br>
     */
    private ZkClient zkClient = new ZkClient("127.0.0.1:2181");

    public ZkServerSocket(int port) {
        ZkServerSocket.port = port;
    }

    private void regServer() {
        // 1.先创建父节点service 为持久节点
        if (!zkClient.exists(parentService)) {
            // 2.创建父节点
            zkClient.createPersistent(parentService);
        }

        String serverKey = parentService + "/server_" + port;
        if (zkClient.exists(serverKey)) {
            zkClient.delete(serverKey);
        }
        // 3.创建子节点 value为服务接口地址
        zkClient.createEphemeral(serverKey, "127.0.0.1:" + port);

    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server start port:" + port);
            regServer();
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e2) {

            }
        }
    }

    public static void main(String[] args) throws IOException {
        ZkServerSocket server = new ZkServerSocket(port);
        Thread thread = new Thread(server);
        thread.start();
    }

}
