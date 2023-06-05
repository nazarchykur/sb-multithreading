package com.example.multithreading;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingPart10_3 {

    /*
    Цей код також є прикладом використання багатопотоковості (multithreading) в Java. Він демонструє використання замків
    (locks) і умовних змінних (condition variables) для синхронізації потоків.

    Клас MultithreadingPart10_3 містить метод main, який є точкою входу в програму. В цьому методі створюються два потоки
    (t1 і t2), які виконують методи firstThread і secondThread відповідно. Потім запускаються ці потоки (t1.start() і
    t2.start()) і головний потік (main) чекає, доки ці потоки завершаться (t1.join() і t2.join()). Після завершення обох
    потоків, викликається метод finished для виведення значення змінної count.

    Клас Runner10_3 містить реалізацію функціональності для потоків. Він має приватне поле count, яке буде зберігати
    значення, яке буде збільшуватись потоками. Також він має об'єкт lock типу ReentrantLock для створення замку і об'єкт
    condition типу Condition для створення умовної змінної.

    Метод incrementCount збільшує значення змінної count на 10 000. Це просто для демонстрації багатопотокового доступу
    до спільного ресурсу.

    Метод firstThread виконується в першому потоці (t1). Спочатку він блокує замок (lock.lock()) і виводить повідомлення
    "Waiting ...". Потім викликає condition.await(), що робить потік очікувати на цій умовній змінній. Поки потік очікує,
    інший потік (t2) може виконувати свою роботу. Після отримання сигналу (виклику condition.signal()) від іншого потоку,
    firstThread продовжить своє виконання, викликає метод incrementCount() і, нарешті, розблоковує замок (lock.unlock()).

    Метод secondThread виконується в другому потоці (t2). Спочатку він затримує потік на 1 секунду (Thread.sleep(1000)),
    щоб забезпечити, що перший потік буде очікувати. Потім він блокує замок (lock.lock()) і виводить повідомлення
    "Press the enter key to continue ...". Після того, як користувач натисне Enter, потік продовжить виконання, виведе
    повідомлення "Got it!" і викличе condition.signal(), щоб сигналізувати першому потоку, що він може продовжувати
    виконання. Потім метод incrementCount() викликається, а замок розблоковується.

    Метод finished просто виводить значення змінної count після того, як обидва потоки завершили своє виконання. З огляду
    на те, що обидва потоки викликають incrementCount() в своїх методах, очікується, що значення count буде 20 000
    (10 000 від кожного потоку).

     */

    /*
    В даному коді використовується інтерфейс Lock і його конкретна реалізація ReentrantLock для створення замку (lock)
    і забезпечення синхронізації між потоками.

    Lock є інтерфейсом, який надає методи для керування блокуванням і розблокуванням потоків. Він забезпечує більш гнучкий
    підхід до синхронізації, ніж традиційний ключове слово synchronized.

    У даному випадку створюється замок ReentrantLock, який є реалізацією інтерфейсу Lock. Цей замок може бути захоплений
    (заблокований) одним потоком, інші потоки будуть очікувати на розблокування.

    Крім того, використовується об'єкт Condition для створення умовної змінної. Умовна змінна (Condition) пов'язується з
    певним замком і надає можливість потокам чекати на певну умову або сигнал перед продовженням виконання.

    У даному випадку замок ReentrantLock створюється за допомогою оператора new ReentrantLock(), а потім використовується
    для створення умовної змінної condition за допомогою методу lock.newCondition(). Клас ReentrantLock надає методи для
    отримання умовних змінних (newCondition()) і для розблокування замку (unlock()).

    У коді потоки використовують lock для захоплення замку перед виконанням критичної секції коду. Критична секція коду
    знаходиться в блоках lock.lock() та lock.unlock(). Умовна змінна condition використовується для сигналізування та
    очікування між потоками. Методи await() та signal() умовної змінної використовуються для блокування та розблокування
    потоків відповідно, у відповідності до визначених умов.

    Таке використання Lock та Condition надає більш гнучкий та масштабований підхід до синхронізації потоків у порівнянні
    з традиційним ключовим словом synchronized.

     */
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

class Runner10_3 {

    private int count = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private void incrementCount() {
        for (int i = 0; i < 10_000; i++) {
            count++;
        }
    }

    public void firstThread() throws InterruptedException {
        lock.lock();
        System.out.println("Waiting ...");
        condition.await();

        try {
            incrementCount();
        } finally {
            lock.unlock();
        }
    }

    public void secondThread() throws InterruptedException {
        Thread.sleep(1000);
        lock.lock();
        System.out.println("Press the enter key to continue ...");
        new Scanner(System.in).nextLine();
        System.out.println("Got it!");

        condition.signal();

        try {
            incrementCount();
        } finally {
            lock.unlock();
        }
    }

    public void finished() {
        System.out.println("Count is = " + count); // Count is = 20000      now it works properly
    }
}