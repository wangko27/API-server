package io.nuls.api.counter;

import io.nuls.api.server.business.CounterInterface;
import io.nuls.api.utils.log.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class QueryCounter {
    private static volatile int balance_top_modify = 1;
    private static volatile int mined_top_modify = 1;
    private static volatile int balance_top_total = 0;
    private static volatile int mined_top_total = 0;
    private static final Lock B_LOCK = new ReentrantLock();
    private static final Lock M_LOCK = new ReentrantLock();
    private static final ReentrantReadWriteLock B_RW_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock M_RW_LOCK = new ReentrantReadWriteLock();



    public static int getBalance(CounterInterface counter) {
        // need reCount
        if(getBalanceModify() == 1) {
            return setBalance(counter);
        }
        return balance_top_total;
    }

    private static int setBalance(CounterInterface counter) {
        B_LOCK.lock();
        try {
            if(balance_top_modify == 1) {
                balance_top_modify = 0;
                balance_top_total = counter.countTableList(null);
                return balance_top_total;
            } else {
                return balance_top_total;
            }
        } finally {
            B_LOCK.unlock();
        }
    }

    public static int getMined(CounterInterface counter) {
        // need reCount
        if(getMinedModify() == 1) {
            return setMined(counter);
        }
        return mined_top_total;
    }

    private static int setMined(CounterInterface counter) {
        M_LOCK.lock();
        try {
            if(mined_top_modify == 1) {
                mined_top_modify = 0;
                mined_top_total = counter.countTableList(null);
                return mined_top_total;
            } else {
                return mined_top_total;
            }
        } finally {
            M_LOCK.unlock();
        }
    }

    public static void setBalanceModify(int modify) {
        B_RW_LOCK.writeLock().lock();
        try {
            balance_top_modify = modify;
        } finally {
            B_RW_LOCK.writeLock().unlock();
        }
    }
    public static int getBalanceModify() {
        B_RW_LOCK.readLock().lock();
        try {
            return balance_top_modify;
        } finally {
            B_RW_LOCK.readLock().unlock();
        }
    }

    public static void setMinedModify(int modify) {
        M_RW_LOCK.writeLock().lock();
        try {
            mined_top_modify = modify;
        } finally {
            M_RW_LOCK.writeLock().unlock();
        }
    }

    public static int getMinedModify() {
        M_RW_LOCK.readLock().lock();
        try {
            return mined_top_modify;
        } finally {
            M_RW_LOCK.readLock().unlock();
        }
    }
}
