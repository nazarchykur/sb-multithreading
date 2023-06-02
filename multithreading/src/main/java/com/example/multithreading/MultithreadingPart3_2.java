package com.example.multithreading;

public class MultithreadingPart3_2 {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }
    public static void main(String[] args) {
        MultithreadingPart3_2 multithreadingPart3 = new MultithreadingPart3_2();
        multithreadingPart3.doWork();
    }

    public void doWork() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                increment();
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
