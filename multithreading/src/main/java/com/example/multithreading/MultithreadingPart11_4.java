package com.example.multithreading;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingPart11_4 {

    /*
    Цей код продовжує демонструвати використання блокування замків (locks) для уникнення ситуації взаємного блокування
    (deadlock) у багатопотоковому середовищі. Клас Runner11_4 представляє сценарій, де два потоки конкурентно маніпулюють
    об'єктами Account11_4.

    У цьому коді створюються два об'єкти Account11_4, account1 та account2, які представляють рахунки з початковим
    балансом 10000. Кожен потік (t1 та t2) виконує свою функцію - firstThread або secondThread.

    Метод acquireLocks використовується для отримання блокування для двох замків lock1 та lock2. Цей метод використовує
    конструкцію "try-finally" для гарантованого звільнення замків у будь-якому випадку.

    У методі acquireLocks виконується спроба отримати блокування для обох замків. Якщо обидва замка успішно отримані,
    метод просто повертається, і програма може продовжити виконання. Якщо потік не вдалося отримати обидва замка, то він
    розблоковує будь-які замки, які вже були отримані, і чекає коротку паузу за допомогою Thread.sleep(10). Після цього
    він повторює спробу отримати замки знову. Такий підхід забезпечує уникнення взаємного блокування.

    У методах firstThread та secondThread викликається метод acquireLocks для отримання блокування перед тим, як потоки
    маніпулюють об'єктами Account11_4. Після отримання блокування потоки виконують операцію переказу грошей між рахунками
    (transfer) за допомогою методу Account11_4.transfer(). Після завершення операції потоки розблоковують замки.

    Метод finished виводить інформацію про стан рахунків, включаючи баланс кожного рахунку та загальний баланс.

    Цей код ілюструє підхід до управління блокуванням для уникнення взаємного блокування. Використання блокування замків
    допомагає забезпечити правильну синхронізацію доступу до спільних ресурсів у багатопотокових програмах.

     */

    public static void main(String[] args) throws InterruptedException {
        Runner11_4 runner11 = new Runner11_4();
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

class Runner11_4 {

    private Account11_4 account1 = new Account11_4();
    private Account11_4 account2 = new Account11_4();

    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();

    public void acquireLocks(Lock lock1, Lock lock2) throws InterruptedException {
        while (true) {
            // Acquire locks
            boolean gotFirstLock = false;
            boolean gotSecondLock = false;

            try {
                gotFirstLock = lock1.tryLock();
                gotSecondLock = lock2.tryLock();
            } finally {
                if (gotFirstLock && gotSecondLock) {
                    return;
                }
                if (gotFirstLock) {
                    lock1.unlock();
                }
                if (gotSecondLock) {
                    lock2.unlock();
                }
            }

            // Locks are not acquired
            Thread.sleep(10);
        }

    }

    public void firstThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10_000; i++) {
            acquireLocks(lock1, lock2);

            try {
                Account11_4.transfer(account1, account2, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void secondThread() throws InterruptedException {
        Random random = new Random();

        for (int i = 0; i < 10_000; i++) {
            acquireLocks(lock1, lock2);

            try {
                Account11_4.transfer(account2, account1, random.nextInt(100));
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

            Account1 balance = 16965
            Account2 balance = 3035
            Total balance = 20000
         */
    }
}

class Account11_4 {
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

    public static void transfer(Account11_4 from, Account11_4 to, int amount) {
        from.withdraw(amount);
        to.deposit(amount);
    }
}