package com.example.multithreading;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingPart11_3 {
    public static void main(String[] args) throws InterruptedException {
        Runner11_3 runner11 = new Runner11_3();
        Thread t1 = new Thread(() -> {
            try {
                runner11.firstThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                runner11.secondThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        runner11.finished();

    }
}

class Runner11_3 {

    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();

    private Account11_3 account1 = new Account11_3();
    private Account11_3 account2 = new Account11_3();


    public void firstThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10_000; i++) {
            lock1.lock();
            lock2.lock();
            try {
                Account11_3.transfer(account1, account2, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void secondThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10_000; i++) {
            /*
                !!! if we reorder of calling locks we will get deadlock
             */

            lock2.lock();
            lock1.lock();
            try {
                Account11_3.transfer(account2, account1, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void finished() {
        System.out.println("Account1 balance = " + account1.getBalance());
        System.out.println("Account2 balance = " + account2.getBalance());
        System.out.println("Total balance = " + (account1.getBalance() + account2.getBalance()));

        /*
        NOW it works properly

            Account1 balance = 5311
            Account2 balance = 14689
            Total balance = 20000
         */
    }
}

class Account11_3 {
    private int balance = 10000;

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }

    public static void transfer(Account11_3 from, Account11_3 to, int amount) {
        from.withdraw(amount);
        to.deposit(amount);
    }
}
