package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
    Обмеження Future

    1. Його не можна завершити вручну.
    
        Допустимо, ви написали функцію отримання актуальної ціни товару з віддаленого API. Оскільки цей виклик API займає 
        багато часу, ви запускаєте його в окремому потоці та повертаєте Future з функції.
        
        Тепер припустимо, що віддалений сервіс припинив працювати і ви хочете завершити Future вручну, передавши актуальну 
        ціну продукту з кешу.
        
        Чи зможете ви зробити це з Future? Ні!
    
    
    2. Не можна виконувати подальші дії над результатом Future без блокування.
        
        Future не повідомляє про своє завершення. У ньому є метод get(), який блокує потік до того часу, поки результат 
        стане доступним.
        
        Також у Future не можна повісити функцію-колбек, щоб вона спрацьовувала автоматично, як тільки стане доступний 
        результат.
    
    3. Неможливо виконати безліч Future один за одним.
    
        Трапляються випадки, коли потрібно виконати тривалу операцію і після її завершення передати результат іншої 
        тривалої операції тощо.
        
        Такий алгоритм асинхронної роботи неможливий під час використання Future.
        
    4. Неможливо поєднати кілька Future.
    
        Припустимо, що у вас є 10 різних завдань у Future, які ви хочете запустити паралельно, і як тільки всі вони 
        завершаться, викликати певну функцію. З Future ви не можете зробити та це.
    
    5. Немає обробки винятків.

        Future API немає механізму обробки винятків.
    
    
    
    Ось так багато обмежень! Саме тому в нас і з'явився CompletableFuture. 
    З його допомогою можна досягти всього перерахованого вище.
    
    
    CompletableFuture реалізує інтерфейси Future та CompletionStage і надає величезний набір зручних методів для створення 
    та об'єднання кількох Future. Він також має повноцінну підтримку обробки винятків.

 */

public class _1_WhyNotFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main() starts ...");
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Future<List<Integer>> listFuture = executorService.submit(() -> {
            System.out.println("inside future ...: " + Thread.currentThread().getName());
            delayInSecond(5); // imitate some work when calling 3rd service
//            System.out.println(1 / 0); // ArithmeticException: / by zero
            return Arrays.asList(1, 2, 3, 4, 5);
        });
        
        // 1. немаэ методу щоб зупинити Future примусово
        
        List<Integer> integers = listFuture.get(); // на цьому етапі потік буде зупинено поки не отримаємо результат
        System.out.println("integers = " + integers);
        
        System.out.println("main() ends ...");
        
        /*
           2. Не можна виконувати подальші дії над результатом Future
             немає якихось інших методів, щоб ми могли, наприклад зробити наступну дію з отриманими "integers"
             щось як integers.doNextFutureOperation()
         */
        
        
        /*
            4. Неможливо поєднати кілька Future.
            
                Future<List<Integer>> listFuture1 = executorService.submit(() -> Arrays.asList(1, 2, 3, 4, 5));
                Future<List<Integer>> listFuture2 = executorService.submit(() -> Arrays.asList(1, 2, 3, 4, 5));
                Future<List<Integer>> listFuture3 = executorService.submit(() -> Arrays.asList(1, 2, 3, 4, 5));
                
                немає такого щоб зробити listFuture1 + listFuture2 + listFuture3
                
                тільки конкретно викликати 
                    listFuture1.get();
                    listFuture2.get();
                    listFuture3.get();
         */
        
        /*
            5. Немає обробки винятків.
            
                    Future<List<Integer>> listFuture = executorService.submit(() -> {
                        System.out.println("inside future ...: " + Thread.currentThread().getName());
                        System.out.println(1 / 0);  // <= ArithmeticException
                        return Arrays.asList(1, 2, 3, 4, 5);
                    });
                    
                main() starts ...
                inside future ...: pool-1-thread-1
                Exception in thread "main" java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
                    at java.base/java.util.concurrent.FutureTask.report(FutureTask.java:122)
                    at java.base/java.util.concurrent.FutureTask.get(FutureTask.java:191)      
         */

    }

    private static void delayInSecond(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}