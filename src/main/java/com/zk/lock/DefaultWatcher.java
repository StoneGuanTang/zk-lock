package com.zk.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * create by tgss on 2021/4/28 20:04
 **/
public class DefaultWatcher implements Watcher {

    private CountDownLatch cd;

    public DefaultWatcher(CountDownLatch cd) {
        this.cd = cd;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState state = watchedEvent.getState();
        System.out.println("client state" + state);
        switch (state) {
            case Unknown:
                break;
            case Disconnected:
                System.out.println("client disconnected");
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println("client SyncConnected");
                cd.countDown();
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            case Closed:
                break;
        }
    }
}
