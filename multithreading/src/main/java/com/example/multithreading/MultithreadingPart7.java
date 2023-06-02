package com.example.multithreading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultithreadingPart7 {
    /*
    Одним з популярних механізмів синхронізації для взаємодії між виробником (producer) і споживачем (consumer) в
    мультипоточних програмах є BlockingQueue. Він забезпечує безпечну передачу даних між потоками та автоматично керує
    блокуванням і розблокуванням, коли черга стає порожньою або повною.

    У цьому прикладі ми використовуємо LinkedBlockingQueue, який є реалізацією BlockingQueue на основі зв'язаного списку.
    Виробник генерує числа від 1 до 10 і поміщає їх у чергу, використовуючи метод put(). Споживач бере числа з черги,
    використовуючи метод take() і виводить їх на екран.

    У даному прикладі черга має розмір 5. Якщо черга стає повною, put() метод блокує виробника і чекає, поки звільниться
    місце. Аналогічно, якщо черга порожня, take() метод блокує споживача і чекає, поки з'явиться новий елемент.

    Таким чином, виробник і споживач автоматично синхронізуються через BlockingQueue, не потребуючи власноручного
    керування потоками. Це спрощує роботу з взаємодією між потоками та забезпечує безпечну передачу даних.

    Варто зауважити, що в реальних сценаріях можуть бути додаткові перевірки на стан черги, обробка виключень та інші
    механізми безпеки. Проте, основна концепція використання BlockingQueue для спілкування між виробником і споживачем
    залишається незмінною.

     */
    public static void main(String[] args) {
        System.out.println("Main thread started");
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        final int totalItems = 10;

        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= totalItems; i++) {
                    queue.put(i);
                    System.out.println("Producer produced: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                int consumedItems = 0;
                while (consumedItems < totalItems) {
                    int number = queue.take();
                    System.out.println("Consumer consumed: " + number);
                    consumedItems++;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Main thread finished");
    }
}
