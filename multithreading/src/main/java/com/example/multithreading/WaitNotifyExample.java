package com.example.multithreading;

public class WaitNotifyExample {

    /*

    Використання методів wait() і notify() безпосередньо в Spring Boot не є типовим підходом до управління багатопотоковістю.
    Spring Boot надає розширені можливості для керування багатопотоковістю та асинхронним виконанням, які спрощують розробку
    сучасних додатків.

    Основні підходи та методики, які можна використовувати в Spring Boot для керування багатопотоковістю, включають:

        1. Анотації асинхронності: Spring Framework надає анотації, такі як @Async, які дозволяють позначити методи як
            асинхронні. Це дозволяє виконувати ці методи в окремих потоках без прямого керування потоками.

        2. Завдання пулу потоків: Spring Framework надає можливість налаштування пулу потоків за допомогою конфігураційних
            параметрів. Це дозволяє контролювати кількість потоків, які використовуються для обробки асинхронних завдань.

        3. CompletableFuture: Це клас з Java 8, який надає зручні методи для асинхронного виконання та обробки результатів.
            Він інтегрований з Spring Framework і може бути використаний для реалізації асинхронних операцій.

        4. Reactor: Reactor є реактивною бібліотекою, яка дозволяє реалізувати асинхронні, не блокуючі операції у Spring
            Boot додатках. Вона підтримує структури даних, такі як Mono і Flux, які можна використовувати для обробки
            асинхронних потоків подій.

    Ці методики є більш сучасними та ефективними для управління багатопотоковістю в Spring Boot, оскільки вони забезпечують
    високорівневі абстракції та інструменти, які спрощують розробку та уникнення потенційних проблем з багатопотоковістю,
    таких як гонки за ресурсами або дедлоки.

    Таким чином, використання методів wait() і notify() безпосередньо в Spring Boot не є рекомендованим. Замість цього,
    краще використовувати сучасні підходи та інструменти, які надаються фреймворком для ефективного керування багатопотоковістю.

     */
    public static void main(String[] args) {
        final Object lock = new Object();

        Thread producerThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    while (!isConditionSatisfied()) {
                        System.out.println("Producer is waiting...");
                        lock.wait();
                    }
                    System.out.println("Producer has been notified!");
                    System.out.println("Producer is producing data...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread consumerThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Consumer is performing some tasks...");
                setCondition(true);
                lock.notify();
            }
        });

        producerThread.start();
        consumerThread.start();
    }

    private static boolean condition = false;

    private static boolean isConditionSatisfied() {
        return condition;
    }

    private static void setCondition(boolean value) {
        condition = value;
    }
}
