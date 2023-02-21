package com.example.sbmultithreadingexp2;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {

    @RqueueListener(value = "${email.queue.name}") // (1)
    public void sendEmail(Email email) {
        log.info("Email {}", email);
    }

    @RqueueListener(value = "${invoice.queue.name}") // (2)
    public void generateInvoice(Invoice invoice) {
        log.info("Invoice {}", invoice);
    }
}

/*
    
    2) 
    
    Додавати завдання за допомогою Rqueue дуже просто. Нам потрібно анотувати метод за допомогою  RqueueListener. 
    В  RqueuListenerінструкції є кілька полів, які можна налаштувати в залежності від варіанта використання. 
    Встановіть  deadLetterQueueдля надсилання завдань в іншу чергу. Інакше завдання буде відкинуто у разі невдачі. 
    Ми також можемо встановити, скільки разів завдання має бути повторене, використовуючи поле. numRetries
    
 */