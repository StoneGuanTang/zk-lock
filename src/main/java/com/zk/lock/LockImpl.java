package com.zk.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * create by tgss on 2021/4/28 19:55
 **/
public class LockImpl implements Lock {

    private ZooKeeper zk;

    private final String lock = "/lock";

    private final CountDownLatch cd = new CountDownLatch(1);

    private String threadName;

    private LockCallBack lockCallBack;


    public LockImpl(ZooKeeper zk, String threadName) {
        this.zk = zk;
        this.threadName = threadName;
        this.lockCallBack = createCallBack();
    }

    public LockImpl(ZooKeeper zooKeeper) {
        this.zk = zooKeeper;
    }

    @Override
    public void tryLock() {
        zk.create(lock, threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this.lockCallBack, threadName);
        try {
            cd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlock() {
        try {
            zk.delete(this.lockCallBack.path, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    public LockCallBack createCallBack() {
        return new LockCallBack();
    }

    public class LockCallBack implements AsyncCallback.Create2Callback, AsyncCallback.ChildrenCallback, Watcher {

        private String path;

        private String lockName;

        /**
         * 节点创建后回调
         */
        @Override
        public void processResult(int i, String pathPrefix, Object value, String path, Stat stat) {
            this.path = path;
            this.lockName = path.substring(1);
            System.out.println(threadName + " create " + path);
            // 创建节点之后，获取父目录所有的子目录
            zk.getChildren("/", false, this, value);
        }

        /**
         * 获取子目录后的回调
         */
        @Override
        public void processResult(int i, String s, Object o, List<String> list) {
            Collections.sort(list);
            int index = list.indexOf(this.lockName);
            try {
                // 我是第一个临时节点，我可以获得锁
                if (index == 0) {
                    System.out.println("");

                    zk.setData("/", threadName.getBytes(), -1);
                    cd.countDown();
                } else {
                    // 否则的话，监听我前面的节点
                    zk.addWatch("/" + list.get(index - 1), this, AddWatchMode.PERSISTENT);
                }
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 监听上一个临时节点，上一个临时节点被删除的话，下一个临时节点要重新获取父目录下的子目录，判断自己是不是第一个节点（是否获得执行权）
         * @param watchedEvent
         */
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("pre node event");
            Event.EventType type = watchedEvent.getType();
            switch (type) {
                case None:
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    // 监听到前面的节点已经删除了，应该重新获取下父目录下面的子目录
                    zk.getChildren("/", false, this, threadName);
                    System.out.println("node delete");
                    break;
                case NodeDataChanged:
                    break;
                case NodeChildrenChanged:
                    break;
                case DataWatchRemoved:
                    break;
                case ChildWatchRemoved:
                    break;
                case PersistentWatchRemoved:
                    break;
            }
        }
    }


}
