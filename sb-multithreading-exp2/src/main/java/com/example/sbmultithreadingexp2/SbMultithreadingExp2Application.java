package com.example.sbmultithreadingexp2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableRedisRepositories
@EnableWebMvc
public class SbMultithreadingExp2Application {

    public static void main(String[] args) {
        SpringApplication.run(SbMultithreadingExp2Application.class, args);
    }

}


/*

    1)
    
    Ми збираємося використовувати бібліотеку  Rqueue  для виконання будь-яких завдань із довільною затримкою. 
    Rqueue - це заснований на Spring виконавець асинхронних завдань, який може виконувати завдання з будь-якою 
    затримкою, побудований на бібліотеці обміну повідомленнями Spring і підтримується Redis.
    
    Ми додамо залежність spring boot starter для  Rqueue com.github.sonus21:rqueue-spring-boot-starter:3.0.1-RELEASE

 */