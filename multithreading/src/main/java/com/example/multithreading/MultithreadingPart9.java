package com.example.multithreading;

import java.util.LinkedList;
import java.util.Random;

public class MultithreadingPart9 {
    
    /*
    Цей код є прикладом використання багатопотоковості (multithreading) в Java. Він демонструє вирішення проблеми 
    виробник-споживач (producer-consumer problem) за допомогою синхронізації і спільного використання об'єкту-блокувальника.

    Код має дві основні класи: MultithreadingPart9 і Processor9.
    
    Клас MultithreadingPart9 містить метод main, який є точкою входу в програму. В цьому методі створюються два потоки 
    (t1 і t2), які виконуватимуть різні дії. Потім запускаються ці потоки (t1.start() і t2.start()) і головний потік (main) 
    чекає, доки ці потоки завершаться (t1.join() і t2.join()). Це забезпечує послідовну виконання дій у потоках.
    
    Клас Processor9 має два методи: produce і consume. Метод produce відповідає за вироблення даних і додавання їх до 
    списку list, який представляє загальний ресурс. Метод consume відповідає за споживання даних зі списку list.
    
    У обох методах використовується механізм синхронізації з допомогою ключового слова synchronized. Це забезпечує, що 
    тільки один потік може одночасно виконувати код, оточений блоком synchronized, для даного об'єкта-блокувальника (lock).
    
    У методі produce є основний цикл, який виконується MAX_VALUE разів. Кожного разу, коли список list досягає максимального 
    розміру (MAX_SIZE_LIST), потік викликає lock.wait() і переходить у режим очікування, відпускаючи блокувальник lock. 
    Коли інший потік викликає lock.notifyAll(), цей потік знову переходить до виконання і додає значення до списку list. 
    Після додавання значення потік викликає lock.notifyAll(), щоб повідомити інші потоки, які можуть бути в стані очікування, 
    що список був змінений.
    
    У методі consume також є цикл, який виконується MAX_VALUE разів. Кожного разу, коли список list порожній, потік 
    викликає lock.wait() і переходить у режим очікування. Коли інший потік викликає lock.notifyAll(), цей потік знову 
    переходить до виконання, виводить розмір списку та перший елемент, видаляє його зі списку і викликає lock.notifyAll(), 
    щоб повідомити інші потоки, які можуть бути в стані очікування, що список був змінений.
    
    У методі consume також є додатковий виклик Thread.sleep(random.nextInt(1000)), що спричиняє затримку потоку на 
    випадковий проміжок часу (до 1 секунди) після кожного видалення значення зі списку. Це демонструє можливість різних 
    проміжків часу між споживачами.
     */

    public static void main(String[] args) {
        Processor9 processor = new Processor9();

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

class Processor9 {
    private LinkedList<Integer> list = new LinkedList<>();
    private final int MAX_SIZE_OF_LIST = 10;
    private final int MAX_VALUE_OF_ITERATION = 20;

    private final Object lock = new Object();

    public void produce() throws InterruptedException {
        int value = 0;
        int i = 0;
        while (i < MAX_VALUE_OF_ITERATION) {
            synchronized (lock) {
                while (list.size() == MAX_SIZE_OF_LIST) {
                    lock.wait();
                }
                list.add(value++);

                lock.notifyAll();
                i++;
            }
        }
    }

    public void consume() throws InterruptedException {
        Random random = new Random();
        int i = 0;
        while (i < MAX_VALUE_OF_ITERATION) {
            synchronized (lock) {
                while (list.isEmpty()) {
                    lock.wait();
                }

                System.out.print("List size is = " + list.size());
                Integer value = list.removeFirst();
                System.out.println("; value is = " + value);
                lock.notifyAll();
            }
            Thread.sleep(random.nextInt(1000));
            i++;
        }
    }
}
