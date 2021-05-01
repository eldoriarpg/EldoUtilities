package de.eldoria.eldoutilities.utils;

public class Stopwatch {
    long lastStart;
    long lastStop;

    public void start() {
        lastStart = System.currentTimeMillis();
    }

    public void stop() {
        lastStop = System.currentTimeMillis();
    }

    public void print(String message) {
        long duration = lastStop - lastStart;
        System.out.printf("%s: %.4f%n", message, duration / 1000d);
    }

    public void printAndRestart(String message) {
        stop();
        print(message);
        start();
    }
}
