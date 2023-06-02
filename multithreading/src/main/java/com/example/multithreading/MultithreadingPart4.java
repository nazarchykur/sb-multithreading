package com.example.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultithreadingPart4 {
    public static void main(String[] args) {
        new Worker().doWork();
        /*
            Starting ...
            Time takes = 2360
            list1 = 1000, list2 = 1000
         */

        new Worker2().doWork();
        /*
        we create 2 thread and then run them:
        we can see that it takes 2 seconds, but we have problem with shared lists, and no-one has 2000 elements
        because 2 thread are running at the same time for each list

            Starting Worker 2 ...
            Time takes = 2396
            list1 = 1996, list2 = 1995
         */

        new Worker3().doWork();
        /*
        we create 2 thread and then run them:
        but now we add synchronized for stageOne and stageTwo, so each list now has 2000 elements, but it takes seconds
        because as we know now each thread takes method and do its work and others threads are waiting for it, nad only
        after current thread is finished, the next thread can do its work with this method

        so synchronized works as expected, but it adds lock to its class and another synchronized method cannot be taken
        by another thread because it is locked by current thread, that why it takes double time = more than 4 seconds

            Starting Worker 3 ...
            Time takes = 4759
            list1 = 2000, list2 = 2000
         */

        new Worker4().doWork();
        /*
        now we added only often used code by synchronized lock object for needed piece of code, and we can see that it
        takes only 2 seconds and works properly

        class now does not locked by current thread
        so in this situation we have 2 threads which can do its work simultaneously by calling stageOne and stageTwo
        methods. Because only inside these methods some piece of code is synchronized and would be locked by current
        thread and only after the current thread finished, the next thread can do its work and use this piece of code

            Starting Worker 4 ...
            Time takes = 2454
            list1 = 2000, list2 = 2000
         */
    }
}

class Worker {
    private Random random = new Random();
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    public void stageOne() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list1.add(random.nextInt(100));
    }

    public void stageTwo() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list2.add(random.nextInt(100));
    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public void doWork() {
        System.out.println("Starting Worker ...");
        long start = System.currentTimeMillis();
        process();
        long end = System.currentTimeMillis();

        System.out.println("Time takes = " + (end - start));
        System.out.println("list1 = " + list1.size() + ", list2 = " + list2.size());
    }
}

class Worker2 {
    private Random random = new Random();
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    public void stageOne() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list1.add(random.nextInt(100));
    }

    public void stageTwo() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list2.add(random.nextInt(100));
    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public void doWork() {
        System.out.println("Starting Worker 2 ...");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            process();
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            process();
        });
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();

        System.out.println("Time takes = " + (end - start));
        System.out.println("list1 = " + list1.size() + ", list2 = " + list2.size());
    }
}

class Worker3 {
    private Random random = new Random();
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    public synchronized void stageOne() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list1.add(random.nextInt(100));
    }

    public synchronized void stageTwo() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list2.add(random.nextInt(100));
    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public void doWork() {
        System.out.println("Starting Worker 3 ...");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            process();
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            process();
        });
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();

        System.out.println("Time takes = " + (end - start));
        System.out.println("list1 = " + list1.size() + ", list2 = " + list2.size());
    }
}

class Worker4 {
    private Random random = new Random();

    private Object lock1 = new Object();
    private Object lock2 = new Object();
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    public void stageOne() {
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list1.add(random.nextInt(100));
        }
    }

    public void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list2.add(random.nextInt(100));
        }
    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public void doWork() {
        System.out.println("Starting Worker 4 ...");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            process();
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            process();
        });
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();

        System.out.println("Time takes = " + (end - start));
        System.out.println("list1 = " + list1.size() + ", list2 = " + list2.size());
    }
}