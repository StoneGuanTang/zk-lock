package com.zk.lock;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create by tgss on 2021/4/28 19:54
 **/
public class App {

    private ZooKeeper zk;

    @Before
    public void init() {
        this.zk = ZkUtil.getZk();
    }

    @Test
    public void testLock() {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                Lock lock = new LockImpl(zk, threadName);
                lock.tryLock();
                System.out.println(threadName + "do somethings");
                lock.unlock();
            });
        }

        while (true) {

        }
    }

}
