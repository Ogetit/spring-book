package org.github.core.modules.lock;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本地锁，只能在同一个线程中lock和unlcok 如果在一个线程中lock 在另外一个线程中unlock 则会报错
 * <p/>
 * Lock lock = ...;
 * if (lock.tryLock()) {
 * try {
 * // manipulate protected state
 * } finally {
 * lock.unlock();
 * }
 * } else {
 * // perform alternative actions
 * }
 */
public class ReentrantLockBillLock implements IBatchBillLockHandler {
    private static Map<String, ReentrantLock> cache = new ConcurrentHashMap<String, ReentrantLock>();
    private static Interner<String> pool = Interners.newWeakInterner();

    private ReentrantLock getLock(IBillIdentify billIdentify) {
        String key = getKey(billIdentify);
        ReentrantLock lock = cache.get(key);
        if (lock == null) {
            synchronized (pool.intern(key)) {
                lock = cache.get(key);
                if (lock == null) {
                    lock = new ReentrantLock(true);
                    cache.put(key, lock);
                }
            }
        }
        return lock;
    }

    /**
     * 当使用内部锁时，一旦开始请求，锁就不能停止了，所以内部锁给实现具有时限的活动带来了风险。为了解决这一问题，可以使用定时锁。当具有时限的活
     * 动调用了阻塞方法，定时锁能够在时间预算内设定相应的超时。如果活动在期待的时间内没能获得结果，定时锁能使程序提前返回。可定时的锁获取模式，由tryLock(long, TimeUnit)方法实现。
     *
     * @param billIdentify
     * @return
     */
    @Override
    public boolean tryLock(IBillIdentify billIdentify) {
        ReentrantLock lock = getLock(billIdentify);
        if (billIdentify.getSingle_expire_time() > 0) {
            try {
                return lock.tryLock(billIdentify.getSingle_expire_time(), TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            /**
             * 此方法仅在调用时锁为空闲状态才获取该锁。如果锁可用，则获取锁，并立即返回值true。如果锁不可用，则此方法将立即返回值false。此方法的典型使用语句如下：
             */
            return lock.tryLock();
        }
    }

    @Override
    public boolean tryLock(IBillIdentify billIdentify, long timeout, TimeUnit unit) {
        ReentrantLock lock = getLock(billIdentify);
        try {
            if (timeout > 0 && unit != null) {
                return lock.tryLock(timeout, unit);
            } else {
                return lock.tryLock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void lock(IBillIdentify billIdentify) {
        ReentrantLock lock = getLock(billIdentify);
        lock.lock();
    }

    @Override
    public void unLock(IBillIdentify billIdentify) {
        String key = getKey(billIdentify);
        ReentrantLock lock = cache.get(key);
        if (lock != null) {
            lock.unlock();
            if (!lock.isLocked()) {
                cache.remove(key);
            }
        }
    }

    @Override
    public void unLock(List<IBillIdentify> billIdentifyList) {
        for (IBillIdentify billIdentify : billIdentifyList) {
            unLock(billIdentify);
        }
    }

    private String getKey(IBillIdentify billIdentify) {
        return billIdentify.uniqueIdentify();
    }


}
