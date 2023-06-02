package com.example.multithreading;

public class ThreadsMain {
    public static void main(String[] args) {
        System.out.println(" main() starts..." + Thread.currentThread().getName());
        new MyThread().start();
        new MyThread().start();

        System.out.println(" main() ends..." + Thread.currentThread().getName());

    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread1 starts..." + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
            System.out.println("Thread1 doing something...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread1 ends..." + Thread.currentThread().getName());
    }
}
