package com.example.multithreading;

import java.util.Scanner;

public class SomeProcessorMain {
    public static void main(String[] args) {
        SomeProcessor someProcessor = new SomeProcessor();
        someProcessor.start();

        System.out.println("Press enter to stop...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        someProcessor.stopProcessor();
    }
}

class SomeProcessor extends Thread {
    private boolean isRunning = true;
    @Override
    public void run() {
        while (isRunning) {
            System.out.println("hello");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopProcessor() {
        isRunning = false;
    }
}