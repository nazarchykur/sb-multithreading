package com.example.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultithreadingPart12 {
    public static void main(String[] args) throws InterruptedException {
//        Semaphore semaphore = new Semaphore(1);
////        semaphore.release();
//        semaphore.acquire();
//        System.out.println("Available permits: " + semaphore.availablePermits());

//        Connection.getInstance().connect();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 200; i++) {
            executorService.submit(() -> {
                Connection.getInstance().connect();
            });
        }

        executorService.shutdown();

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}

class Connection {
    private static Connection instance = new Connection();
    private int countOfConnections = 0;

    private Connection() {
    }

    public static Connection getInstance() {
        return instance;
    }

    public void connect() {
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