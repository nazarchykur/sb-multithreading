package com.example.multithreading;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingPart11_2 {
    public static void main(String[] args) throws InterruptedException {
        Runner11_2 runner11 = new Runner11_2();
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

class Runner11_2 {

    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();

    private Account11_2 account1 = new Account11_2();
    private Account11_2 account2 = new Account11_2();


    public void firstThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10_000; i++) {
            lock1.lock();
            lock2.lock();
            try {
                Account11_2.transfer(account1, account2, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void secondThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10_000; i++) {
            lock1.lock();
            lock2.lock();
            try {
                Account11_2.transfer(account2, account1, random.nextInt(100));
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

class Account11_2 {
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

    public static void transfer(Account11_2 from, Account11_2 to, int amount) {
        from.withdraw(amount);
        to.deposit(amount);
    }
}
