package com.example.multithreading;

public class RunnableMain {
    public static void main(String[] args) {
        System.out.println(" main() starts..." + Thread.currentThread().getName());
        new Thread(() -> {
            System.out.println("Thread2 starts..." + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
                System.out.println("Thread2 doing something...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread2 ends..." + Thread.currentThread().getName());
        }).start();

        new Thread(() -> {
            System.out.println("Thread3 starts..." + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
                System.out.println("Thread3 doing something...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread3 ends..." + Thread.currentThread().getName());
        }).start();

        System.out.println(" main() ends..." + Thread.currentThread().getName());

    }
}