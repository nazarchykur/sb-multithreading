package com.example.multithreading;

public class MultithreadingPart3 {

    private int count = 0;

    public static void main(String[] args) {
        MultithreadingPart3 multithreadingPart3 = new MultithreadingPart3();
        multithreadingPart3.doWork();
    }

    public void doWork() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                count++;
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                count++;
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
        /*
        sometimes it works but not always:
            Count is = 17983
            Count is = 16652
            Count is = 20000 - ok
            Count is = 10872
         */
    }
}