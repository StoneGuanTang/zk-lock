package com.zk.lock;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * create by tgss on 2021/4/28 19:59
 **/
public class ZkUtil {

    private static final String address = "localhost:2181,localhost:2182,localhost:2183/testLock";

    private static final int sessionTimeOut = 5000;

    private static final CountDownLatch cd = new CountDownLatch(1);

    private static final Watcher watcher = new DefaultWatcher(cd);

    public static ZooKeeper getZk() {
        try {
            ZooKeeper zk = new ZooKeeper(address, sessionTimeOut, watcher);
            cd.await();
            return zk;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("create zk error");
            return null;
        }
    }
}
