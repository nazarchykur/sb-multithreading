package com.example.multithreading;

import java.util.Scanner;

public class MultithreadingPart8 {

    /*
    Методи wait() і notify() є частинами механізму синхронізації в Java, який базується на концепції "спостерігач-споживач"
    (observer-consumer). Вони використовуються для забезпечення спільної роботи потоків та координації взаємодії між ними.

    Методи wait() і notify() визначені в класі Object, що є базовим класом для всіх об'єктів в Java.

        1. wait(): Метод wait() використовується для призупинення виконання потоку і звільнення блокування ресурсів,
            пов'язаних з цим об'єктом. Виклик wait() робить потік очікувати до тих пір, поки інший потік не викличе метод
            notify() або notifyAll() для того самого об'єкта. Цей механізм дозволяє потокам ефективно співпрацювати,
            чекаючи на певну умову перед продовженням виконання.

        2. notify(): Метод notify() використовується для повідомлення очікуючим потокам, які викликали wait(), що стан
            об'єкта може бути змінений і їм можна спробувати отримати блокування ресурсів знову. Метод notifyAll()
            повідомляє всі потоки, що чекають на цьому об'єкті, а не лише один конкретний потік.

    Звичайно, використання wait() і notify() вимагає деяких обов'язкових кроків та рекомендацій:

        1. Виклик методу wait() та notify() має бути розміщений всередині синхронізованого блоку (метода) на тому самому об'єкті.

        2. Потік, що викликає wait(), повинен володіти блокуванням (monitor) цього об'єкта.

        3. Щоб інші потоки могли продовжити своє виконання після виклику notify(), потік, що викликає notify(), повинен
            звільнити блокування (monitor) об'єкта.

        4. Виклик notify() не гарантує, який саме потік продовжить виконання. Це залежить від планувальника потоків.

        5. Краще використовувати notifyAll() замість notify(), якщо необхідно повідомити всі очікуючі потоки, щоб уникнути
            проблеми зі стоянням (локальним стоянням).

    Застосування wait() і notify() забезпечує можливість синхронізації потоків та контролю за їх взаємодією. Це особливо
    корисно, коли один потік має виконати певну умову перед тим, як інший потік може продовжити виконання або виконати
    певну дію. Такий механізм допомагає уникнути гонок потоків і забезпечує правильну послідовність взаємодії між потоками.

     */

    public static void main(String[] args) {
        Processor8 processor = new Processor8();

        Thread t1 = new Thread(() -> {
            try {
                processor.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                processor.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Processor8 {
    public void produce() throws InterruptedException {
        synchronized (this) {
            System.out.println("Producer thread is running");
            wait();
            System.out.println("Resumed.");
        }
    }

    public void consume() throws InterruptedException {
        System.out.println("Consumer thread is running");
        Scanner sc = new Scanner(System.in);
        Thread.sleep(2000);

        synchronized (this) {
            System.out.println("Waiting for return key");
            sc.nextLine();
            System.out.println("Return key passed.");
            notify();
        }
        System.out.println("Consumer thread is running");
    }
}
