package com.example.multithreading;

public class MultithreadingPart10 {
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

class Runner10 {

    private int count = 0;

    private void incrementCount() {
        for (int i = 0; i < 10_000; i++) {
            count++;
        }
    }
    public void firstThread() throws InterruptedException {
        incrementCount();
    }

    public void secondThread() throws InterruptedException {
        incrementCount();
    }

    public void finished() {
        System.out.println("Count is = " + count); // Count is = 14958 | Count is = 20000 | Count is = 10872
    }
}