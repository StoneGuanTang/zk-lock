package com.zk.lock;

/**
 * create by tgss on 2021/4/28 19:54
 **/
public interface Lock {

    void tryLock();

    void unlock();
}
