package com.example.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MultithreadingPart12_2 {

    /*
    Semaphore (Семафор) - це синхронізаційний примітив, що використовується для контролю доступу до обмеженої кількості
    ресурсів у багатопотоковому середовищі. Він дозволяє обмежувати кількість потоків, які можуть одночасно отримати
    доступ до певного ресурсу або виконувати певну операцію.

    Основні методи класу Semaphore:

        - acquire(): Цей метод викликається потоком, щоб отримати доступ до ресурсу, який контролюється семафором.
            Якщо семафор має вільні ресурси, потік отримує доступ до них. Якщо всі ресурси семафора використані, потік
            буде заблокований до тих пір, поки якийсь інший потік не звільнить ресурси за допомогою методу release().

        - release(): Цей метод викликається потоком, щоб повернути ресурси семафору. Він звільняє ресурси, які раніше
            були отримані за допомогою методу acquire(), дозволяючи іншим потокам отримати доступ до цих ресурсів.

        - tryAcquire(): Цей метод перевіряє, чи може потік отримати доступ до ресурсу. Він негайно повертає true, якщо
            ресурс доступний і успішно отриманий потоком, або false, якщо ресурс використовується іншим потоком. Він не
            блокує потік, якщо ресурс недоступний.

        - tryAcquire(long timeout, TimeUnit unit): Цей метод працює так само, як tryAcquire(), але додає можливість задати
            таймаут очікування. Потік спробує отримати доступ до ресурсу протягом певного часового інтервалу, після чого
            поверне результат true або false, в залежності від того, чи вдалося отримати доступ.

    Ці методи дозволяють потокам взаємодіяти з семафором, керуючи доступом до обмежених ресурсів і забезпечуючи правильну
    синхронізацію в багатопотоковому середовищі.

     */

    /*
    Цей код демонструє використання семафора для обмеження кількості одночасних з'єднань до ресурсу (у цьому випадку -
    до об'єкту Connection_2).

    У методі main створюється ExecutorService з використанням Executors.newCachedThreadPool(). Це дозволяє виконувати
    завдання у вигляді окремих потоків. В циклі 200 разів викликається метод submit() для виконання анонімної функції,
    яка викликає метод connect() на об'єкті Connection_2. Це означає, що 200 потоків одночасно намагатимуться підключитися
    до ресурсу.

    Об'єкт Connection_2 має приватний конструктор і використовує шаблон Singleton, тому єдиний екземпляр цього класу можна
    отримати за допомогою методу getInstance(). У конструкторі створюється семафор з обмеженням в 10 ресурсів (з'єднань).
    Це означає, що одночасно можуть бути використані лише 10 з'єднань, а інші потоки мають зачекати, поки ресурси стануть
    доступними.

    Метод connect() спочатку викликає acquire() на семафорі, щоб отримати доступ до ресурсу (з'єднання). Якщо всі ресурси
    зайняті, потік буде заблокований до тих пір, поки якесь з'єднання не звільниться. Після отримання доступу викликається
    метод doConnect(), який виконує функціонал підключення (у цьому випадку просто виводить повідомлення про встановлене
    з'єднання). Після завершення підключення викликається release() на семафорі, щоб повернути ресурс.

    Метод doConnect() спочатку збільшує локальну змінну countOfConnections, яка показує кількість поточних з'єднань.
    Потім він чекає 2 секунди (симулює довготривалу операцію підключення) і зменшує countOfConnections. Усі ці дії
    виконуються взаємовиключно за допомогою блоку synchronized, щоб уникнути конкуренції між потоками при зміні
    countOfConnections.

    На виводі ви побачите повідомлення про встановлення з'єднання, кількість поточних з'єднань після кожного підключення
    та після його закриття.

    Загалом, цей код демонструє використання семафора для обмеження доступу до обмежених ресурсів та правильного управління
    багатопотоковою ситуацією.

     */

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 200; i++) {
            executorService.submit(() -> {
                Connection_2.getInstance().connect();
            });
        }

        executorService.shutdown();

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}

class Connection_2 {
    private static Connection_2 instance = new Connection_2();

    private Semaphore semaphore = new Semaphore(10);
    private int countOfConnections = 0;

    private Connection_2() {
    }

    public static Connection_2 getInstance() {
        return instance;
    }

    public void connect() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            doConnect();
        } finally {
            semaphore.release();
        }
    }
    public void doConnect() {
        System.out.println("Connection established");

        synchronized (this) {
            countOfConnections++;
            System.out.println("Count of connections: " + countOfConnections);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        synchronized (this) {
            countOfConnections--;
            System.out.println("Count of connections: " + countOfConnections);
        }
    }
}
