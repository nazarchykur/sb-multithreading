package com.example.multithreading;

import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MultithreadingPart13 {

    /*
    Інтерфейси Callable і Runnable є частинами Java API для роботи з багатопотоковістю і використовуються для представлення
    завдань, які можна виконати в окремих потоках.

    Інтерфейс Runnable є найпростішим і містить один метод run(), який не повертає результатів. Цей метод визначає код,
    який потрібно виконати в окремому потоці. Основна мета Runnable - запускати асинхронні завдання.

    Наприклад, ось приклад використання Runnable з ExecutorService:
            public class MyRunnable implements Runnable {
                @Override
                public void run() {
                    // Виконується код завдання
                    System.out.println("Hello from Runnable!");
                }
            }

            public class Main {
                public static void main(String[] args) {
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    executorService.execute(new MyRunnable());
                    executorService.shutdown();
                }
            }

    Інтерфейс Callable є аналогом Runnable, але з деякими відмінностями. Основна різниця в тому, що метод call() інтерфейсу
    Callable повертає значення, яке можна отримати за допомогою об'єкта Future. Це дозволяє отримати результат виконання
    завдання або обробити винятки, якщо вони сталися.

    Ось приклад використання Callable з ExecutorService:

            public class CallableExample {
            public static void main(String[] args) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();

                        Callable<Integer> task = () -> {
                            // Код виконавчого завдання
                            return 42;
                        };

                        Future<Integer> future = executorService.submit(task);

                        try {
                            Integer result = future.get(); // Очікуємо на результат
                            System.out.println("Результат: " + result);
                        } catch (Exception e) {
                            System.err.println("Сталася помилка: " + e.getMessage());
                        }

                        executorService.shutdown();
                    }
                }

    У цьому прикладі ми створюємо ExecutorService з одним потоком за допомогою методу newSingleThreadExecutor(). Далі
    створюємо об'єкт Callable із визначеним в ньому кодом виконавчого завдання, яке повертає число 42. Ми викликаємо метод
    submit(Callable<T> task) ExecutorService, щоб відправити завдання на виконання. В результаті отримуємо об'єкт Future,
    який представляє майбутній результат виконання завдання.

    Ми використовуємо метод get() об'єкта Future, щоб отримати результат виконання завдання. Якщо завдання ще не завершилося,
    цей виклик буде блокувати поточний потік до отримання результату. Якщо під час виконання завдання сталася помилка, то
    виклик get() кине виняток, який ми можемо обробити у блоці catch. В іншому випадку, ми виводимо отриманий результат.

    На завершення, ми викликаємо метод shutdown() ExecutorService, щоб закрити його і зупинити виконання завдань.

     */

    /*
    Об'єкт Future є частиною Java Concurrency API і використовується для отримання результатів виконання асинхронних задач,
    які були відправлені на виконання за допомогою ExecutorService або CompletionService. Він представляє результат
    асинхронного обчислення або його поточний статус.

    Основні методи, які можна використовувати з об'єктом Future, включають:

        - boolean isDone(): Повертає true, якщо завдання, пов'язане з цим Future, вже виконано, інакше повертає false.

        - boolean isCancelled(): Повертає true, якщо завдання, пов'язане з цим Future, було скасоване перед його завершенням.

        - boolean cancel(boolean mayInterruptIfRunning): Скасовує завдання, пов'язане з цим Future.
            Параметр mayInterruptIfRunning визначає, чи потрібно перервати виконання завдання, якщо воно вже виконується.
            Якщо завдання вже завершене або скасоване, то цей метод повертає false. Якщо завдання було успішно скасоване,
            то повертається true.

        - V get() throws InterruptedException, ExecutionException: Очікує на завершення виконання завдання і повертає його
            результат. Якщо завдання ще не завершене, виклик цього методу блокує виконуючий потік до отримання результату.
            Якщо виникає помилка під час виконання завдання, то викидається ExecutionException. Якщо під час очікування
            на результат виконання викликають метод get(), і цей потік переривається, то викидається InterruptedException.

        - V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException: Очікує на
            завершення виконання завдання протягом певного часового інтервалу, вказаного параметрами timeout та unit.
            Якщо виконання завдання не завершено протягом цього інтервалу, викидається TimeoutException.

    Ці методи дозволяють отримувати результати виконання асинхронних завдань, перевіряти їх статус та скасовувати їх в разі
    потреби. Користуючись об'єктами Future, ви можете більш гнучко керувати виконанням потоків та отримувати результати
    асинхронних обчислень відповідно до вашої потреби.

     */

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        // implement Callable interface implicitly
//        Future<Integer> future = executorService.submit(new Callable<Integer>() {
//
//            @Override
//            public Integer call() throws Exception {
//                Random random = new Random();
//                int duration = random.nextInt(4000);
//                System.out.println("Starting ...");
//                Thread.sleep(duration);
//                System.out.println("Finished ...");
//                return duration;
//            }
//        });

        // using Lambda expression instead of using Callable in this case
        Future<Integer> future = executorService.submit(() -> {
            Random random = new Random();
            int duration = random.nextInt(4000);

            if (duration > 2000) {
                throw new RuntimeException("Duration is too long");
            }

            System.out.println("Starting ...");
            Thread.sleep(duration);
            System.out.println("Finished ...");
            return duration;
        });

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        try {
            System.out.println("Result: " + future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }

        /*
        if all is OK

            Starting ...
            Finished ...
            Result: 3315
         */

        /*
            if (duration > 2000) {
                throw new RuntimeException("Duration is too long");
            }

            java.lang.RuntimeException: Duration is too long
         */
    }
}
