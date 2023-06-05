package com.example.multithreading;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingPart10_2 {
    public static void main(String[] args) throws InterruptedException {
        Runner10_3 runner10 = new Runner10_3();
        Thread t1 = new Thread(() -> {
            try {
                runner10.firstThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                runner10.secondThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        runner10.finished();

    }
}

class Runner10_2 {

    private int count = 0;
    private Lock lock = new ReentrantLock();

    private void incrementCount() {
        for (int i = 0; i < 10_000; i++) {
            count++;
        }
    }

    public void firstThread() throws InterruptedException {
        // it is not a good idea to use lock here in this way because our method can throw exception and unlock() will not be called
//        lock.lock();
//        incrementCount();
//        lock.unlock();

        lock.lock();

        try {
            incrementCount();
        } finally {
            lock.unlock();
        }
    }

    public void secondThread() throws InterruptedException {
        lock.lock();

        try {
            incrementCount();
        } finally {
            lock.unlock();
        }
    }

    public void finished() {
        System.out.println("Count is = " + count); // Count is = 20000      now it works properly
    }
}