package com.example.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

public class MultithreadingPart3_1 {
    private AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) {
        MultithreadingPart3_1 multithreadingPart3 = new MultithreadingPart3_1();
        multithreadingPart3.doWork();
    }

    public void doWork() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                count.incrementAndGet();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                count.incrementAndGet();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Count is = " + count);
    }
}
