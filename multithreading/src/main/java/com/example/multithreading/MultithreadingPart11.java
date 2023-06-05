package com.example.multithreading;

import java.util.Random;

public class MultithreadingPart11 {
    public static void main(String[] args) throws InterruptedException {
        Runner11 runner11 = new Runner11();
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

class Runner11 {

    private Account account1 = new Account();
    private Account account2 = new Account();


    public void firstThread() throws InterruptedException {
        Random random = new Random();
        for (int i = 0; i < 10_000; i++) {
            Account.transfer(account1, account2, random.nextInt(100));
        }
    }

    public void secondThread() throws InterruptedException {
        Random random = new Random();
        for (int i = 0; i < 10_000; i++) {
            Account.transfer(account2, account1, random.nextInt(100));
        }
    }

    public void finished() {

        System.out.println("Account1 balance = " + account1.getBalance());
        System.out.println("Account2 balance = " + account2.getBalance());
        System.out.println("Total balance = " + (account1.getBalance() + account2.getBalance()));

        /*
        It SHOULD NOT work properly because of multithreading problem (without synchronization)

            Account1 balance = 33726
            Account2 balance = 26414
            Total balance = 60140
         */
    }
}

class Account {
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

    public static void transfer(Account from, Account to, int amount) {
        from.withdraw(amount);
        to.deposit(amount);
    }
}