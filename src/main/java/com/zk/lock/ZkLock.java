package com.zk.lock;

import org.apache.zookeeper.ZooKeeper;

/**
 * create by tgss on 2021/4/29 1:34
 **/
public class ZkLock {

    private ZooKeeper zk;

    public ZkLock(ZooKeeper zk) {
        this.zk = zk;
    }

    public Lock getLok(String lockName) {
        return new LockImpl(zk, lockName);
    }

}
