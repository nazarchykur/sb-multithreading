package org.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
-----------------------------------------------------------------------------------------------------------------------
-------------------   completableFuture.get()   /  completableFuture.complete("Результат Future")    ------------------
-----------------------------------------------------------------------------------------------------------------------

    1. Найпростіший приклад
        Можна створити CompletableFuture, використовуючи за замовчуванням конструктор:
        CompletableFuture<String> completableFuture = new CompletableFuture <String>() ;
        
    Це найпростіший CompletableFuture, який можна створити. Щоб отримати результат цього CompletableFuture, можна 
    викликати get() :
        String result = completableFuture.get();
    
    Метод get() блокує потік, поки Future не завершиться. Таким чином цей виклик заблокує потік назавжди, тому що Future 
    ніколи не завершується.
    
    Щоб завершити CompletableFuture вручну, можна використати метод complete() :
        completableFuture.complete("Результат Future");
        
    Всі клієнти, які чекають на цей Future, отримають вказаний результат, а подальші виклики completableFuture.complete() 
    будуть ігноруватися.
 */
public class _2_CompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main() starts...");

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
//        String result = completableFuture.get(); // цей виклик заблокує потік назавжди = далі код не виконується
        boolean complete = completableFuture.complete("Результат Future");
        System.out.println("complete = " + complete);

        System.out.println("main() ends...");
    }
    /*
        main() starts...
        complete = true
        main() ends...
     */
}

/*
-----------------------------------------------------------------------------------------------------------------------
------------------------------------------         runAsync()               -------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

    2. Виконання асинхронних завдань із використанням runAsync()
    
    Якщо ви хочете асинхронно виконати деяке фонове завдання, яке не повертає результат, можна використовувати метод 
    CompletableFuture.runAsync() . Він приймає об'єкт Runnable і повертає CompletableFuture<Void> .

 */
class CompletableFuture_runAsync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main() starts..." + Thread.currentThread().getName());

        // Асинхронно запускам задачу, задану об'єктом Runnable
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // Імітація роботи
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("Я буду працювати в окремому потоці, а не в головному. " + Thread.currentThread().getName());
        });

        // Блокування й очікування завершення Future

        /*
            заблокує поточний потік виконання до того моменту, поки результат не буде доступний. Оскільки результатом є
            `Void`, то метод `get()` поверне `null`.
            Таким чином, виклик методу `future.get()` не має сенсу, оскільки ми не очікуємо повернення результату.

            1. Якщо метод `future.get()` не повертає нічого (тобто тип результату `Void`), то він заблокує поточний потік
            виконання до того моменту, поки результат не буде доступний. Інші рядки коду, які знаходяться після виклику
            `future.get()`, не будуть виконуватися, доки результат не буде доступний.

            2. Якщо метод `CompletableFuture` повертає якийсь об'єкт або просто строку, то метод `future.get()` заблокує
            поточний потік виконання до того моменту, поки результат не буде доступний. Після цього метод `get()` поверне
            результат, який повернув метод, що виконався у `CompletableFuture`. Інші рядки коду, які знаходяться після
            виклику `future.get()`, не будуть виконуватися, доки результат не буде доступний.

            3. Якщо станеться помилка, наприклад, виключення буде кинуто в методі, який був переданий в `CompletableFuture`,
            то метод `future.get()` заблокує поточний потік виконання до того моменту, поки результат не буде доступний.
            Після цього метод `get()` кине виключення, яке було кинуто в методі, який був переданий в `CompletableFuture`.
            Інші рядки коду, які знаходяться після виклику `future.get()`, не будуть виконуватися, доки результат не буде доступний.
         */
//        future.get();

        /*
            метод `future.join()` також блокує поточний потік виконання до того моменту, поки результат не буде доступний.
            Інші рядки коду, які знаходяться після виклику `future.join()`, не будуть виконуватися, доки результат не буде доступний.

            Отже, якщо використовувати метод `future.join()` замість `future.get()`, то результат буде доступний так само,
            але виконання коду буде трохи швидше, оскільки метод `join()` не повертає результат і не кидає виключення,
            тому йому не потрібно обробляти ці випадки. Однак, якщо використовувати метод `get()`, то можна обробити
            виключення, які можуть бути кинуті в методі, який був переданий в `CompletableFuture`.
         */
        future.join();

        System.out.println("main() ends..." + Thread.currentThread().getName());
    }
    
    /*
        main() starts...main
        Я буду працювати в окремому потоці, а не в головному. ForkJoinPool.commonPool-worker-1
        main() ends...main
     */
}

/*
-----------------------------------------------------------------------------------------------------------------------
------------------------------------------         supplyAsync()               -------------------------------------------
-----------------------------------------------------------------------------------------------------------------------


    3. Виконання асинхронної задачі та повернення результату з використанням supplyAsync()
    
    CompletableFuture.runAsync() корисний для завдань, які нічого не повертають. Але що, якщо все ж таки потрібно 
    повернути якийсь результат із фонового завдання?
    
    У такому випадку вам допоможе метод CompletableFuture.supplyAsync() . Він приймає Supplier<T> і повертає 
    CompletableFuture<T> , де T це тип значення, що повертається функцією-постачальником:
    // Запуск асинхронної задачі, заданий об’єктом Supplier
            CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
                @Override
                public String get() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                    return "Результат асинхронной задачі";
                }
            });
             // Блокування й очікування завершення Future
            String result = future.get();
            System.out.println(result);
    
    Supplier це функціональний інтерфейс, що представляє постачальника результатів. Він має лише один метод get(),
    в якому можна вказати фонове завдання і повернути результат.

    Нагадаю, можна використовувати лямбда-вирази, щоб скоротити код:

 */

class CompletableFuture_supplyAsync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main() starts... " + Thread.currentThread().getName());

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Результат асинхронної задачі " + Thread.currentThread().getName();
        });

        String result = future.get();
        System.out.println(result);
        System.out.println("main() ends... " + Thread.currentThread().getName());
    }
    
    /*
        main() starts... main
        Результат асинхронної задачі ForkJoinPool.commonPool-worker-1
        main() ends... main
     */
}


/*

Нотатка про пул потоків та Executor

    Ви можете поцікавитися: добре, runAsync() і supplyAsync() виконуються в окремому потоці, але ж ми ніде не 
    створювали новий потік, вірно?
    
    Правильно! CompletableFuture виконує ці завдання у потоці, отриманому з глобального ForkJoinPool.commonPool() .
    
    Також ви можете створити пул потоків і передати його методам runAsync() та supplyAsync() , щоб вони виконували 
    свої завдання у потоці, отриманому вже з вашого пулу потоків.
    
    Всі методи CompletableFuture API представлені у двох варіантах: один приймає Executor як аргумент, а другий ні.
        
        // Варіація методів runAsync() й supplyAsync()
            static CompletableFuture<Void>  runAsync(Runnable runnable)
            static CompletableFuture<Void>  runAsync(Runnable runnable, Executor executor)
            static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
            static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)

 */

class CompletableFuture_WithExecutor {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Executor executor = Executors.newFixedThreadPool(10);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Результат асинхронної задачі";
        }, executor);

        String result = future.get();
//        String result = future.join();
        System.out.println("result = " + result);
    }
}

/*
    
    Перетворення дій з CompletableFuture

        Метод CompletableFuture.get() блокуючий. Він чекає, поки Future завершиться та поверне результат.
        
        Але ж це не те, що нам потрібно, правда? Для побудови асинхронних систем ми повинні мати можливість повісити на 
        CompletableFuture колбек, який автоматично викликається після завершення Future.
        
        Так що нам не потрібно чекати результату і всередині функції-колбека ми зможемо написати логіку, яка відпрацює 
        після завершення Future.
        
        Ви можете повісити колбек на CompletableFuture, використовуючи методи thenApply() , thenAccept() та thenRun() .
    
 */

/*
-----------------------------------------------------------------------------------------------------------------------
------------------------------------------         thenApply()               -------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

    1. thenApply()
    
    Ви можете використовувати метод thenApply() для обробки та перетворення результату CompletableFuture при його 
    надходженні. Як аргумент він приймає Function<T, R> .
    
    Function це теж функціональний інтерфейс, що представляє функцію, яка приймає аргумент типу T і повертає результат типу R:
 */

class CompletableFuture_thenApply {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Створимо CompletableFuture
        CompletableFuture<String> whatsYourNameFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Learner";
        });

        // Добавляємо колбек до Future, використовуючи thenApply()
        CompletableFuture<String> greetingFuture = whatsYourNameFuture.thenApply(name -> "Привіт, " + name);

        //  Блокування й очікування завершення Future
        System.out.println(greetingFuture.get()); // Привіт, Learner
    }
}

/*
    Ви також можете зробити кілька послідовних перетворень за допомогою серії викликів thenApply() . 
    Результат одного thenApply() передається наступному:

 */

class CompletableFuture_thenApply2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                    return "Learner";
                })
                .thenApply(name -> "Привіт, " + name)
                .thenApply(greeting -> greeting + ". Ласкаво просимо до вивчення CalliCoder");

        System.out.println(welcomeText.get()); // Привіт, Learner. Ласкаво просимо до вивчення CalliCoder
    }
}

/*
-----------------------------------------------------------------------------------------------------------------------
--------------------------------        thenAccept() і thenRun()               ----------------------------------------
-----------------------------------------------------------------------------------------------------------------------

    2. thenAccept() і thenRun()
    
        Якщо ви не хочете повертати результат, а просто хочете виконати частину коду після завершення Future, можете 
        скористатися методами thenAccept() і thenRun() . Ці методи є споживачами і часто використовуються як 
        завершальний метод у ланцюжку.
        
        CompletableFuture.thenAccept() приймає Consumer<T> і повертає CompletableFuture<Void> . Він має доступ до 
        результату CompletableFuture, до якого він прикріплений.
                
                class CompletableFuture_thenAccept {
                    public static void main(String[] args) {
                        // Приклад thenAccept()
                        CompletableFuture.supplyAsync(() -> ProductService.getProductDetail(productId))
                            .thenAccept(product -> {
                                System.out.println("Отримана інформація про продукт з віддаленого сервера " + product.getName())
                        });
                    }
                }
        
        На відміну від thenAccept() , thenRun() не має доступу до результату Future. Він приймає Runnable і повертає 
        CompletableFuture<Void> :
        
                // Приклад thenRun()
                CompletableFuture.supplyAsync(() -> {
                    // Виконуємо деякі розрахунки  
                }).thenRun(() -> {
                    // Розрахунки завершені
                });
                
                
 */

/*
-----------------------------------------------------------------------------------------------------------------------
--------------------------------        thenApply()  /  thenApplyAsync()        ---------------------------------------
-----------------------------------------------------------------------------------------------------------------------


    Нотатка про асинхронні колбеки
    
    Всі методи-колбеки в CompletableFuture мають два асинхронні види:
        // Види thenApply()
        <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn)
        <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn)
        <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor)


    Ці асинхронні види колбеків допоможуть розпаралелити завдання, виконавши їх в окремому потоці.
    
 */

class CompletableFuture_thenApply3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Деякий результат";
        }).thenApply(result -> {
            /*
              Виконується в тому ж потоці, де і задача supplyAsync() або в головному потоці, якщо задача supplyAsync() 
              завершиться одразу(щоб перевірити це потрібно видалити sleep())
            */
//            return "Опрацьований результат";
            return result + ". " + "Опрацьований результат";
        });

        System.out.println(future.get());
    }

    /*
        1 return
            "Опрацьований результат"

        2 return
            Деякий результат. Опрацьований результат
     */
}

/*
    У наведеному вище прикладі задача thenApply() виконується в тому ж потоці, де і задача supplyAsync(), або в 
    головному потоці, якщо завдання supplyAsync() завершується досить швидко (спробуйте видалити виклик sleep() для перевірки).
    
    Щоб мати більше контролю над потоком, що виконує завдання, можна використовувати асинхронні колбеки. Якщо ви 
    використовуєте thenApplyAsync() , він буде виконаний в іншому потоці, отриманому з ForkJoinPool.commonPool() :

 */

class CompletableFuture_thenApply4 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Деякий результат " + Thread.currentThread().getName())
                .thenApplyAsync(result -> {
                    // Виконується в другому потоці, взятому з ForkJoinPool.commonPool()
//                    return "Опрацьований результат " + Thread.currentThread().getName();
                    return result + ". Опрацьований результат " + Thread.currentThread().getName();
                });

        System.out.println(future.get());
    }
    /*
        1 return
            Опрацьований результат ForkJoinPool.commonPool-worker-2

        2 return
            Деякий результат ForkJoinPool.commonPool-worker-1. Опрацьований результат ForkJoinPool.commonPool-worker-2
     */
}

/*
    Бачимо що при використанні thenApplyAsync(), завдання буде виконано в іншому потоці, отриманому з пулу потоків Executor.

 */

class CompletableFuture_thenApply5 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Executor executor = Executors.newFixedThreadPool(2);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Деякий результат " + Thread.currentThread().getName();
        }).thenApplyAsync(result -> {
            // Виконується в потоці, отриманому від Executor
//            return "Опрацьований результат " + Thread.currentThread().getName();
            return result + ". Опрацьований результат " + Thread.currentThread().getName();
        }, executor);

        System.out.println(future.get());
    }
    /*
        1 return
            Опрацьований результат pool-1-thread-1

        2 return
            Деякий результат ForkJoinPool.commonPool-worker-1. Опрацьований результат pool-1-thread-1
    */
}

/*
------------------------------------------------------------------------------------------------------------------------
---------------------------------------    Об'єднання двох CompletableFuture    ---------------------------------------
------------------------------------------------------------------------------------------------------------------------
 */

/*
---------------------------------------    Об'єднання двох thenCompose()    ---------------------------------------

    1. Комбінування двох залежних завдань, використовуючи thenCompose()
    
    Припустимо, що ви хочете отримати інформацію про користувача з віддаленого сервісу, і, як інформація буде доступна, 
    отримати кредитний рейтинг користувача вже з іншого сервісу.
    Ось реалізації методів getUserDetail() та getCreditRating() :
    
            CompletableFuture<User> getUsersDetail(String userId) {
                return CompletableFuture.supplyAsync(() -> {
                    UserService.getUserDetails(userId);
                });
            }
     
            CompletableFuture<Double> getCreditRating(User user) {
                return CompletableFuture.supplyAsync(() -> {
                    CreditRatingService.getCreditRating(user);
                });
            }
    
    
    Тепер давайте подивимося, що станеться, якщо ми скористаємося методом thenApply() для досягнення бажаного результату:
    
            CompletableFuture<CompletableFuture<Double>> result = getUserDetail(userId)
                    .thenApply(user -> getCreditRating(user));
    
    
    У попередніх прикладах Supplier, переданий в theApply() , повертав просте значення, але в цьому випадку він повертає 
    CompletableFuture. Отже, кінцевим результатом у наведеному вище прикладі є вкладений CompletableFuture.
    
    Щоб позбутися вкладеного Future, використовуйте метод thenCompose() :
    
            CompletableFuture<Double> result = getUserDetail(userId)
                    .thenCompose(user -> getCreditRating(user));
    
    Правило таке: якщо функція-колбек повертає CompletableFuture, а ви хочете простий результат (а в більшості випадків 
    саме він вам і потрібен), тоді використовуйте thenCompose() .

 */


/*
---------------------------------------    Об'єднання двох thenCombine()    ---------------------------------------

    2. Комбінування двох незалежних завдань з thenCombine()
    
    Якщо thenCompose() використовується для об'єднання двох завдань, коли одне залежить від іншого, то thenCombine() 
    використовується, коли ви хочете, щоб дві задачі працювали незалежно один від одного і по завершенню обох 
    виконувалася якась дія.

 */

class CompletableFuture_thenCombine {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Отримання ваги.");
        CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 70.0;
        });

        System.out.println("Отримання росту.");
        CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 172.5;
        });

        System.out.println("Розрахунок індексу маси тіла.");
        CompletableFuture<Double> combinedFuture = weightInKgFuture
                .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
                    Double heightInMeter = heightInCm / 100;
                    return weightInKg / (heightInMeter * heightInMeter);
                });

        // Колбек, переданий методу theCombine() , викликається, коли обидва завдання завершаться.
        System.out.println("Ваш індекс маси тіла - " + combinedFuture.get());
    }
    
    /*
        Отримання росту.
        Розрахунок індексу маси тіла.
        Ваш індекс маси тіла - 23.52446964923335
     */
}

/*
------------------------------------------------------------------------------------------------------------------------
--------------------------------       Об'єднання кількох CompletableFuture        -------------------------------------
------------------------------------------------------------------------------------------------------------------------

    Ми використовували thenCompose() і thenCombine(), щоб об'єднати два CompletableFuture разом. Але що якщо ви хочете
    об'єднати довільну кількість CompletableFuture? Можна скористатися такими методами:
    
            static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs)
            static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs)
 */


/*
------------------------------------      CompletableFuture.allOf()        ---------------------------------------------

    1. CompletableFuture.allOf()
    
    CompletableFuture.allOf() використовується в тих випадках, коли є список незалежних завдань, які ви хочете запустити 
    паралельно, а після завершення всіх завдань виконати якусь дію.
    
    Припустимо, ви хочете завантажити вміст 100 різних веб-сторінок. Ви можете виконати цю операцію послідовно, але це 
    забере багато часу. Тому ви написали функцію, яка отримує посилання на веб-сторінку та повертає CompletableFuture, 
    тобто завантажує контент сторінки асинхронно:

            CompletableFuture<String> downloadWebPage(String pageLink) {
                return CompletableFuture.supplyAsync(() -> {
                    // Код завантаження та повернення вмісту веб-сторінки
                });
            }
    
    Тепер, коли всі веб-сторінки завантажилися, ви хочете підрахувати кількість сторінок з ключовим словом 
    'CompletableFuture'. Скористайтеся для цього методом CompletableFuture.allOf() :
    
    
            List<String> webPageLinks = Arrays.asList(...) // список із 100 посилань
             
            // Асинхронно завантажуємо вміст усіх веб-сторінок
            List<CompletableFuture<String>> pageContentFutures = webPageLinks.stream()
                    .map(webPageLink -> downloadWebPage(webPageLink))
                    .collect(Collectors.toList());
             
            // Створюємо комбінований Future, використовуючи allOf()
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    pageContentFutures.toArray(new CompletableFuture[0])
            );
    
    Проблема з CompletableFuture.allOf() полягає в тому, що він повертає CompletableFuture<Void>. Але ми можемо отримати
    результати всіх завершених CompletableFuture, дописавши кілька рядків коду:
    
            // Коли всі завдання завершено, викликаємо future.join(), щоб отримати результати та зібрати їх в один список
            CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v -> {
               return pageContentFutures.stream()
                       .map(pageContentFuture -> pageContentFuture.join())
                       .collect(Collectors.toList());
            });
    
    Оскільки ми викликаємо future.join() , коли всі завдання вже завершено, блокування ніде не відбувається 
    
    Метод join() схожий на get(). Єдина відмінність полягає в тому, що він кидає unchecked виняток, якщо CompletableFuture 
    завершується з помилкою.
    
    Давайте тепер підрахуємо кількість веб-сторінок, які містять наше ключове слово:
    
            // Підраховуємо кількість веб-сторінок, що містять ключове слово "CompletableFuture"
            CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents -> {
                return pageContents.stream()
                        .filter(pageContent -> pageContent.contains("CompletableFuture"))
                        .count();
            });
     
            System.out.println("Кількість веб-сторінок із ключовим словом CompletableFuture -" + countFuture.get());
                
 */

/*
------------------------------------      CompletableFuture.anyOf()        ---------------------------------------------

    2. CompletableFuture.anyOf()
    
    CompletableFuture.anyOf() , як випливає з назви, завершується відразу ж, як тільки завершується будь-який із заданих 
    CompletableFuture. Кінцевим результатом буде результат цього першого CompletableFuture, що завершився.


    У наведеному прикладі anyOfFuture завершується, коли завершується будь-яка з трьох CompletableFuture. Оскільки в 
    future2 затримка менше, він завершиться першим, отже, кінцевим результатом буде:
    Результат Future 2 .
    
    CompletableFuture.anyOf() приймає змінну кількість аргументів Future і повертає CompletableFuture<Object> . 
    Проблема CompletableFuture.anyOf() в тому, що якщо у вас є завдання, які повертають результати різних типів, то ви 
    не знатимете тип вашого кінцевого CompletableFuture.
 */

class CompletableFuture_anyOf {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Результат Future 1";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Результат Future 2";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Результат Future 3";
        });

        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);

        System.out.println(anyOfFuture.get()); // Результат Future 2
    }
}

/*
------------------------------------------------------------------------------------------------------------------------
------------------------------      Обробка винятків CompletableFuture        ------------------------------------------
------------------------------------------------------------------------------------------------------------------------

    Ми розглянули, як створити, перетворити та об'єднати CompletableFuture. Тепер розберемося, що робити, якщо щось пішло не так.
    
    Спочатку розглянемо, як помилки поширюються в ланцюжку завдань. Наприклад:
    
                CompletableFuture.supplyAsync(() -> {
                    // Код, який може викинути виняток
                    return "Деякий результат";
                }).thenApply(result -> {
                    return "Опрацьований результат";
                }).thenApply(result -> {
                    return "Результат наступної обробки";
                }).thenAccept(result -> {
                    // Якісь дії з остаточним результатом
                });
                
     Якщо у вихідній задачі supplyAsync() виникне помилка, тоді жодна з наступних задач thenApply() не буде викликана і 
     Future завершиться з винятком. Якщо помилка виникне в першому thenApply(), то всі наступні завдання в ланцюжку не
     будуть запущені і Future так само завершиться з винятком.
                
 */

/*
---------------------------------            exceptionally()              ---------------------------------------------

    1. Обробка винятків з використанням методу exceptionally()
    
    Метод exceptionally() дає можливість оминути можливі помилки, якщо вони є. Можна заблокувати виняток і повернути 
    значення замовчуванням.
    
    Зверніть увагу, що помилка не буде поширюватися далі ланцюжком, якщо ви її опрацюєте.

 */

class CompletableFuture_exceptionally {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Integer age = -1;

        CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
            if (age < 0) {
                throw new IllegalArgumentException("Вік не може мати від'ємне значення");
            }
            if (age > 18) {
                return "Дорослий";
            } else {
                return "Дитина";
            }
        }).exceptionally(ex -> {
            System.out.println("Ой! У нас тут виняток: " + ex.getMessage());
            return "Невідомо!";
        });

        System.out.println("Зрілість: " + maturityFuture.get());
    }
    /*
        Ой! У нас тут виняток: java.lang.IllegalArgumentException: Вік не може мати від'ємне значення
        Зрілість: Невідомо!
     */
}

/*
-------------------------------------        handle()          --------------------------------------------------------

    2. Опрацювання винятків із використанням методу handle()
    
    Для відновлення після винятків API також надає загальніший метод handle(). 
    Він викликається незалежно від цього, виник виняток чи ні.
    
    Якщо виникає виняток, аргумент res буде null, якщо не виникне, то ex буде null.
 */

class CompletableFuture_handle {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Integer age = -1;

        CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
            if (age < 0) {
                throw new IllegalArgumentException("Вік не може мати від'ємне значення");
            }
            if (age > 18) {
                return "Дорослий";
            } else {
                return "Дитина";
            }
        }).handle((res, ex) -> {
            if (ex != null) {
                System.out.println("Ой! У нас тут виняток - " + ex.getMessage());
                return "Невідомо!";
            }
            return res;
        });

        System.out.println("Зрілість: " + maturityFuture.get());
    }
    /*
        Ой! У нас тут виняток - java.lang.IllegalArgumentException: Вік не може мати від'ємне значення
        Зрілість: Невідомо!
     */
}